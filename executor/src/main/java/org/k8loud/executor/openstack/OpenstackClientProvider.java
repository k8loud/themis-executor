package org.k8loud.executor.openstack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Synchronized;
import lombok.extern.slf4j.Slf4j;
import org.k8loud.executor.exception.OpenstackException;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.client.IOSClientBuilder;
import org.openstack4j.api.exceptions.AuthenticationException;
import org.openstack4j.api.exceptions.ConnectionException;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Token;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.k8loud.executor.exception.code.OpenstackExceptionCode.AUTHENTICATION_ERROR;
import static org.openstack4j.api.OSClient.OSClientV3;

@RequiredArgsConstructor
@Getter
@Slf4j
public class OpenstackClientProvider {
    @Autowired
    private final OpenstackProperties openstackProperties;
    private Token token = null;

    public boolean isTokenActive() {
        if (token == null) {
            log.debug("Token is not set");
            return false;
        }

        return isTokenExpiringSoon(token);
    }

    @Synchronized
    @Retryable(retryFor = ConnectionException.class, backoff = @Backoff(delay = 100))
    public OSClientV3 getClientFromToken() throws OpenstackException {
        if (!isTokenActive()) {
            token = createOpenstackToken();
        }

        log.debug("Creating client from token ");
        return OSFactory.clientFromToken(token);
    }

    private boolean isTokenExpiringSoon(Token token) {
        // is token expiring within next 30 minutes
        boolean isTokenExpiring = token.getExpires()
                .before(Date.from(Instant.now()
                        .minus(30, ChronoUnit.MINUTES)));
        if (isTokenExpiring) {
            log.debug("Token is expiring. Expiry date is {}", token.getExpires());
        }
        return isTokenExpiring;
    }

    private Token createOpenstackToken() throws OpenstackException {
        IOSClientBuilder.V3 clientBuilder = OSFactory.builderV3()
                .endpoint(openstackProperties.getEndpoint());

        switch (openstackProperties.getApiConfig()) {
            case UNSCOPED ->
                    clientBuilder.credentials(openstackProperties.getUsername(), openstackProperties.getPassword(),
                            Identifier.byName(openstackProperties.getDomainName()));
            case DOMAIN_SCOPED ->
                    clientBuilder.credentials(openstackProperties.getUsername(), openstackProperties.getPassword())
                            .scopeToDomain(Identifier.byId(openstackProperties.getDomainID()));
            case PROJECT_SCOPED ->
                    clientBuilder.credentials(openstackProperties.getUsername(), openstackProperties.getPassword(),
                                    Identifier.byName(openstackProperties.getDomainName()))
                            .scopeToProject(Identifier.byId(openstackProperties.getProjectID()));
        }

        OSClient.OSClientV3 client;
        try {
            log.debug("Authenticating Openstack client...");
            client = clientBuilder.authenticate();
        } catch (AuthenticationException authenticationException) {
            log.error("Failed to authenticate Openstack. ", authenticationException);
            throw new OpenstackException(authenticationException, AUTHENTICATION_ERROR);
        }

        return client.getToken();
    }
}

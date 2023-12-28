package org.k8loud.executor.command;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
import org.apache.commons.codec.binary.Base64;
import org.jetbrains.annotations.NotNull;
import org.k8loud.executor.exception.CommandException;
import org.k8loud.executor.exception.ValidationException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;

import static org.k8loud.executor.exception.code.CommandExceptionCode.FAILED_TO_EXECUTE_COMMAND;
import static org.k8loud.executor.util.Util.resultMap;

@Slf4j
@Service
public class CommandExecutionServiceImpl implements CommandExecutionService {
    private static final int COMMAND_EXIT_CHECK_SLEEP_MS = 100;

    public CommandExecutionServiceImpl() {
        // Fixes https://stackoverflow.com/questions/6559272/algid-parse-error-not-a-sequence
        // But maybe it's not the best approach
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );
    }

    @Override
    public Map<String, Object> executeCommand(@NotNull String host, @NotNull Integer port, @NotNull String privateKey,
                                              @NotNull String user,
                                              @NotNull String command) throws CommandException, ValidationException {
        try (SSHClient client = initClient(host, port, privateKey, user);
             Session session = client.startSession()) {
            log.info("Executing `{}`", command);
            final Command commandObj = session.exec(command);
            do {
                try {
                    Thread.sleep(COMMAND_EXIT_CHECK_SLEEP_MS);
                } catch (InterruptedException e) {
                    log.warn("Interrupted while waiting for command exit");
                }
            } while (!commandObj.isEOF());
            String stdout = IOUtils.readFully(commandObj.getInputStream()).toString();
            String stderr = IOUtils.readFully(commandObj.getErrorStream()).toString();
            Integer exitStatus = commandObj.getExitStatus();
            commandObj.close();
            log.info("exit status: {}, stdout: `{}`, stderr: `{}`", exitStatus, stdout, stderr);
            return resultMap(String.format("Successfully executed '%s'", command),
                    Map.of("exitStatus", exitStatus, "stdout", stdout, "stderr", stderr));
        } catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CommandException(FAILED_TO_EXECUTE_COMMAND);
        }
    }

    private SSHClient initClient(String host, Integer port, String privateKey, String user) throws IOException,
            NoSuchAlgorithmException, InvalidKeySpecException {
        final SSHClient client = new SSHClient();
        // https://stackoverflow.com/questions/7873909/dealing-with-host-key-not-verifiable-could-not-verify-ssh-rsa-host-key-with
        client.addHostKeyVerifier(new PromiscuousVerifier());
        PrivateKey privateKeyObj = loadPrivateKey(privateKey);
        PublicKey publicKeyObj = getPublicKey(privateKeyObj);
        KeyPair keyPair = new KeyPair(publicKeyObj, privateKeyObj);
        KeyProvider keyProvider = client.loadKeys(keyPair);
        log.info("Connecting to {}:{} as {}", host, port, user);
        client.connect(host, port);
        client.authPublickey(user, keyProvider);
        return client;
    }

    PrivateKey loadPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] decoded = Base64.decodeBase64(privateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    PublicKey getPublicKey(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        RSAPrivateCrtKey privateCrtKey = (RSAPrivateCrtKey) privateKey;
        RSAPublicKeySpec publicKeySpec = new java.security.spec.RSAPublicKeySpec(privateCrtKey.getModulus(),
                privateCrtKey.getPublicExponent());
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(publicKeySpec);
    }
}

package org.k8loud.executor.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class CommandExecutionServiceImplTest {
    CommandExecutionServiceImpl commandExecutionService;

    @BeforeEach
    public void setUp() {
        commandExecutionService = new CommandExecutionServiceImpl();
    }

    @Test
    void testGenerationOfPublicKeyFromPrivate() throws NoSuchAlgorithmException, InvalidKeySpecException,
            SignatureException, InvalidKeyException {
        // given
        String privateKey = getPrivateKey();

        // when
        PrivateKey privateKeyObj = commandExecutionService.loadPrivateKey(privateKey);
        PublicKey publicKeyObj = commandExecutionService.getPublicKey(privateKeyObj);

        // then
        assertTrue(checkIfKeysMatch(privateKeyObj, publicKeyObj));
    }

    // https://stackoverflow.com/questions/49426844/how-to-validate-a-public-and-private-key-pair-in-java
    private boolean checkIfKeysMatch(PrivateKey privateKeyObj, PublicKey publicKeyObj) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        // create a challenge
        byte[] challenge = new byte[10000];
        ThreadLocalRandom.current().nextBytes(challenge);

        // sign using the private key
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKeyObj);
        signature.update(challenge);
        byte[] sign = signature.sign();

        // verify signature using the public key
        signature.initVerify(publicKeyObj);
        signature.update(challenge);

        return signature.verify(sign);
    }

    private String getPrivateKey() {
        return "MIIEpAIBAAKCAQEAm06QpJF+CI8wJ6N3/Iw5UD0Dzcp4cdBQSoF/KjrnIa7XSB6kJdhRV2ab+skWMMZpa4Gms7RGYKnC1UMbpyGij/4zlshlmiAPDtJOFP1G28PXRnbVw/FFJMFJEN1FgJ10WjzHL82MA5lIRiilYtI40yuaEY1VeohsFu1S9CwTOIO1EoPdv3AOmVdxHFL9ER9OhqunASN34JRCq3xGrAHb64JN8QQbtOgBUfvvb4PVyw/vZScnKQW+xUE6DnuJuLo1B+7382qpJN3wnLnfTPiA9EoTpAaJxCgaSfABvQUrI+vwXNmJEn1d7N3E6dWSayuUFlWYBrWNLb5ba1E9MTigKwIDAQABAoIBAHaT99AO3gG/Ae9h2lzS23et/leDvY54lLUuRl+B9bM8AVcpPf8/OGvZBkLmgbDf7OGObi9K92kadI3L3TMwdSQn3E/F1f1leNnCLKfN8eodjyJX3ULr1fUINrdPmp9mmM3FFfgjokGlKQ+YPR5Ej+p7ofjAxoD15EFlFi8j79D3XiKyISw1edWZ7TSRwbTLSfe1EexQdmUbwiUkOkCff20CfslzAqfcJNUOlOzldWwQbv4U6EZ8wTYDse04O+6BAHGuV5UV6uJ7Bgkr0W426ro2I4pC3olFF5ntcz/YfqCN+AEVp6zGTN1NgnsRkUgqVbTVtA+h2tLSjZYJu/34XUECgYEAyv1piHEOHZZjrBFJx7i8B/ttpf9Ovuc7BtBJLX2zwzDwpkhVdq0Rdd2CQiK9c5syf5oHnqQKBSzebFimQrQyAaPkF2yHsccEfQo4kmsTfdQtorXJYdKc5A//BVmVdDkuHgLVi3YF3WDRoTIxwZG2jwhx+3Z7Jko4JSeYw8ygwZsCgYEAw91gdy7vhkYRDNmuWEZ3UQwcm5vlQ7HImutS3L65VH4bbs8YUpCOk/ZIxvmoGDNAqdP23lxV7G9r9jHLsbSP+4zQWj38C6zvS2uIAnP6bwS5qRffKKN766DSK69z8CFTzYGi8cKU+6cpMJ4CCEddBp0iDeN/lwHoLFYUp/i2jLECgYEAlHlG98W5zWpy/SioDq3Q2wXM7d4QexIm2pT+8YdANUVWwEmoOWXdn5il7jn62NRr22mOqTrTGXX4Osec1K145jo6W+fGWQJ5Bfyz5GTtnNvVhW3yHtsuZqJMpl0gNCKe2NS53Yg2QfbPXliYjD1IitZiiMvgx+EBsCuGUbEa5IECgYBvWxFZMWokoPlYoPoqXgXAIm6xWbTDA/TVfy0hK54al4fpO/zMFDu7i65c0dvrxlfhNg2I5l5DiiWLV0xDwDCMB8b9R3hg+vUhoAU3v9CGBd24cUYCyM/PayOSirITuB75G9cUNFiXqrYUWgE1y7zr4bJWyxzjMcZaTqZ3yUOgQQKBgQC/Z2jx1U+sRWm+6WHUkeYGunQa/PTqD/wK+Xg77DZMMTHHdGkZ6jETu/DEJCoyXcVGanYv4xyCCA2yrT4HmnffFEhaZcT+P6pY5Y8j/7U8JEQKtE8OCmnZIOKSAtG3Gg1GKk5Bn2HYCRh5xzyVF1F/69pg4PSFxx3qChZ1n9BvWg==";
    }
}

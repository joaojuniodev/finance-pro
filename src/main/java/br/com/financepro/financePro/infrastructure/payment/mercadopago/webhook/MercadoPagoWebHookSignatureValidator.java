package br.com.financepro.financePro.infrastructure.payment.mercadopago.webhook;

import br.com.financepro.financePro.infrastructure.config.MercadoPagoProperties;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class MercadoPagoWebHookSignatureValidator {

    private final MercadoPagoProperties properties;

    public MercadoPagoWebHookSignatureValidator(MercadoPagoProperties properties) {
        this.properties = properties;
    }

    public boolean isValid(String xSignature, String xRequestId, String paymentId) {
        if (xSignature == null || xRequestId == null || paymentId == null) return false;

        String ts = extractValue(xSignature, "ts");
        String v1 = extractValue(xSignature, "v1");

        if (ts == null || v1 == null) return false;

        String manifest = String.format(
            "id:%s;request-id:%s;ts:%s;",
            paymentId,
            xRequestId,
            ts
        );

        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secretKey = new SecretKeySpec(
                properties.getWebhookSecret().getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
            );

            mac.init(secretKey);

            byte[] hash = mac.doFinal(manifest.getBytes(StandardCharsets.UTF_8));

            String calculatedSignature = bytesToHex(hash);

            return MessageDigest.isEqual(
                calculatedSignature.getBytes(StandardCharsets.UTF_8),
                v1.getBytes(StandardCharsets.UTF_8)
            );
        }
        catch (Exception e) {
            return false;
        }
    }

    private String extractValue(String signature, String key) {
        for (String part : signature.split(",")) {
            String[] keyValue = part.split("=", 2);

            if (keyValue.length == 2 && keyValue[0].trim().equals(key)) {
                return keyValue[1].trim();
            }
        }
        return null;
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
}
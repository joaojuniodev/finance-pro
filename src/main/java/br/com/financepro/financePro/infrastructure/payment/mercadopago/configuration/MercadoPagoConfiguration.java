package br.com.financepro.financePro.infrastructure.payment.mercadopago.configuration;

import br.com.financepro.financePro.infrastructure.config.MercadoPagoProperties;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.PreferenceClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MercadoPagoConfiguration {

   private final MercadoPagoProperties properties;

    public MercadoPagoConfiguration(MercadoPagoProperties properties) {
        this.properties = properties;
        MercadoPagoConfig.setAccessToken(properties.getAccessToken());
    }

    @Bean
    public PreferenceClient preferenceClient() {
        return new PreferenceClient();
    }

    @Bean
    public PaymentClient paymentClient() {
        return new PaymentClient();
    }
}
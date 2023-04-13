package com.hermanvfx.springbootactuator.service;

import com.hermanvfx.springbootactuator.configuration.CertificateStoreConfigure;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.Instant;
import java.util.Enumeration;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(CertificateStoreConfigure.class)
public class CertificateMetricServiceImpl implements CertificateMetricService {

    private final MeterRegistry meterRegistry;

    private final String PATH_KEYSTORE = "C:/Program Files/Java/jre1.8.0_361/lib/security/cacerts";
    private final char[] KEY_PASS = "changeit".toCharArray();

    @Override
    @PostConstruct
    public void registerMetrics() {
        try {
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(new FileInputStream(PATH_KEYSTORE), KEY_PASS);

            Enumeration<String> aliases = keyStore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = aliases.nextElement();
                Certificate certificate = keyStore.getCertificate(alias);
                if (certificate instanceof X509Certificate x509Certificate) {
                    Gauge.builder("certificate.days_until_expiry",
                                    () -> {
                                        return Duration.between(Instant.now(),
                                                x509Certificate
                                                        .getNotAfter()
                                                        .toInstant())
                                                .toDays();
                                    })
                            .tag("alias", alias)
                            .register(meterRegistry);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.hermanvfx.springbootactuator.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "certificate.key-store")
@Getter
@Setter
public class CertificateStoreConfigure {
    private String keyPath;
    private String keyPass;
}

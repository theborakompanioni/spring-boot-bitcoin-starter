package org.tbk.bitcoin.tool.fee.dummy.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tbk.bitcoin.tool.fee.dummy.ConfigurableDummyFeeSource;
import org.tbk.bitcoin.tool.fee.dummy.DummyFeeProvider;
import org.tbk.bitcoin.tool.fee.dummy.DummyFeeSource;

import static java.util.Objects.requireNonNull;

@Configuration
@EnableConfigurationProperties(DummyFeeProviderAutoConfigProperties.class)
@ConditionalOnProperty(name = "org.tbk.bitcoin.tool.fee.dummy.enabled", havingValue = "true")
public class DummyFeeProviderAutoConfiguration {

    private final DummyFeeProviderAutoConfigProperties properties;

    public DummyFeeProviderAutoConfiguration(DummyFeeProviderAutoConfigProperties properties) {
        this.properties = requireNonNull(properties);
    }

    @Bean
    public DummyFeeSource dummyFeeSource() {
        return new ConfigurableDummyFeeSource(properties.getStaticFeeData());
    }

    @Bean
    @ConditionalOnClass(DummyFeeSource.class)
    @ConditionalOnMissingBean(DummyFeeProvider.class)
    public DummyFeeProvider dummyFeeProvider(DummyFeeSource dummyFeeSource) {
        return new DummyFeeProvider(dummyFeeSource);
    }
}

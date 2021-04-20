package org.tbk.bitcoin.tool.fee.dummy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@Data
@ConfigurationProperties(
        prefix = "org.tbk.bitcoin.tool.fee.dummy",
        ignoreUnknownFields = false
)
public class DummyFeeProviderAutoConfigProperties {

    private static final Map<Duration, BigDecimal> DEFAULT_STATIC_FEE_DATA = Map.of();

    private boolean enabled;

    private Map<Duration, BigDecimal> staticFeeData;

    public Map<Duration, BigDecimal> getStaticFeeData() {
        return Optional.ofNullable(staticFeeData)
                .orElse(DEFAULT_STATIC_FEE_DATA);
    }
}

package org.tbk.bitcoin.tool.fee.dummy;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

@Slf4j
public class ConfigurableDummyFeeSource implements DummyFeeSource {

    private final Map<Duration, BigDecimal> configuredFees;

    public ConfigurableDummyFeeSource(@NonNull Map<Duration, BigDecimal> configuredFees) {
        this.configuredFees =  configuredFees;
    }

    @Override
    public Map<Duration, BigDecimal> feeEstimations() {
        return configuredFees;
    }
}

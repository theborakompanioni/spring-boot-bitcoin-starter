package org.tbk.bitcoin.tool.fee.dummy;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

/**
 * Free configurable dummy fee estimations
 *
 * Designed to deliver fix values useful for automatic tests.
 */
public interface DummyFeeSource {

    /**
     * All configured fee estimations as map Duration => satPerVByte
     *
     * @return configured feeEstimations
     */
    Map<Duration, BigDecimal> feeEstimations();

}

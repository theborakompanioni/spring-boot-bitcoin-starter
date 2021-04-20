package org.tbk.bitcoin.tool.fee.dummy;

import lombok.extern.slf4j.Slf4j;
import org.tbk.bitcoin.tool.fee.*;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class DummyFeeProvider extends AbstractFeeProvider {

    private static final ProviderInfo providerInfo = ProviderInfo.SimpleProviderInfo.builder()
            .name("Dummy")
            .description("")
            .build();

    private final DummyFeeSource dummyFeeSource;

    public DummyFeeProvider(DummyFeeSource dummyFeeSource) {
        super(providerInfo);
        this.dummyFeeSource = dummyFeeSource;
    }

    @Override
    public boolean supports(FeeRecommendationRequest request) {
        return request.getDesiredConfidence().isEmpty();
    }

    @Override
    public Flux<FeeRecommendationResponse> requestHook(FeeRecommendationRequest request) {

        Optional<BigDecimal> satPerVByteOrNull = dummyFeeSource.feeEstimations().entrySet().stream()
                .filter(it -> it.getKey().compareTo(request.getDurationTarget()) <= 0)
                .max(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue);

        if (satPerVByteOrNull.isEmpty()) {
            log.warn("No dummy value available for duration {}", request.getDurationTarget());
            return Flux.empty();
        }

        return Flux.just(FeeRecommendationResponseImpl.builder()
                .addFeeRecommendation(FeeRecommendationResponseImpl.FeeRecommendationImpl.builder()
                        .feeUnit(FeeRecommendationResponseImpl.SatPerVbyteImpl.builder()
                                .satPerVbyteValue(satPerVByteOrNull.get())
                                .build())
                        .build())
                .build());
    }
}

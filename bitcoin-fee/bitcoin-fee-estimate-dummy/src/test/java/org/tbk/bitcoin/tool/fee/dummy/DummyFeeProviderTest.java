package org.tbk.bitcoin.tool.fee.dummy;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.tbk.bitcoin.tool.fee.FeeRecommendationRequest;
import org.tbk.bitcoin.tool.fee.FeeRecommendationRequest.Confidence;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DummyFeeProviderTest {

    private DummyFeeProvider sut;

    private DummyFeeSource mockedFeeSource = mock(DummyFeeSource.class);

    private static final Duration TEST_DURATION = Duration.ofMinutes(23L);

    private static final BigDecimal TEST_FEE_AMOUNT = BigDecimal.TEN;

    @Before
    public void setup() {
        sut = new DummyFeeProvider(mockedFeeSource);
    }

    @Test
    public void supportsReturnsTrueForNotConfidenceDesiredRequest() {
        var request = mock(FeeRecommendationRequest.class);
        when(request.getDesiredConfidence()).thenReturn(Optional.empty());

        assertThat(sut.supports(request), is(true));
    }

    @Test
    public void supportsReturnsFalseForConfidenceDesiredRequest() {
        var request = mock(FeeRecommendationRequest.class);
        when(request.getDesiredConfidence()).thenReturn(Optional.of(mock(Confidence.class)));

        assertThat(sut.supports(request), is(false));
    }

    @Test
    public void requestHookReturnEmptyResultWithEmptyFeeSource() {
        var request = mock(FeeRecommendationRequest.class);
        when(request.getDurationTarget()).thenReturn(TEST_DURATION);

        var result = sut.requestHook(request);
        assertThat(result.toStream().count(), is(0L));
    }


    @Test
    public void requestHookIgnoresFeeSourceWithGreaterDuration() {
        var request = mock(FeeRecommendationRequest.class);
        when(request.getDurationTarget()).thenReturn(TEST_DURATION);
        when(mockedFeeSource.feeEstimations()).thenReturn(ImmutableMap.<Duration, BigDecimal>builder()
                .put(TEST_DURATION.plusMinutes(1), TEST_FEE_AMOUNT)
                .build()
        );

        var result = sut.requestHook(request);

        assertThat(result.toStream().count(), is(0L));
    }

    @Test
    public void requestHookReturnEstimationWithExactDuration() {
        var request = mock(FeeRecommendationRequest.class);
        when(request.getDurationTarget()).thenReturn(TEST_DURATION);
        when(mockedFeeSource.feeEstimations()).thenReturn(ImmutableMap.<Duration, BigDecimal>builder()
                .put(TEST_DURATION, TEST_FEE_AMOUNT)
                .build()
        );

        var result = sut.requestHook(request);

        assertThat(result.toStream().count(), is(1L));
        assertThat(result.toStream().findFirst().get().getFeeRecommendations(), hasSize(1));
        var feeRecommendation = result.toStream().findFirst().get().getFeeRecommendations().stream().findFirst().get();
        assertThat(feeRecommendation.getFeeUnit().getValue(), is(TEST_FEE_AMOUNT));
    }

    @Test
    public void requestHookReturnsEstimationWithSmallerDuration() {
        var request = mock(FeeRecommendationRequest.class);
        when(request.getDurationTarget()).thenReturn(TEST_DURATION);
        when(mockedFeeSource.feeEstimations()).thenReturn(ImmutableMap.<Duration, BigDecimal>builder()
                .put(TEST_DURATION.minusMinutes(1), TEST_FEE_AMOUNT)
                .build()
        );

        var result = sut.requestHook(request);

        assertThat(result.toStream().count(), is(1L));
        assertThat(result.toStream().findFirst().get().getFeeRecommendations(), hasSize(1));
        var feeRecommendation = result.toStream().findFirst().get().getFeeRecommendations().stream().findFirst().get();
        assertThat(feeRecommendation.getFeeUnit().getValue(), is(TEST_FEE_AMOUNT));
    }

    @Test
    public void requestHookReturnsEstimationFromNearestMatchingDuration() {
        var request = mock(FeeRecommendationRequest.class);
        when(request.getDurationTarget()).thenReturn(TEST_DURATION);
        when(mockedFeeSource.feeEstimations()).thenReturn(ImmutableMap.<Duration, BigDecimal>builder()
                .put(TEST_DURATION.plusMinutes(1), TEST_FEE_AMOUNT.add(BigDecimal.TEN)) // will be ignored because duration too high
                .put(TEST_DURATION.minusMinutes(2), TEST_FEE_AMOUNT.add(BigDecimal.ONE)) // matching duration
                .put(TEST_DURATION.minusMinutes(1), TEST_FEE_AMOUNT) // matching duration with best match
                .put(TEST_DURATION.minusMinutes(3), TEST_FEE_AMOUNT.subtract(BigDecimal.ONE)) // also matching but not the best match
                .build()
        );

        var result = sut.requestHook(request);

        assertThat(result.toStream().count(), is(1L));
        assertThat(result.toStream().findFirst().get().getFeeRecommendations(), hasSize(1));
        var feeRecommendation = result.toStream().findFirst().get().getFeeRecommendations().stream().findFirst().get();
        assertThat(feeRecommendation.getFeeUnit().getValue(), is(TEST_FEE_AMOUNT)); // amount from best match: TEST_DURATION - 1 minute
    }
}

package com.jworks.qup.service.framework;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.assertj.core.api.AbstractBigDecimalAssert;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractLongAssert;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.OptionalAssert;
import org.mockito.BDDMockito;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
public abstract class TestSupport {

    protected static final CommonOperations COMMON_OPERATIONS = new CommonOperations();
    protected static final Random random = COMMON_OPERATIONS.getRandom();

    protected final int nextNumber() {
        return COMMON_OPERATIONS.nextNumber();
    }

    protected String nextEmail() {
        return COMMON_OPERATIONS.nextEmail();
    }

    private BigDecimal nextAmount() {
        return COMMON_OPERATIONS.nextAmount();
    }

    protected double scale2(BigDecimal bigDecimal) {
        return COMMON_OPERATIONS.scale2(bigDecimal);
    }

    // Chooses n integers at RANDOM from the range [0, limit)
    protected List<Integer> chooseAtRandom(final int n, final int limit) {
        return COMMON_OPERATIONS.chooseAtRandom(n, limit);
    }

    protected <T> ObjectAssert<T> assertThat(T actual) {
        return COMMON_OPERATIONS.assertThat(actual);
    }

    protected <ELEMENT> ListAssert<ELEMENT> assertThat(List<? extends ELEMENT> actual) {
        return COMMON_OPERATIONS.assertThat(actual);
    }

    protected <VALUE> OptionalAssert<VALUE> assertThat(Optional<VALUE> actual) {
        return COMMON_OPERATIONS.assertThat(actual);
    }

    protected AbstractBooleanAssert<?> assertThat(Boolean actual) {
        return COMMON_OPERATIONS.assertThat(actual);
    }

    protected AbstractBigDecimalAssert<?> assertThat(BigDecimal actual) {
        return COMMON_OPERATIONS.assertThat(actual);
    }

    protected AbstractLongAssert<?> assertThat(long actual) {
        return COMMON_OPERATIONS.assertThat(actual);
    }

    protected AbstractLongAssert<?> assertThat(Long actual) {
        return COMMON_OPERATIONS.assertThat(actual);
    }

    protected <T extends Object> BDDMockito.BDDMyOngoingStubbing<T> given(T methodCall) {
        return COMMON_OPERATIONS.given(methodCall);
    }

    protected BDDMockito.BDDStubber willReturn(Object toBeReturned) {
        return COMMON_OPERATIONS.willReturn(toBeReturned);
    }

    protected BDDMockito.BDDStubber willReturn(Object toBeReturned, Object... toBeReturnedNext) {
        return COMMON_OPERATIONS.willReturn(toBeReturned, toBeReturnedNext);
    }

    protected BDDMockito.BDDStubber willThrow(Class<? extends Throwable> toBeThrown) {
        return COMMON_OPERATIONS.willThrow(toBeThrown);
    }

    protected BDDMockito.BDDStubber willThrow(Throwable... toBeThrown) {
        return COMMON_OPERATIONS.willThrow(toBeThrown);
    }

    protected String anyString() {
        return COMMON_OPERATIONS.anyString();
    }

    protected <T extends Object> T any(Class<T> type) {
        return COMMON_OPERATIONS.any(type);
    }

    protected BDDMockito.BDDStubber willCallRealMethod() {
        return COMMON_OPERATIONS.willCallRealMethod();
    }
}

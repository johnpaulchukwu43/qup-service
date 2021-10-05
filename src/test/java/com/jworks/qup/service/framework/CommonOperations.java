package com.jworks.qup.service.framework;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.assertj.core.api.AbstractBigDecimalAssert;
import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractLongAssert;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.assertj.core.api.ListAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.OptionalAssert;
import org.mockito.BDDMockito;

/**
 * @author bodmas
 * @since Oct 4, 2021.
 */
public class CommonOperations {

    private int counter;
    private final Random random;

    {
        long seed = (long) (Math.random() * 1_000_000_000_000_000L);
        random = new Random(seed);
        System.out.println("seed = " + seed);
    }

    public int getCounter() {
        return counter;
    }

    public int incrementAndGetCounter() {
        return ++counter;
    }

    public Random getRandom() {
        return random;
    }

    public BigDecimal nextAmount() {
        return BigDecimal.valueOf(100 + random.nextInt(10_000)).setScale(2, RoundingMode.HALF_UP);
    }

    public int nextNumber() {
        return ++counter;
    }

    public String nextEmail() {
        return "user" + nextNumber() + "@teamapt.com";
    }

    protected double scale2(BigDecimal bigDecimal) {
        return bigDecimal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    // Chooses n integers at RANDOM from the range [0, limit)
    protected List<Integer> chooseAtRandom(final int n, final int limit) {
        if (n > limit)
            throw new RuntimeException(n + " = n > limit = " + limit);

        int offset = 0;
        List<Integer> result = new ArrayList<>();
        int decr = n;
        int length = limit;
        for (int i = 0; i < n; i++) {
            int span = length - decr;
            int choice = random.nextInt(span + 1);
            offset += choice;
            result.add(offset);
            offset++;
            length -= (choice + 1);
            decr--;
        }
        if (result.size() != n)
            throw new RuntimeException("Wrong computation: expected " + n + " but found " + result.size());
        return result;
    }

    protected <T> ObjectAssert<T> assertThat(T actual) {
        return Assertions.assertThat(actual);
    }

    protected <ELEMENT> ListAssert<ELEMENT> assertThat(List<? extends ELEMENT> actual) {
        return AssertionsForInterfaceTypes.assertThat(actual);
    }

    protected <VALUE> OptionalAssert<VALUE> assertThat(Optional<VALUE> actual) {
        return AssertionsForClassTypes.assertThat(actual);
    }

    protected AbstractBooleanAssert<?> assertThat(Boolean actual) {
        return AssertionsForClassTypes.assertThat(actual);
    }

    protected AbstractBigDecimalAssert<?> assertThat(BigDecimal actual) {
        return AssertionsForClassTypes.assertThat(actual);
    }

    protected AbstractLongAssert<?> assertThat(long actual) {
        return AssertionsForClassTypes.assertThat(actual);
    }

    protected AbstractLongAssert<?> assertThat(Long actual) {
        return AssertionsForClassTypes.assertThat(actual);
    }

    protected <T extends Object> BDDMockito.BDDMyOngoingStubbing<T> given(T methodCall) {
        return BDDMockito.given(methodCall);
    }

    protected BDDMockito.BDDStubber willReturn(Object toBeReturned) {
        return BDDMockito.willReturn(toBeReturned);
    }

    protected BDDMockito.BDDStubber willReturn(Object toBeReturned, Object... toBeReturnedNext) {
        return BDDMockito.willReturn(toBeReturned, toBeReturnedNext);
    }

    protected BDDMockito.BDDStubber willThrow(Class<? extends Throwable> toBeThrown) {
        return BDDMockito.willThrow(toBeThrown);
    }

    protected BDDMockito.BDDStubber willThrow(Throwable... toBeThrown) {
        return BDDMockito.willThrow(toBeThrown);
    }

    protected String anyString() {
        return BDDMockito.anyString();
    }

    protected <T extends Object> T any(Class<T> type) {
        return BDDMockito.any(type);
    }

    protected BDDMockito.BDDStubber willCallRealMethod() {
        return BDDMockito.willCallRealMethod();
    }
}

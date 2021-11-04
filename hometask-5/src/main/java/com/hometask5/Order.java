package com.hometask5;

import java.util.concurrent.ThreadLocalRandom;

public class Order {
    private static final long MAX_DURATION = 1000;

    private final long duration;

    Order(long duration) {
        this.duration = duration;
    }

    Order() {
        this.duration = ThreadLocalRandom.current().nextLong(MAX_DURATION);
    }

    public long getDuration() {
        return duration;
    }
}

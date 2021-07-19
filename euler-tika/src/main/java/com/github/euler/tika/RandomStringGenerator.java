package com.github.euler.tika;

import java.util.Random;

public class RandomStringGenerator {

    private final String sample;
    private final Random random;

    public RandomStringGenerator() {
        this("0123456789abcdefghijklmnopqrstuvxzABCDFGHIJKLMNOPQRSTUVXZ");
    }

    public RandomStringGenerator(String sample) {
        super();
        this.sample = sample;
        this.random = new Random();
    }

    public String generate(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(sample.charAt(random.nextInt(sample.length())));
        }
        return builder.toString();
    }

}

package com.cms.security;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PasswordGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}<>?";

    private final int length;

    public PasswordGenerator() {
        this(16);
    }

    public PasswordGenerator(int length) {
        if (length < 12)
            throw new IllegalArgumentException("password length must be >= 12");
        this.length = length;
    }

    public String generate() {
        List<Character> pwd = new ArrayList<>();
        // ensure at least one from each category
        pwd.add(randomChar(UPPER));
        pwd.add(randomChar(LOWER));
        pwd.add(randomChar(DIGITS));
        pwd.add(randomChar(SPECIAL));

        String all = UPPER + LOWER + DIGITS + SPECIAL;
        for (int i = pwd.size(); i < length; i++) {
            pwd.add(randomChar(all));
        }
        Collections.shuffle(pwd, RANDOM);
        StringBuilder sb = new StringBuilder();
        for (char c : pwd)
            sb.append(c);
        return sb.toString();
    }

    private char randomChar(String s) {
        return s.charAt(RANDOM.nextInt(s.length()));
    }
}

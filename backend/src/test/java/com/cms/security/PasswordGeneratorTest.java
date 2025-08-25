package com.cms.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PasswordGeneratorTest {

    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}<>?";
    private static final String ALL = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" +
            "abcdefghijklmnopqrstuvwxyz" +
            "0123456789" +
            SPECIAL;

    @Test
    void generatedPasswordHasAllCategories_length16() {
        PasswordGenerator g = new PasswordGenerator(16);
        String pwd = g.generate();
        assertNotNull(pwd);
        assertEquals(16, pwd.length());
        assertTrue(pwd.chars().anyMatch(ch -> Character.isUpperCase(ch)), "must contain uppercase");
        assertTrue(pwd.chars().anyMatch(ch -> Character.isLowerCase(ch)), "must contain lowercase");
        assertTrue(pwd.chars().anyMatch(ch -> Character.isDigit(ch)), "must contain digit");
        assertTrue(pwd.chars().anyMatch(ch -> SPECIAL.indexOf(ch) >= 0), "must contain special");
    }

    @Test
    void defaultConstructorProduces16() {
        PasswordGenerator g = new PasswordGenerator();
        String pwd = g.generate();
        assertNotNull(pwd);
        assertEquals(16, pwd.length());
    }

    @Test
    void minLength12_isAllowedAndHasAllCategories() {
        PasswordGenerator g = new PasswordGenerator(12);
        String pwd = g.generate();
        assertNotNull(pwd);
        assertEquals(12, pwd.length());
        assertTrue(pwd.chars().anyMatch(ch -> Character.isUpperCase(ch)));
        assertTrue(pwd.chars().anyMatch(ch -> Character.isLowerCase(ch)));
        assertTrue(pwd.chars().anyMatch(ch -> Character.isDigit(ch)));
        assertTrue(pwd.chars().anyMatch(ch -> SPECIAL.indexOf(ch) >= 0));
    }

    @Test
    void constructorRejectsTooShort() {
        assertThrows(IllegalArgumentException.class, () -> new PasswordGenerator(8));
    }

    @Test
    void generatedPasswordsAreLikelyDifferent() {
        PasswordGenerator g = new PasswordGenerator(16);
        String a = g.generate();
        String b = g.generate();
        // Extremely unlikely to collide; this guards against a broken RNG
        // implementation
        assertNotEquals(a, b);
    }

    @Test
    void generatedPasswordOnlyUsesAllowedCharacters() {
        PasswordGenerator g = new PasswordGenerator(32);
        String pwd = g.generate();
        for (int i = 0; i < pwd.length(); i++) {
            char c = pwd.charAt(i);
            assertTrue(ALL.indexOf(c) >= 0, () -> "unexpected character: '" + c + "'");
        }
    }
}

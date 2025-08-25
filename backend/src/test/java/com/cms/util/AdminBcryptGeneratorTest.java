package com.cms.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Temporary test to generate bcrypt hash for admin password.
 * This class is safe to keep as a test; it prints one hash when run.
 */
public class AdminBcryptGeneratorTest {

    @Test
    void printAdminBcrypt() {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        String hash = enc.encode("admin1234");
        // print to stdout so Gradle test output contains the hash
        System.out.println("ADMIN_BCRYPT:" + hash);
        // trivial assertion to make test pass
        assert hash.startsWith("$2");
    }
}

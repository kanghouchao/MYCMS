package com.cms.model.tenant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.Test;

class BaseEntityTests {

    // Minimal concrete class for testing lifecycle methods
    static class TestEntity extends BaseEntity {
    }

    @Test
    void onCreateSetsTimestamps() {
        TestEntity e = new TestEntity();
        e.onCreate();
        assertThat(e.getCreatedAt()).isNotNull();
        assertThat(e.getUpdatedAt()).isNotNull();
        assertThat(e.getUpdatedAt()).isEqualTo(e.getCreatedAt());
    }

    @Test
    void onUpdateUpdatesUpdatedAtOnly() throws InterruptedException {
        TestEntity e = new TestEntity();
        e.onCreate();
        OffsetDateTime created = e.getCreatedAt();
        OffsetDateTime updated1 = e.getUpdatedAt();

        // ensure clock moves forward at least 1ms
        Thread.sleep(2);

        e.onUpdate();
        assertThat(e.getCreatedAt()).isEqualTo(created);
        assertThat(e.getUpdatedAt()).isAfter(updated1);
    }
}

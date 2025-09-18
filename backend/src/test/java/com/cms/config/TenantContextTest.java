package com.cms.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class TenantContextTest {

  @Test
  void setGetAndClearTenantId() {
    TenantContext ctx = new TenantContext();
    assertThat(ctx.isTenant()).isFalse();
    ctx.setTenantId("t-1");
    assertThat(ctx.getTenantId()).isEqualTo("t-1");
    assertThat(ctx.isTenant()).isTrue();
    ctx.clear();
    assertThat(ctx.getTenantId()).isNull();
    assertThat(ctx.isTenant()).isFalse();
  }
}

package com.watchtower.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiApplicationTests {

    @Test
    void mainClassLoads() {
        assertThat(ApiApplication.class).isNotNull();
    }
}

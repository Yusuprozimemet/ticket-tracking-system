package net.hackyourfuture.tickettrackingsystem.integration;

import org.junit.jupiter.api.Test;

// Smoke test: proves the whole Spring application context starts up correctly against a
// real PostgreSQL. Reuses the Testcontainers + context setup from IntegrationTestBase.
class ContextLoadsTest extends IntegrationTestBase {

    @Test
    void contextLoads() {
    }
}

package de.pcmr.shop.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SanitiziationUtilsTest {

    private static final String HTML_UNSANITIZED = "<script> malicious-javascipt; </script> Test";
    private static final String HTML_SANITIZED = "Test";

    @Test
    public void testSanitizeHtml() {
        assertEquals(HTML_SANITIZED, SanitizationUtils.sanitizeHtml(HTML_UNSANITIZED).trim());
    }
}

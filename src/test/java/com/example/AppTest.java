package com.example;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AppTest {

    @Test
    void indexHtmlContainsAllRequiredFields() throws IOException {
        String html = App.loadIndexHtml();
        assertTrue(html.contains("Student Name"));
        assertTrue(html.contains("Roll Number"));
        assertTrue(html.contains("Email"));
        assertTrue(html.contains("Course"));
        assertTrue(html.contains("Submit"));
    }

    @Test
    void parseFormDataDecodesFieldsCorrectly() {
        String payload = "studentName=Aakash+Singh&rollNumber=0241CSDS127&email=aakash%40example.com&course=BTech+CSE";
        Map<String, String> parsed = App.parseFormData(payload);

        assertEquals("Aakash Singh", parsed.get("studentName"));
        assertEquals("0241CSDS127", parsed.get("rollNumber"));
        assertEquals("aakash@example.com", parsed.get("email"));
        assertEquals("BTech CSE", parsed.get("course"));
    }

    @Test
    void renderConfirmationPageEscapesAndDisplaysFields() {
        Map<String, String> fields = Map.of(
                "studentName", "Aakash <Singh>",
                "rollNumber", "0241CSDS127",
                "email", "aakash@example.com",
                "course", "CSE-DS"
        );
        String page = App.renderConfirmationPage(fields);
        assertTrue(page.contains("Registration Successful"));
        assertTrue(page.contains("Aakash &lt;Singh&gt;"));
        assertTrue(page.contains("0241CSDS127"));
        assertTrue(page.contains("CSE-DS"));
    }
}

package org.acme.demo.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestApi {

    @GetMapping
    public ResponseEntity<Map<String, String>> test() {
        return ResponseEntity.ok(Map.ofEntries(
            Map.entry("message", "Test API is working!")
        ));
    }
}

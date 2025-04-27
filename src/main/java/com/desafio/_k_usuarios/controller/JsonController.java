package com.desafio._k_usuarios.controller;

import com.desafio._k_usuarios.entity.JsonData;
import com.desafio._k_usuarios.service.JsonService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/json")
public class JsonController {

    private final JsonService jsonService;

    @PostMapping("/users")
    public ResponseEntity<Void> uploadUsers(@RequestParam("file") MultipartFile file) {
        jsonService.saveUsers(file);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/super-users")
    public ResponseEntity<List<JsonData>> superUsers() {
        long start = System.currentTimeMillis();
        List<JsonData> result = jsonService.superUsers();
        long end = System.currentTimeMillis();
        return ResponseEntity.ok()
                .header("Execution Time", String.valueOf(end - start))
                .body(result);
    }

    @GetMapping("/top-countries")
    public ResponseEntity<Map<String, Long>> topCountries() {
        long start = System.currentTimeMillis();
        Map<String, Long> result = jsonService.topCountries();
        long end = System.currentTimeMillis();
        return ResponseEntity.ok()
                .header("Execution Time", String.valueOf(end - start))
                .body(result);
    }

    @GetMapping("/team-insights")
    public ResponseEntity<List<Map<String, Object>>> teamInsights() {
        long start = System.currentTimeMillis();
        List<Map<String, Object>> result = jsonService.teamInsights();
        long end = System.currentTimeMillis();
        return ResponseEntity.ok()
                .header("Execution Time", String.valueOf(end - start))
                .body(result);
    }

}

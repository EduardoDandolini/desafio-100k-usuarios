package com.desafio._k_usuarios.controller;

import com.desafio._k_usuarios.entity.JsonData;
import com.desafio._k_usuarios.service.JsonService;
import com.desafio._k_usuarios.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    public ResponseEntity<ResponseUtil<List<JsonData>>> superUsers() {
        return ResponseEntity.ok(jsonService.superUsers());
    }

    @GetMapping("/top-countries")
    public ResponseEntity<ResponseUtil<Map<String, Long>>> topCountries() {
        return ResponseEntity.ok(jsonService.topCountries());
    }

    @GetMapping("/team-insights")
    public ResponseEntity<ResponseUtil<List<Map<String, Object>>>> teamInsights() {
        return ResponseEntity.ok(jsonService.teamInsights());
    }

    @GetMapping("/actives-users-per-day")
    public ResponseEntity<ResponseUtil<Map<LocalDate, Long>>> activesUsersPerDay() {
        return ResponseEntity.ok(jsonService.activeUsersPerDay());
    }

    @GetMapping("/evaluate-endpoints")
    public ResponseEntity<List<Map<String, Object>>> evaluateEndpoints() {
        return ResponseEntity.ok(jsonService.evaluateEndpoints());
    }
}

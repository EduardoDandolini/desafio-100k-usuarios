package com.desafio._k_usuarios.service;

import com.desafio._k_usuarios.entity.Equipe;
import com.desafio._k_usuarios.entity.JsonData;
import com.desafio._k_usuarios.entity.Log;
import com.desafio._k_usuarios.entity.Projeto;
import com.desafio._k_usuarios.util.ResponseExecutionTimeUtil;
import com.desafio._k_usuarios.util.ResponseUtil;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JsonService {

    private final ObjectMapper objectMapper;
    private List<JsonData> users = new CopyOnWriteArrayList<>();
    private final RestTemplate restTemplate = new RestTemplate();

    private static final List<String> ENDPOINTS = List.of(
            "/json/super-users",
            "/json/top-countries",
            "/json/team-insights",
            "/json/active-users-per-day"
    );

    public void saveUsers(MultipartFile file) {
        try {
            List<JsonData> usersToSave = objectMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<List<JsonData>>() {
                    }
            );

            users.clear();
            users.addAll(usersToSave);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo JSON", e);
        }
    }

    public ResponseUtil<List<JsonData>> superUsers() {
        return ResponseExecutionTimeUtil.withMetaData(() ->
                users.stream()
                        .filter(user -> user.getScore() >= 900 && user.isAtivo())
                        .toList()
        );
    }

    public ResponseUtil<Map<String, Long>> topCountries() {
        return ResponseExecutionTimeUtil.withMetaData(() ->
                superUsers().getData().stream()
                        .collect(Collectors.groupingBy(JsonData::getPais, Collectors.counting()))
                        .entrySet().stream()
                        .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                        .limit(5)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
        );
    }

    public ResponseUtil<List<Map<String, Object>>> teamInsights() {
        return ResponseExecutionTimeUtil.withMetaData(() ->
                superUsers().getData().stream()
                        .collect(Collectors.groupingBy(user -> user.getEquipe().getNome()))
                        .entrySet().stream()
                        .map(entry -> {
                            List<JsonData> users = entry.getValue();
                            long members = users.size();
                            long leaders = users.stream().filter(user -> user.getEquipe().isLider()).count();
                            long projects = users.stream().filter(user -> user.getEquipe().getProjetos().stream().anyMatch(Projeto::isConcluido)).count();
                            double activePercent = (users.stream().filter(JsonData::isAtivo).count() * 100.0);

                            Map<String, Object> map = new HashMap<>();
                            map.put("team", entry.getKey());
                            map.put("totalMembers", members);
                            map.put("leaders", leaders);
                            map.put("projectsCompleted", projects);
                            map.put("activePercent", activePercent);
                            return map;
                        }).toList()
        );
    }

    public ResponseUtil<Map<LocalDate, Long>> activeUsersPerDay() {
        return ResponseExecutionTimeUtil.withMetaData(() ->
                users.stream()
                        .filter(JsonData::isAtivo)
                        .flatMap(user -> user.getLogs().stream())
                        .filter(log -> "login".equalsIgnoreCase(log.getAcao()))
                        .collect(Collectors.groupingBy(
                                Log::getData,
                                Collectors.counting()
                        ))
        );
    }

    public List<Map<String, Object>> evaluateEndpoints() {
        int port = 8080;
        List<Map<String, Object>> results = new ArrayList<>();

        for (String endpoint : ENDPOINTS) {
            Map<String, Object> result = new HashMap<>();
            String url = "http://localhost:" + port + endpoint;
            result.put("endpoint", endpoint);

            try {
                long start = System.currentTimeMillis();
                ResponseEntity<String> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        new HttpEntity<>(new HttpHeaders()),
                        String.class
                );
                long end = System.currentTimeMillis();

                result.put("status", response.getStatusCodeValue());
                result.put("responseTimeMillis", end - start);
                result.put("validJson", isJsonValid(response.getBody()));
            } catch (Exception e) {
                result.put("status", 500);
                result.put("responseTimeMillis", -1);
                result.put("validJson", false);
            }

             results.add(result);
        }

        return results;
    }

    private boolean isJsonValid(String body) {
        try {
            objectMapper.readTree(body);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

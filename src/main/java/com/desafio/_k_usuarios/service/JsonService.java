package com.desafio._k_usuarios.service;

import com.desafio._k_usuarios.entity.Equipe;
import com.desafio._k_usuarios.entity.JsonData;
import com.desafio._k_usuarios.entity.Log;
import com.desafio._k_usuarios.entity.Projeto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JsonService {

    private final  ObjectMapper objectMapper;
    private List<JsonData> users = new CopyOnWriteArrayList<>();

    public void saveUsers(MultipartFile file) {
        try {
            List<JsonData> usersToSave = objectMapper.readValue(
                    file.getInputStream(),
                    new TypeReference<List<JsonData>>() {}
            );

            users.clear();
            users.addAll(usersToSave);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao processar o arquivo JSON", e);
        }
    }

    public List<JsonData> superUsers() {
       return users.stream().filter(user -> user.getScore() >= 900 && user.isAtivo()).toList();
    }

    public Map<String, Long> topCountries() {
       return superUsers().stream()
                .collect(Collectors.groupingBy(JsonData::getPais, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    }

    public List<Map<String, Object>> teamInsights() {
        return superUsers().stream()
                .collect(Collectors.groupingBy(user -> user.getEquipe().getNome()))
                .entrySet().stream()
                .map(entry -> {
                    List<JsonData> users = entry.getValue();
                    long members = users.size();
                    long leaders = users.stream().filter(user -> user.getEquipe().isLider()).count();
                    long projects = users.stream().filter(user -> user.getEquipe().getProjetos().stream().anyMatch(Projeto::isConcluido)).count();
                    double activePercent = (users.stream().filter(JsonData ::isAtivo).count() * 100.0);

                    Map<String, Object> map = new HashMap<>();
                    map.put("team", entry.getKey());
                    map.put("totalMembers", members);
                    map.put("leaders", leaders);
                    map.put("projectsCompleted", projects);
                    map.put("activePercent", activePercent);
                    return map;
                }).toList();

    }
}

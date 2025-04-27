package com.desafio._k_usuarios.entity;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Log {

    private Long id;
    private LocalDate data;
    private String acao;
}


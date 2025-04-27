package com.desafio._k_usuarios.entity;

import lombok.Data;

import java.util.List;

@Data
public class Equipe {

    private Long id;
    private String nome;
    private boolean lider;
    private List<Projeto> projetos;
}

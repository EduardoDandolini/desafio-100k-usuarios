package com.desafio._k_usuarios.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ResponseUtil<T> {

    private T data;
    private long processingTimeMillis;
    private Instant timestamp;
}

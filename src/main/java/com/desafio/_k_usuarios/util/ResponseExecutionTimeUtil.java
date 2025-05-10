package com.desafio._k_usuarios.util;

import java.time.Instant;
import java.util.function.Supplier;

public class ResponseExecutionTimeUtil {

    public static <T> ResponseUtil<T> withMetaData(Supplier<T> supplier) {
        long start = System.currentTimeMillis();
        T data = supplier.get();
        long end = System.currentTimeMillis();
        return new ResponseUtil<>(
                data,
                end - start,
                Instant.now()
        );
    }
}

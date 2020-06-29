package com.mnzit.spring.redis.redisdemo.dto;

import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Manjit Shakya
 * @email manjit.shakya@f1soft.com
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GenericResponse<T> implements Serializable {

    @NotNull
    private String resultCode;
    @NotNull
    private String resultDescription;
    private T data;

    public static <T> GenericResponse build(String code, String description) {
        return new GenericResponse(code, description,null);
    }

    public static <T> GenericResponse build(String code, String description, T data) {
        return new GenericResponse(code, description, data);
    }
}

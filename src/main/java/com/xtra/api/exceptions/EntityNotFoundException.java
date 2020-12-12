package com.xtra.api.exceptions;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

@EqualsAndHashCode(callSuper = true)
@Data
public class EntityNotFoundException extends RuntimeException {
    private String details;

    public EntityNotFoundException() {
    }

    public EntityNotFoundException(String message){
        super("Entity Not Found!");
        details = message;
    }

    public EntityNotFoundException(String entityName, String id) {
        super("Entity Not Found!");
        details = generateMessage(entityName, id);
    }

    private static String generateMessage(String entityName, String id) {
        return entityName + " was not found for id = " + id;
    }

}

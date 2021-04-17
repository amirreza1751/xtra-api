package com.xtra.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EntityNotFoundException extends RuntimeException {
    private String details;

    public EntityNotFoundException() {
        details = "An entity was not found!";
    }

    public EntityNotFoundException(String entityName) {
        details = entityName + " was not found!";
    }

    public EntityNotFoundException(String entityName, Object id) {
        super("Entity Not Found!");
        details = entityName + " was not found for id = " + id + "!";
    }

    public EntityNotFoundException(String entityName, String fieldName, String value) {
        super("Entity Not Found!");
        details = entityName + " was not found for " + fieldName + " = " + value + "!";
    }

}

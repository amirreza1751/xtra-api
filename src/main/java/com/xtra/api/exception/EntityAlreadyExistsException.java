package com.xtra.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class EntityAlreadyExistsException extends RuntimeException {
    private String details;

    public EntityAlreadyExistsException() {
        details = "Entity already exists in the database!";
    }

    public EntityAlreadyExistsException(Long entityId) {
        details = entityId + "already exists in the database!";
    }

    public EntityAlreadyExistsException(String entityName, Object id) {
        super("Duplicate Entity!");
        details = entityName + " is duplicate for id = " + id + "!";
    }

}

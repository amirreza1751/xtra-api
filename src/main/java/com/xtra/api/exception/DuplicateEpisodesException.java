package com.xtra.api.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class DuplicateEpisodesException extends RuntimeException {
    private String details;

    public DuplicateEpisodesException() {
        details = "Last added episode exists in database!";
    }

    public DuplicateEpisodesException(Long episodeNumber) {
        details = episodeNumber + " exists in database!";
    }

    public DuplicateEpisodesException(String entityName, Object id) {
        super("Duplicate Entry!");
        details = entityName + " is duplicate for episode_number = " + id + "!";
    }

}

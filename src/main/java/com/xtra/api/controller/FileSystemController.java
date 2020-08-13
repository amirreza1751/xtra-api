package com.xtra.api.controller;

import com.xtra.api.model.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/files")
public class FileSystemController {
    @Value("${core.apiPath}")
    private String corePath;
    @Value("${core.apiPort}")
    private String corePort;

    @GetMapping("/list")
    public List<File> getFileList(@RequestParam String path) {
        List<File> result = null;
        try {
            result = new RestTemplate().getForObject(corePath + ":" + corePort + "/file/list_files?path=" + path, List.class);
        } catch (HttpClientErrorException exception) {
            System.out.println(exception.getMessage());
        }
        return result;
    }
}

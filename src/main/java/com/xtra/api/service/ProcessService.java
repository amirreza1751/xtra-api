package com.xtra.api.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static io.netty.util.internal.PlatformDependent.isWindows;

@Service
@Log4j2
public class ProcessService {
    public Long runProcess(String... args) {
        File bitbucket;

        if (isWindows()) {
            bitbucket = new File("NUL");
        } else {
            bitbucket = new File("/dev/null");
        }
        Process proc;
        try {
            proc = new ProcessBuilder(args)
                    .redirectOutput(ProcessBuilder.Redirect.appendTo(bitbucket))
                    .redirectError(ProcessBuilder.Redirect.appendTo(bitbucket))
                    .start();
            log.info("Starting process with args: " + Arrays.toString(args) + "\r\n pid: " + proc.pid());
        } catch (IOException e) {
            log.error("Starting process with args: " + Arrays.toString(args) + " failed");
            return -1L;
        }
        return proc.pid();
    }
}

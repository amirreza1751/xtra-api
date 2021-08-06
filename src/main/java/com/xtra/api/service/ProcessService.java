package com.xtra.api.service;

import com.zaxxer.nuprocess.NuProcess;
import com.zaxxer.nuprocess.NuProcessBuilder;
import com.zaxxer.nuprocess.NuProcessHandler;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@Log4j2
public class ProcessService {
    public void runProcess(String[] args, NuProcessHandler processHandler) {
        NuProcess proc;
        var pb = new NuProcessBuilder(args);
        pb.setProcessListener(processHandler);
        proc = pb.start();
        log.info("Starting process with args: " + Arrays.toString(args) + "\r\n pid: " + proc.getPID());
    }
}

package com.xtra.api.model;

import com.zaxxer.nuprocess.NuAbstractProcessHandler;
import com.zaxxer.nuprocess.NuProcess;

public class ProcessHandler extends NuAbstractProcessHandler {
    private NuProcess nuProcess;
    private final Runnable onExit;

    public ProcessHandler(Runnable onExit) {
        this.onExit = onExit;
    }

    @Override
    public void onStart(NuProcess nuProcess) {
        this.nuProcess = nuProcess;
    }

    @Override
    public void onExit(int statusCode) {
        onExit.run();
    }
}

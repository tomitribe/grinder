package com.tomitribe.performance.scriptengine.crest;

import net.grinder.engine.common.EngineException;
import net.grinder.scriptengine.ScriptEngineService.ScriptEngine;
import net.grinder.scriptengine.ScriptEngineService.WorkerRunnable;
import net.grinder.scriptengine.ScriptExecutionException;

public class CrestScriptEngine implements ScriptEngine {
    @Override
    public WorkerRunnable createWorkerRunnable() throws EngineException {
        return new CrestWorkerRunnable(new DefaultCrestTestRunner());
    }

    @Override
    public WorkerRunnable createWorkerRunnable(Object testRunner) throws EngineException {
        if (testRunner == null) {
            return createWorkerRunnable();
        }

        if (Runnable.class.isAssignableFrom(testRunner.getClass())) {
            final Runnable runnable = Runnable.class.cast(testRunner);
            return new CrestWorkerRunnable(runnable);
        }

        throw new CrestEngineException(testRunner.getClass().getName() + " is not runnable");
    }

    @Override
    public void shutdown() throws EngineException {

    }

    @Override
    public String getDescription() {
        return "Crest";
    }

    private final class CrestWorkerRunnable implements WorkerRunnable {

        private final Runnable runnable;

        private CrestWorkerRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        @Override
        public void run() throws ScriptExecutionException {
            runnable.run();
        }

        @Override
        public void shutdown() throws ScriptExecutionException {

        }
    }
}

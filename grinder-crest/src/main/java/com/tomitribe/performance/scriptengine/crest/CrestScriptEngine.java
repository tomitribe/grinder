package com.tomitribe.performance.scriptengine.crest;

import net.grinder.engine.common.EngineException;
import net.grinder.scriptengine.ScriptEngineService.ScriptEngine;
import net.grinder.scriptengine.ScriptEngineService.WorkerRunnable;
import net.grinder.scriptengine.ScriptExecutionException;
import net.grinder.util.Sleeper;

import java.util.concurrent.atomic.AtomicBoolean;

public class CrestScriptEngine implements ScriptEngine {

    private AtomicBoolean shutdown = new AtomicBoolean(false);

    @Override
    public WorkerRunnable createWorkerRunnable() throws EngineException {
        return new CrestWorkerRunnable(this, new DefaultCrestTestRunner());
    }

    @Override
    public WorkerRunnable createWorkerRunnable(Object testRunner) throws EngineException {
        if (testRunner == null) {
            return createWorkerRunnable();
        }

        if (Runnable.class.isAssignableFrom(testRunner.getClass())) {
            final Runnable runnable = Runnable.class.cast(testRunner);
            return new CrestWorkerRunnable(this, runnable);
        }

        throw new CrestEngineException(testRunner.getClass().getName() + " is not runnable");
    }

    @Override
    public void shutdown() throws EngineException {
        shutdown.set(true);
    }

    @Override
    public String getDescription() {
        return "Crest";
    }

    private final class CrestWorkerRunnable implements WorkerRunnable {

        private final Runnable runnable;
        private final CrestScriptEngine engine;

        private CrestWorkerRunnable(final CrestScriptEngine engine, final Runnable runnable) {
            this.engine = engine;
            this.runnable = runnable;
        }

        @Override
        public void run() throws ScriptExecutionException {
            if (engine.shutdown.get()) {
                throw new CrestScriptExecutionException("Engine shutdown called, terminate this thread",
                        new Sleeper.ShutdownException("Engine shutdown called, terminate this thread"));
            }

            runnable.run();
        }

        @Override
        public void shutdown() throws ScriptExecutionException {
            // no-op, run should not be called any more
        }
    }
}

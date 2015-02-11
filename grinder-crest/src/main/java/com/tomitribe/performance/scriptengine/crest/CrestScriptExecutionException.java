package com.tomitribe.performance.scriptengine.crest;

import net.grinder.scriptengine.ScriptExecutionException;

public class CrestScriptExecutionException extends ScriptExecutionException {
    public CrestScriptExecutionException(String message) {
        super(message);
    }

    public CrestScriptExecutionException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

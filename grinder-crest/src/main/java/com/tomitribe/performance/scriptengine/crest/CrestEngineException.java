package com.tomitribe.performance.scriptengine.crest;

import net.grinder.engine.common.EngineException;

public class CrestEngineException extends EngineException {
    public CrestEngineException(String message) {
        super(message);
    }

    public CrestEngineException(String message, Throwable throwable) {
        super(message, throwable);
    }
}

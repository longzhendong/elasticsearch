/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License;
 * you may not use this file except in compliance with the Elastic License.
 */
package org.elasticsearch.xpack.watcher.execution;

import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.xcontent.ToXContentObject;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.xpack.watcher.actions.ActionWrapper;
import org.elasticsearch.xpack.watcher.condition.Condition;
import org.elasticsearch.xpack.watcher.input.Input;
import org.elasticsearch.xpack.watcher.support.WatcherDateTimeUtils;
import org.elasticsearch.xpack.watcher.transform.Transform;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.Map;

public class WatchExecutionResult implements ToXContentObject {

    private final DateTime executionTime;
    private final long executionDurationMs;
    @Nullable private final Input.Result inputResult;
    @Nullable private final Condition.Result conditionResult;
    @Nullable private final Transform.Result transformResult;
    private final Map<String, ActionWrapper.Result> actionsResults;

    public WatchExecutionResult(WatchExecutionContext context, long executionDurationMs) {
        this(context.executionTime(), executionDurationMs, context.inputResult(), context.conditionResult(), context.transformResult(),
                context.actionsResults());
    }

    private WatchExecutionResult(DateTime executionTime, long executionDurationMs, Input.Result inputResult,
                                 Condition.Result conditionResult, @Nullable Transform.Result transformResult,
                                 Map<String, ActionWrapper.Result> actionsResults) {
        this.executionTime = executionTime;
        this.inputResult = inputResult;
        this.conditionResult = conditionResult;
        this.transformResult = transformResult;
        this.actionsResults = actionsResults;
        this.executionDurationMs = executionDurationMs;
    }

    public DateTime executionTime() {
        return executionTime;
    }

    public long executionDurationMs() {
        return executionDurationMs;
    }

    public Input.Result inputResult() {
        return inputResult;
    }

    public Condition.Result conditionResult() {
        return conditionResult;
    }

    public Transform.Result transformResult() {
        return transformResult;
    }

    public Map<String, ActionWrapper.Result> actionsResults() {
        return actionsResults;
    }

    @Override
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        builder.startObject();

        WatcherDateTimeUtils.writeDate(Field.EXECUTION_TIME.getPreferredName(), builder, executionTime);
        builder.field(Field.EXECUTION_DURATION.getPreferredName(), executionDurationMs);

        if (inputResult != null) {
            builder.field(Field.INPUT.getPreferredName(), inputResult, params);
        }
        if (conditionResult != null) {
            builder.field(Field.CONDITION.getPreferredName(), conditionResult, params);
        }
        if (transformResult != null) {
            builder.field(Transform.Field.TRANSFORM.getPreferredName(), transformResult, params);
        }
        builder.startArray(Field.ACTIONS.getPreferredName());
        for (ActionWrapper.Result result : actionsResults.values()) {
            result.toXContent(builder, params);
        }
        builder.endArray();
        builder.endObject();
        return builder;
    }

    public interface Field {
        ParseField EXECUTION_TIME = new ParseField("execution_time");
        ParseField EXECUTION_DURATION = new ParseField("execution_duration");
        ParseField INPUT = new ParseField("input");
        ParseField CONDITION = new ParseField("condition");
        ParseField ACTIONS = new ParseField("actions");
        ParseField TYPE = new ParseField("type");
    }
}

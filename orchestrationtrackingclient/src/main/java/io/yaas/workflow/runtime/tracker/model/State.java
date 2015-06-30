package io.yaas.workflow.runtime.tracker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape=JsonFormat.Shape.OBJECT)
public enum State {
	STARTED,
	FAILED,
	SUCCEEDED,
	COMPENSATED,
	UNKNOWN;
    
	@JsonValue
	public String toJson() {
		return name().toLowerCase();
	}
	
	@JsonCreator
	public static State fromJson(final String value) {
		return valueOf(value.toUpperCase());
	}}

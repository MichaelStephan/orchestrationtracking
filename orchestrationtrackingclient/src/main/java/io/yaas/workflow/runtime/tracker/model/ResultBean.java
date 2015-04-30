package io.yaas.workflow.runtime.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

/**
 * Created by i303874 on 4/30/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResultBean {

    public Map<String, Object> result;
}

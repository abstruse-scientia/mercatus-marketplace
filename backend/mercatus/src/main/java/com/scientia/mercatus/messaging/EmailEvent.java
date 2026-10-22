package com.scientia.mercatus.messaging;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
@JsonDeserialize(builder=EmailEvent.Builder.class)
public class EmailEvent {


    private final String templateName;

    private final String toEmailAddress;

    private final String dedupKey;

    private final Map<String, String> variables;

    private EmailEvent(Builder builder) {
        this.templateName = builder.templateName;
        this.toEmailAddress = builder.toEmailAddress;
        this.dedupKey = builder.dedupKey;
        this.variables = Collections.unmodifiableMap(builder.variables);
    }

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
        private String templateName;
        private String toEmailAddress;
        private String dedupKey;

        private Map<String, String> variables = new HashMap<>();

        @JsonProperty("templateName")
        public Builder templateName(String templateName) {
            this.templateName = templateName;
            return  this;
        }
        @JsonProperty("toEmailAddress")
        public Builder toEmailAddress(String toEmailAddress) {
            this.toEmailAddress = toEmailAddress;
            return this;
        }
        @JsonProperty("dedupKey")
        public Builder dedupKey(String dedupKey) {
            this.dedupKey = dedupKey;
            return this;
        }
        @JsonProperty("variables")
        public Builder variables(Map<String, String> variables) {
            if (variables != null) {
                this.variables.putAll(variables);
            }
            return this;
        }
        @JsonIgnore
        public Builder variables(String name, String value) {
            this.variables.put(name, value);
            return this;
        }

        public EmailEvent build() {
            return new EmailEvent(this);
        }
    }


}

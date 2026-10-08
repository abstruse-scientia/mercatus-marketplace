package com.scientia.mercatus.messaging;


import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Getter
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

    public static class Builder {
        private String templateName;
        private String toEmailAddress;
        private String dedupKey;

        private Map<String, String> variables = new HashMap<>();

        public Builder templateName(String templateName) {
            this.templateName = templateName;
            return  this;
        }
        public Builder toEmailAddress(String toEmailAddress) {
            this.toEmailAddress = toEmailAddress;
            return this;
        }
        public Builder dedupKey(String dedupKey) {
            this.dedupKey = dedupKey;
            return this;
        }
        public Builder variables(Map<String, String> variables) {
            if (variables != null) {
                this.variables.putAll(variables);
            }
            return this;
        }
        public Builder variables(String name, String value) {
            this.variables.put(name, value);
            return this;
        }

        public EmailEvent build() {
            return new EmailEvent(this);
        }
    }


}

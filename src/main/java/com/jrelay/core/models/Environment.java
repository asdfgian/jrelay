package com.jrelay.core.models;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
@Getter
@Setter
public class Environment {
    private String id;
    private String name;
    private List<Variable> variables;

    public Environment(String name, List<Variable> variables) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.variables = variables;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Variable {
        private boolean enabled;
        private String key;
        private VariableType type;
        private String initialValue;
        private String currentValue;
    }

    public enum VariableType {
        DEFAULT, SECRET
    }

}

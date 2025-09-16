package com.jrelay.core.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.jrelay.core.models.Environment;

import lombok.Getter;

public class EnvironmentRepository extends JsonRepository<List<Environment>> {

    @Getter
    private final List<Environment> environments;

    public EnvironmentRepository(String path) {
        super(path, new TypeReference<>() {
        });
        this.environments = loadOrDefault(new ArrayList<>());
    }

    public void save(Environment environment) {
        environments.add(environment);
        setEntity(environments);
    }

    public Environment findById(String id) {
        return environments.stream()
                .filter(env -> env.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    public void updateById(Environment environment) {
        Environment envFind = findById(environment.getId());
        if (envFind == null) {
            throw new RepositoryException("Environment with id " + environment.getId() + " not found", null);
        }
        envFind.setName(environment.getName());
        envFind.setVariables(environment.getVariables());
        setEntity(environments);
    }

    public void deleteById(String id) {
        boolean removed = environments.removeIf(e -> e.getId().equals(id));
        if (removed) {
            setEntity(environments);
        }
    }

    public Map<String, String> findVariablesByEnvironment(Environment environment) {
        if (environment == null || environment.getVariables() == null) {
            return Collections.emptyMap();
        }

        return environment.getVariables().stream()
                .filter(Environment.Variable::isEnabled)
                .collect(Collectors.toMap(
                        Environment.Variable::getKey,
                        Environment.Variable::getInitialValue,
                        (v1, v2) -> v2,
                        LinkedHashMap::new));
    }
}
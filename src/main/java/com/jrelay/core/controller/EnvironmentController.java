package com.jrelay.core.controller;

import java.util.List;
import java.util.Map;

import com.jrelay.core.models.Environment;
import com.jrelay.core.repository.EnvironmentRepository;

public class EnvironmentController {
    private final EnvironmentRepository repository;

    public EnvironmentController() {
        this.repository = new EnvironmentRepository("environment.json");
    }

    public List<Environment> findAll() {
        return repository.getEnvironments();
    }

    public void save(Environment environment) {
        repository.save(environment);
    }

    public Map<String, String> findVariablesByEnvironment(Environment environment) {
        return repository.findVariablesByEnvironment(environment);
    }

    public void update(Environment environment){
        repository.updateById(environment);
    }

    public Environment find(String id) {
        return repository.findById(id);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }
}

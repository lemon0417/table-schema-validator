package com.example.customvalidator.validation.service;

import com.example.customvalidator.validation.config.ClazzSchemaComponent;
import com.example.customvalidator.validation.config.DatabaseSchemaComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class SchemaService {

    @Autowired
    private DatabaseSchemaComponent databaseSchema;

    @Autowired
    private ClazzSchemaComponent clazzSchema;

    @Autowired
    private List<DataSource> dataSources;

    public void refresh() {
        databaseSchema.databaseSchema(dataSources);

        Set<Class<?>> keys = new HashSet<>(clazzSchema.findAll().keySet());
        clazzSchema.clear();
        for (Class<?> clazz : keys) {
            ClazzSchemaComponent.findByClazz(clazz);
        }
    }
}

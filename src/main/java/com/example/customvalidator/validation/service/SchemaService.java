package com.example.customvalidator.validation.service;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

@Service
public class SchemaService implements InitializingBean {
    @Autowired
    private List<DataSource> dataSources;

    @Override
    public void afterPropertiesSet() {
        refresh();
    }

    public void refresh() {
        DatabaseSchemaAware.refresh(dataSources);
        ClazzSchemaAware.refresh();
    }
}

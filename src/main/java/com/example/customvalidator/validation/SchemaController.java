package com.example.customvalidator.validation;

import com.example.customvalidator.validation.config.ClazzSchemaComponent;
import com.example.customvalidator.validation.config.DatabaseSchemaComponent;
import com.example.customvalidator.validation.service.SchemaService;
import com.example.customvalidator.validation.vo.ColumnInfo;
import com.example.customvalidator.validation.vo.FieldInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Api(tags = "資料表結構")
@RequestMapping("/schema")
@RestController
public class SchemaController {
    @Autowired
    private SchemaService service;

    @ApiOperation(value = "dbSchema")
    @GetMapping("dbSchema")
    public Map<String, Map<String, ColumnInfo>> getDataBaseSchema() {
        return DatabaseSchemaComponent.getDataBaseSchema();
    }

    @ApiOperation(value = "classSchema")
    @GetMapping("classSchema")
    public ConcurrentMap<Class<?>, List<FieldInfo>> getClassSchema() {
        return ClazzSchemaComponent.getClassSchema();
    }

    @ApiOperation("更新")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping
    public void refresh() {
        service.refresh();
    }
}

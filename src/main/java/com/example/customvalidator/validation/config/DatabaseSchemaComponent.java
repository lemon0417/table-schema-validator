package com.example.customvalidator.validation.config;

import com.example.customvalidator.validation.vo.ColumnInfo;
import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.persistence.Table;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;

@Component
public class DatabaseSchemaComponent {
    private static final Map<String, Map<String, ColumnInfo>> CACHE = new HashMap<>();

    public static Map<String, Map<String, ColumnInfo>> getDataBaseSchema() {
        return CACHE;
    }

    @Bean
    public Map<String, Map<String, ColumnInfo>> databaseSchema(List<DataSource> dataSources) {
        CACHE.clear();
        for (DataSource dataSource : dataSources) {
            try {
                Map<String, Map<String, ColumnInfo>> result = getMetaData(dataSource);
                CACHE.putAll(result);
            } catch (MetaDataAccessException e) {
                e.printStackTrace();
            }
        }
        return CACHE;
    }

    private Map<String, Map<String, ColumnInfo>> getMetaData(DataSource dataSource) throws MetaDataAccessException {
        return JdbcUtils.extractDatabaseMetaData(dataSource,
                databaseMetaData -> {
                    Map<String, Map<String, ColumnInfo>> map = new HashMap<>();
                    ResultSet rs = databaseMetaData.getColumns(null, null, "%", null);
                    while (rs.next()) {
                        Class<?> dataType = JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(rs.getInt("DATA_TYPE"));
                        boolean nullable = rs.getInt("NULLABLE") == 1;
                        ColumnInfo info = new ColumnInfo(
                                dataType
                                , rs.getInt("COLUMN_SIZE")
                                , trimToDefault(rs.getString("COLUMN_DEF"), nullable)
                                , nullable
                        );

                        String tableName = rs.getString("TABLE_NAME");
                        String columnName = rs.getString("COLUMN_NAME");
                        Map<String, ColumnInfo> tableInfo = map.getOrDefault(tableName, new HashMap<>());
                        tableInfo.put(columnName, info);
                        map.put(tableName, tableInfo);
                    }
                    return map;
                });
    }

    private String trimToDefault(String str, boolean nullable) {
        final String DEFAULT_STRING = nullable ? null : "";

        if (str != null && str.trim().equalsIgnoreCase("null")) {
            return DEFAULT_STRING;
        }
        return str == null ? DEFAULT_STRING : str.trim();
    }

    public static ColumnInfo getColumnInfo(Class<?> targetEntity, String columnName) {
        String tableName = toTableName(targetEntity);
        Map<String, ColumnInfo> table = CACHE.get(tableName);
        Assert.notNull(table, tableName + " is not a valid table.");
        ColumnInfo column = table.get(columnName);
        Assert.notNull(column, columnName + " is not a valid column in [" + tableName + "].");
        return column;
    }

    private static String toTableName(Class<?> clazz) {
        String name;
        Table table = clazz.getAnnotation(Table.class);
        if (table != null && !table.name().isEmpty()) {
            name = table.name().replaceAll("`", "");
        } else {
            name = UPPER_CAMEL.to(LOWER_UNDERSCORE, clazz.getSimpleName());
        }
        return name;
    }
}

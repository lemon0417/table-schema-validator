package com.example.customvalidator.config;

import com.example.customvalidator.validation.util.TransformerUtil;
import com.example.customvalidator.validation.vo.ColumnInfo;
import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DatabaseSchemaComponent {
    private static Map<String, Map<String, ColumnInfo>> DATABASE_SCHEMA_CACHE;

    public static Map<String, Map<String, ColumnInfo>> getDataBaseSchema() {
        return DATABASE_SCHEMA_CACHE;
    }

    @Bean
    public Map<String, Map<String, ColumnInfo>> databaseSchema(List<DataSource> dataSources) {
        Map<String, Map<String, ColumnInfo>> map = new HashMap<>();
        for (DataSource dataSource : dataSources) {
            try {
                Map<String, Map<String, ColumnInfo>> result = getMetaData(dataSource);
                map.putAll(result);
            } catch (MetaDataAccessException e) {
                e.printStackTrace();
            }
        }
        DATABASE_SCHEMA_CACHE = map;
        return map;
    }

    private Map<String, Map<String, ColumnInfo>> getMetaData(DataSource dataSource) throws MetaDataAccessException {
        return JdbcUtils.extractDatabaseMetaData(dataSource,
                databaseMetaData -> {
                    Map<String, Map<String, ColumnInfo>> map = new HashMap<>();
                    ResultSet rs = databaseMetaData.getColumns(null, null, "%", null);
                    while (rs.next()) {
                        Class<?> dataType = JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(rs.getInt("DATA_TYPE"));
                        boolean nullable = rs.getInt("NULLABLE") == 1;
                        ColumnInfo info = ColumnInfo.builder()
                                .dataType(dataType)
                                .columnSize(rs.getInt("COLUMN_SIZE"))
                                .columnDef(trimToDefault(rs.getString("COLUMN_DEF"), nullable))
                                .nullable(nullable)
                                .build();

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

    public static ColumnInfo getColumnInfo(String key, String columnName) {
        String tableName = TransformerUtil.toUnderscoreNaming(key);
        Map<String, ColumnInfo> table = DATABASE_SCHEMA_CACHE.get(tableName);
        Assert.notNull(table, tableName + " is not a valid table.");
        ColumnInfo column = table.get(columnName);
        Assert.notNull(column, columnName + " is not a valid column in [" + tableName + "].");
        return column;
    }
}

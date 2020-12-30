package com.example.customvalidator.validation.service;

import com.example.customvalidator.validation.vo.ColumnInfo;
import org.hibernate.type.descriptor.sql.JdbcTypeJavaClassMappings;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseSchemaAware {
    private static final Map<String, Map<String, ColumnInfo>> CACHE = new HashMap<>();
    private static final String[] EXCLUDE_COLUMN_PREFIX = new String[]{
            "QRTZ_"
    };

    public static Map<String, Map<String, ColumnInfo>> get() {
        return CACHE;
    }

    public static void refresh(List<DataSource> dataSources) {
        for (DataSource dataSource : dataSources) {
            try {
                synchronized (dataSource) {
                    parse(dataSource);
                }
            } catch (MetaDataAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private static void parse(DataSource dataSource) throws MetaDataAccessException {
        JdbcUtils.extractDatabaseMetaData(dataSource,
                databaseMetaData -> {
                    ResultSet rs = databaseMetaData.getColumns(null, null, "%", null);
                    while (rs.next()) {
                        String columnName = rs.getString("COLUMN_NAME");
                        if (checkExclude(columnName)) continue;

                        Class<?> dataType = JdbcTypeJavaClassMappings.INSTANCE.determineJavaClassForJdbcTypeCode(rs.getInt("DATA_TYPE"));
                        boolean nullable = rs.getInt("NULLABLE") == 1;
                        ColumnInfo info = new ColumnInfo(
                                dataType
                                , rs.getInt("COLUMN_SIZE")
                                , trimToDefault(rs.getString("COLUMN_DEF"), nullable)
                                , nullable
                        );
                        String tableName = rs.getString("TABLE_NAME");
                        Map<String, ColumnInfo> tableInfo = CACHE.getOrDefault(tableName, new HashMap<>());
                        tableInfo.put(columnName, info);
                        CACHE.put(tableName, tableInfo);
                    }
                    return CACHE;
                });
    }

    public static ColumnInfo getColumnInfo(
            String tableName
            , String columnName
    ) {
        Map<String, ColumnInfo> table = CACHE.get(tableName);
        Assert.notNull(table, tableName + " is not a valid table.");
        return table.get(columnName);
    }

    private static String trimToDefault(
            String str
            , boolean nullable
    ) {
        final String DEFAULT_STRING = nullable ? null : "";
        if (str != null && str.trim().equalsIgnoreCase("null")) {
            return DEFAULT_STRING;
        }
        return str == null ? DEFAULT_STRING : str.trim();
    }

    private static boolean checkExclude(String columnName) {
        for (String rule : EXCLUDE_COLUMN_PREFIX) {
            if (columnName.startsWith(rule))
                return true;
        }
        return false;
    }
}

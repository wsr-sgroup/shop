package com.project.platform.typehandler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes(JsonNode.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonNodeTypeHandler extends BaseTypeHandler<JsonNode> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JsonNode parameter, JdbcType jdbcType)
            throws SQLException {
        try {
            ps.setString(i, MAPPER.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("Failed to convert JsonNode to String", e);
        }
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        if (json == null)
            return null;
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            throw new SQLException("Failed to parse JSON from column: " + columnName, e);
        }
    }

    @Override
    public JsonNode getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        if (json == null)
            return null;
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            throw new SQLException("Failed to parse JSON from column index: " + columnIndex, e);
        }
    }

    @Override
    public JsonNode getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        if (json == null)
            return null;
        try {
            return MAPPER.readTree(json);
        } catch (Exception e) {
            throw new SQLException("Failed to parse JSON from callable column index: " + columnIndex, e);
        }
    }
}

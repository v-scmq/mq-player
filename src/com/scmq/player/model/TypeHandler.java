package com.scmq.player.model;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;

@MappedJdbcTypes(JdbcType.INTEGER)
public class TypeHandler extends BaseTypeHandler<SortMethod> {

    @Override
    public SortMethod getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return SortMethod.valueOf(rs.getInt(columnName));
    }

    @Override
    public SortMethod getNullableResult(ResultSet rs, int column) throws SQLException {
        return SortMethod.valueOf(rs.getInt(column));
    }

    @Override
    public SortMethod getNullableResult(CallableStatement cs, int column) throws SQLException {

        return SortMethod.valueOf(cs.getInt(column));
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int column, SortMethod value, JdbcType dbType) throws SQLException {
        ps.setInt(column, value.getCode());
    }
}
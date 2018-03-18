package com.zchi.common.mybatis.handler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by zchi on 16/6/16.
 */
public class CustomEnumTypeHandler extends BaseTypeHandler<BaseEnum> {

    private Class<BaseEnum> type;

    private final BaseEnum[] enums;

    public CustomEnumTypeHandler(Class<BaseEnum> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();
        if (this.enums == null) {
            throw new IllegalArgumentException(type.getSimpleName());
        }
    }

    @Override public void setNonNullParameter(PreparedStatement ps, int i, BaseEnum parameter,
        JdbcType jdbcType) throws SQLException {
        ps.setInt(i, parameter.getValue());
    }

    @Override public BaseEnum getNullableResult(ResultSet rs, String columnName)
        throws SQLException {
        int index = rs.getInt(columnName);
        if (rs.wasNull()) {
            return null;
        } else {
            return locateEnumStatus(index);
        }

    }

    @Override public BaseEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        int index = rs.getInt(columnIndex);
        if (rs.wasNull()) {
            return null;
        } else {
            return locateEnumStatus(index);
        }
    }

    @Override public BaseEnum getNullableResult(CallableStatement cs, int columnIndex)
        throws SQLException {
        int index = cs.getInt(columnIndex);
        if (cs.wasNull()){
            return null;
        } else {
            return locateEnumStatus(index);
        }
    }

    private BaseEnum locateEnumStatus(int code) {
        for (BaseEnum status : enums) {
            if (status.getValue() == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的枚举类型：" + code + ",请核对" + type.getSimpleName());
    }
}

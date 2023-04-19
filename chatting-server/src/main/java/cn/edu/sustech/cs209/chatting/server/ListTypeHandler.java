package cn.edu.sustech.cs209.chatting.server;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.postgresql.util.PGobject;

public class ListTypeHandler extends BaseTypeHandler<List<Integer>> {

    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<Integer> integers, JdbcType jdbcType) throws SQLException {
        if (integers == null) {
            preparedStatement.setNull(i, Types.ARRAY, "_int4");
            return;
        }
        PGobject pgObject = new PGobject();
        pgObject.setType("_int4");
        pgObject.setValue(Arrays.toString(integers.toArray()).replace("[", "{").replace("]", "}"));
        preparedStatement.setObject(i, pgObject);
    }


    @Override
    public List<Integer> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        Array array = resultSet.getArray(s);
        if (array == null) {
            return null;
        }
        Integer[] intArray = (Integer[]) array.getArray();
        return Arrays.asList(intArray);
    }

    @Override
    public List<Integer> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        Array array = resultSet.getArray(i);
        if (array == null) {
            return null;
        }
        Integer[] intArray = (Integer[]) array.getArray();
        return Arrays.asList(intArray);
    }

    @Override
    public List<Integer> getNullableResult(CallableStatement callableStatement, int i)
            throws SQLException {
        Array array = callableStatement.getArray(i);
        if (array == null) {
            return null;
        }
        Integer[] integers = (Integer[]) array.getArray();
        return Arrays.asList(integers);
    }

}

package org.obridge.coconut.data.jdbc;

import org.junit.Assert;
import org.junit.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JdbcTemplateTest {

    @Test
    public void t1() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceProvider.dataSource());

        Integer integer = jdbcTemplate.query("SELECT count(1) FROM information_schema.tables", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getInt(1);
            }
        }).get(0);

        Assert.assertTrue(integer > 0);

    }

    @Test
    public void t2() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceProvider.dataSource());
        List<String> objects = jdbcTemplate.queryForList("select * from information_schema.tables");
        Assert.assertTrue(objects.size() > 0);
    }

    @Test
    public void t3() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceProvider.dataSource());

        List<String> tableName = jdbcTemplate.query("select * from information_schema.tables where table_name like ?", new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("TABLE_NAME");
            }
        }, "T%");

        Assert.assertTrue(tableName.contains("TABLES"));
        Assert.assertTrue(tableName.contains("TRIGGERS"));
        Assert.assertTrue(tableName.contains("TYPE_INFO"));

    }

    @Test
    public void t4() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceProvider.dataSource());

        try {
            List<Object> objects = jdbcTemplate.queryForList("select * from information_schema.tablesssss");
            Assert.fail("Exception Should be thrown");
        } catch (JdbcTemplateException e) {

        }

    }

    @Test
    public void t5() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceProvider.dataSource());

        List<Integer> tableName = jdbcTemplate.query("select * from information_schema.tables where table_name like ?", new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet resultSet, int i) throws SQLException {
                return i;
            }
        }, "T%");


        Integer integer = tableName.stream().max(Integer::compareTo).get();
        Assert.assertTrue(integer > 3);
    }

    @Test
    public void t6() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceProvider.dataSource());

        List<Object> objects = jdbcTemplate.queryForList("select * from information_schema.tables where 1=2");

        Assert.assertEquals(0, objects.size());

    }

    @Test
    public void t7() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceProvider.dataSource());

        List<Map<String, String>> query = jdbcTemplate.query("select * from information_schema.tables where table_name like ?", new StringMapRowMaper(), "T%");


        System.out.println(query);

        String json = "{" + query.get(0).entrySet().stream()
                .map(e -> "\"" + e.getKey() + "\"" + ":\"" + String.valueOf(e.getValue()) + "\"")
                .collect(Collectors.joining(", ")) + "}";

        System.out.println(json);


        Integer integer = query.size();
        Assert.assertTrue(integer > 3);

        Assert.assertTrue(json.contains("\"tableType\":\"SYSTEM TABLE\""));
        Assert.assertTrue(json.contains("\"sql\":\"null\", \"tableClass\":\"org.h2.table.MetaTable\""));
    }

    @Test
    public void t8() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(TestDataSourceProvider.dataSource());

        List<String> query = jdbcTemplate.query("select * from information_schema.tables where table_name like ?", new JsonRowMapper(), "T%");


        System.out.println(query.get(0));


        Integer integer = query.size();
        Assert.assertTrue(integer > 3);

        String json = query.toString();

        Assert.assertTrue(json.contains("\"tableType\":\"SYSTEM TABLE\""));
        Assert.assertTrue(json.contains("\"sql\":\"null\", \"tableClass\":\"org.h2.table.MetaTable\""));
    }


}
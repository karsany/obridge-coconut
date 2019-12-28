package org.obridge.coconut.incubator.db;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddUserCommand implements DatabaseCommand {
    private final String name;
    private final String password;

    public AddUserCommand(String name, String password) {
        this.name = name;
        this.password = password;
    }

    @Override
    public List<Statement> commands() {

        return Arrays.asList(
                new Statement() {
                    @Override
                    public String sql() {
                        return "insert into users(name, password, createdt) values (:name, :password, sysdate)";
                    }

                    @Override
                    public Map<String, Object> variables() {
                        final HashMap<String, Object> params = new HashMap<>();
                        params.put("name", name);
                        params.put("password", password);
                        return params;
                    }
                }
        );
    }
}

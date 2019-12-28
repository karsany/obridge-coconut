package org.obridge.coconut.incubator.method2refined;

import org.json.JSONObject;

public class JsonAddUserCommand implements AddUserCommand {

    private final JSONObject json;

    public JsonAddUserCommand(String json) {
        this.json = new JSONObject(json);
    }


    @Override
    public String name() {
        return json.getString("name");
    }

    @Override
    public String toString() {
        return json.toString(2);
    }

    @Override
    public String password() {
        return json.getString("password");
    }
}

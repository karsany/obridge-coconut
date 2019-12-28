package org.obridge.coconut.incubator.db;

import java.util.Map;

public interface Statement {

    String sql();

    Map<String, Object> variables();

}

package org.obridge.coconut.query.query;

import org.obridge.coconut.query.exception.QueryException;
import org.obridge.coconut.query.interfaces.Query;
import org.obridge.coconut.query.util.StringHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class ResourceFileQuery implements Query {

    @Override
    public String sql() {
        throw new UnsupportedOperationException("Resource file must be used with method parameter");
    }

    @Override
    public String sql(Method m) {
        String sqlFileName = m.getDeclaringClass()
                .getSimpleName()
                + "_" + StringHelper.capitalize(m.getName()) + ".sql";

        try (BufferedReader bfr = new BufferedReader((new InputStreamReader(m.getDeclaringClass()
                .getResourceAsStream(sqlFileName),
                StandardCharsets.UTF_8)))) {
            return bfr.lines()
                    .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new QueryException(e);
        }
    }

}

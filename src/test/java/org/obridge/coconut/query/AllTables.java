package org.obridge.coconut.query;

import org.obridge.coconut.json.JsonString;
import org.obridge.coconut.query.annotation.Bind;
import org.obridge.coconut.query.annotation.QuerySource;
import org.obridge.coconut.query.query.AutomaticTableQuery;

import java.util.List;

public interface AllTables {

    @QuerySource(AutomaticTableQuery.class)
    List<Table> getAllTables();

    @QuerySource(AutomaticTableQuery.class)
    List<Table> getAllTablesByOwner(@Bind("owner") String owner);

    interface Table extends JsonString {

        String owner();

        String tableName();

    }
}

package org.obridge.coconut.incubator.db;

import java.util.List;

public interface DatabaseCommand {

    List<Statement> commands();

}

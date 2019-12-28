package org.obridge.coconut.data.transaction;

import org.obridge.coconut.data.session.DatabaseSession;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface Transaction {

    // csak fut
    void apply(Consumer<DatabaseSession> c);

    // várunk eredményt
    <T> T apply(Function<DatabaseSession, T> c);

    // nem várunk eredmény, viszont input van
    <T> void apply(BiConsumer<DatabaseSession, T> t, T u);

    // várunk eredményt és input is van
    <T, R> R apply(BiFunction<DatabaseSession, T, R> t, T u);

    // trx kezelés
    void commit();

    void rollback();

    // trx lánc
    TransactionChain chain();

}

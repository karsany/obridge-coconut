package org.obridge.coconut.data.transaction;

import org.obridge.coconut.data.session.DatabaseSession;

import java.util.function.Consumer;
import java.util.function.Function;

public interface TransactionChain {

    TransactionChain commit();

    TransactionChain rollback();

    TransactionChain transact(Consumer<DatabaseSession> c);

    <Q> TransactionChainData<Q> get(Function<DatabaseSession, Q> f);

    TransactionChain process(Runnable r);
}

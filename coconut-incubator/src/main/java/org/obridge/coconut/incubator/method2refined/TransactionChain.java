package org.obridge.coconut.incubator.method2refined;

import java.util.function.Consumer;
import java.util.function.Function;

public interface TransactionChain {

    TransactionChain commit();

    TransactionChain rollback();

    TransactionChain apply(Consumer<DBConnection> c);

    <Q> TransactionChainDataHolder<Q> get(Function<DBConnection, Q> f);

    TransactionChain callback(Runnable r);
}

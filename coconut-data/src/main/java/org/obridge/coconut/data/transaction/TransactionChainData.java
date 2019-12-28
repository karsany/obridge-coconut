package org.obridge.coconut.data.transaction;

import org.obridge.coconut.data.session.DatabaseSession;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public interface TransactionChainData<T> extends TransactionChain {

    TransactionChain transact(BiConsumer<DatabaseSession, T> c);

    <Q> TransactionChainData<Q> get(BiFunction<DatabaseSession, T, Q> f);

    TransactionChainData<T> process(Consumer<T> t);
}

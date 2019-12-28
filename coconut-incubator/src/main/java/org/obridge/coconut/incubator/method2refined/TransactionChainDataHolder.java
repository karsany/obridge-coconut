package org.obridge.coconut.incubator.method2refined;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface TransactionChainDataHolder<T> extends TransactionChain {

    TransactionChain put(BiConsumer<DBConnection, T> c);

    <Q> TransactionChainDataHolder<Q> putAndGet(BiFunction<DBConnection, T, Q> f);

    TransactionChainDataHolder<T> callback(Consumer<T> t);
}

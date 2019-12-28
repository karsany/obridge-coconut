package org.obridge.coconut.incubator.method2refined;

import java.util.function.Consumer;
import java.util.function.Function;

public class GenericTransactionChain implements TransactionChain {
    private final Transaction trx;

    public GenericTransactionChain(Transaction trx) {
        this.trx = trx;
    }

    @Override
    public TransactionChain commit() {
        this.trx.commit();
        return this;
    }

    @Override
    public TransactionChain rollback() {
        this.trx.rollback();
        return this;
    }

    @Override
    public TransactionChain apply(Consumer<DBConnection> c) {
        trx.apply(c::accept);
        return this;
    }

    @Override
    public <Q> TransactionChainDataHolder<Q> get(Function<DBConnection, Q> f) {
        final Q ret = trx.ret(dbConnection -> f.apply(dbConnection));
        return new GenereicTransactionChainDataHolder<Q>(trx, ret);
    }

    @Override
    public TransactionChain callback(Runnable r) {
        r.run();
        return this;
    }


}

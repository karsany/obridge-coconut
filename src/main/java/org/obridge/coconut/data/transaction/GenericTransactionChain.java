package org.obridge.coconut.data.transaction;

import org.obridge.coconut.data.session.DatabaseSession;

import java.util.function.Consumer;
import java.util.function.Function;

public class GenericTransactionChain implements TransactionChain {
    protected final Transaction trx;

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
    public TransactionChain transact(Consumer<DatabaseSession> c) {
        trx.apply(c);
        return this;
    }

    @Override
    public <Q> TransactionChainData<Q> get(Function<DatabaseSession, Q> f) {
        return new GenereicTransactionChainData<Q>(trx, trx.apply(f));
    }

    @Override
    public TransactionChain process(Runnable r) {
        r.run();
        return this;
    }


}

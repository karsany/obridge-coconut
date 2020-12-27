package org.obridge.coconut.data.transaction;

import org.obridge.coconut.data.session.DatabaseSession;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class GenereicTransactionChainData<Q> extends GenericTransactionChain implements TransactionChainData<Q> {

    private final Q ret;

    public GenereicTransactionChainData(Transaction trx, Q ret) {
        super(trx);
        this.ret = ret;
    }

    @Override
    public TransactionChain transact(BiConsumer<DatabaseSession, Q> c) {
        c.andThen((dbConnection, q) -> trx.apply((dbConnection1, o) -> dbConnection1, q));
        return this;
    }

    @Override
    public <Q1> TransactionChainData<Q1> get(BiFunction<DatabaseSession, Q, Q1> f) {

        final DatabaseSession[] dbs = new DatabaseSession[1];
        trx.apply(databaseSession -> {
            return dbs[0] = databaseSession;
        });

        return new GenereicTransactionChainData<>(this.trx, f.apply(dbs[0], this.ret));

    }

    @Override
    public TransactionChainData<Q> process(Consumer<Q> t) {
        t.accept(this.ret);
        return this;
    }

}

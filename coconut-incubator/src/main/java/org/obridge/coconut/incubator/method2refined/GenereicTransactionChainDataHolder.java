package org.obridge.coconut.incubator.method2refined;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class GenereicTransactionChainDataHolder<Q> extends GenericTransactionChain implements TransactionChainDataHolder<Q> {

    private final Transaction trx;
    private final Q ret;

    public GenereicTransactionChainDataHolder(Transaction trx, Q ret) {
        super(trx);
        this.trx = trx;
        this.ret = ret;
    }

    @Override
    public TransactionChain put(BiConsumer<DBConnection, Q> c) {
        c.andThen((dbConnection, q) -> trx.apply((dbConnection1, o) -> dbConnection1, q));
        return this;
    }

    @Override
    public <Q1> TransactionChainDataHolder<Q1> putAndGet(BiFunction<DBConnection, Q, Q1> f) {
        final DBConnection[] dbc = new DBConnection[1];

        this.trx.apply(new DBCommand() {
            @Override
            public void run(DBConnection db) {
                dbc[0] = db;
            }
        });

        final Q1 apply = f.apply(dbc[0], this.ret);

        return new GenereicTransactionChainDataHolder<Q1>(trx, apply);
    }

    @Override
    public TransactionChainDataHolder<Q> callback(Consumer<Q> t) {
        t.accept(this.ret);
        return this;
    }

}

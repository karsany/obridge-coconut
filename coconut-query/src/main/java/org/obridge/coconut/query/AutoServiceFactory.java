package org.obridge.coconut.query;

import javax.sql.DataSource;
import java.lang.reflect.Proxy;

public final class AutoServiceFactory {

    private AutoServiceFactory() {
    }

    @SuppressWarnings("unchecked")
    public static <T> T init(DataSource ds, Class<T> clazz) {
        return (T) Proxy.newProxyInstance(AutoServiceFactory.class.getClassLoader(),
                                          new Class[] { clazz },
                                          new AutoServiceInvocationHandler(ds));
    }

}

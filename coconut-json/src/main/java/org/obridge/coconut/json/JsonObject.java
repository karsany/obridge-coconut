package org.obridge.coconut.json;

import org.json.JSONArray;
import org.json.JSONObject;
import org.obridge.coconut.converter.Converter;
import org.obridge.coconut.converter.Converters;
import org.obridge.coconut.converter.exception.ConverterNotFoundException;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public final class JsonObject {

    private static <T> T create(Class<T> clazz, JSONObject jo) throws ConverterNotFoundException {

        Map<String, Object> methodResultMap = new HashMap<>();

        for (Method method : clazz.getMethods()) {

            if (method.getParameterCount() == 0 && method.getReturnType() != Void.TYPE) {

                String jsonKey = null;
                if (jo.has(method.getName())) {
                    jsonKey = method.getName();
                } else if (jo.has(method.getName()
                        .substring(3, 4)
                        .toLowerCase()
                        + method.getName()
                        .substring(4))
                        && method.getName()
                        .startsWith("get")) {

                    jsonKey = method.getName()
                            .substring(3, 4)
                            .toLowerCase()
                            + method.getName()
                            .substring(4);

                } else {

                    methodResultMap.put(method.getName(), null);

                }

                if (jsonKey != null) {

                    final Object o = jo.get(jsonKey);

                    if (o.getClass()
                            .equals(method.getReturnType())) {
                        methodResultMap.put(method.getName(), o);
                    } else if (o.getClass()
                            .equals(JSONObject.class)) {
                        methodResultMap.put(method.getName(),
                                JsonObject.create(method.getReturnType(), (JSONObject) o));
                    } else if (o.getClass()
                            .equals(JSONArray.class)) {

                        final Type genericReturnType = method.getGenericReturnType();
                        Class c = (Class<?>) ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];

                        Collection coll = new ArrayList();

                        final JSONArray arr = (JSONArray) o;
                        for (Object joo : arr) {
                            coll.add(JsonObject.create(c, (JSONObject) joo));
                        }

                        methodResultMap.put(method.getName(), coll);
                    } else {
                        final Converter converter = Converters.getConverter(o.getClass(), method.getReturnType());
                        methodResultMap.put(method.getName(), converter.convert(o));
                    }

                }

            }

        }

        return (T) Proxy.newProxyInstance(
                JsonObject.class.getClassLoader(),
                new Class[]{clazz},
                new InvocationHandler() {

                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {

                        return methodResultMap.get(method.getName());
                    }
                });

    }

    public static <T> T create(Class<T> clazz, String jsonString) throws ConverterNotFoundException {
        JSONObject jo = new JSONObject(jsonString);
        return create(clazz, jo);
    }

}

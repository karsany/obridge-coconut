package org.obridge.coconut.configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Profile<T> {

    private final T t;

    public Profile(T development, T production) {
        String profileName = PROP.get("application.profile");
        this.t = profileName.equals("dev") ? development : production;


        log.info(profileName + " profile: " + this.t.getClass().getName() + " initialized");


    }

    public T get() {
        return t;
    }

}

package org.obridge.coconut.server;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ServerConfiguration {

    ObjectMapper getObjectMapper();

    int getPort();

}

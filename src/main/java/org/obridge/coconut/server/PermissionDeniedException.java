package org.obridge.coconut.server;

public class PermissionDeniedException extends RuntimeException {

    public PermissionDeniedException() {
        super("Permission denied");
    }
}

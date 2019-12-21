package org.obridge.coconut.command;

import java.util.UUID;

public abstract class IdentifiableAbstractCommand implements Command, Identifiable {

    private UUID uuid = UUID.randomUUID();

    @Override
    public UUID getUUID() {
        return this.uuid;
    }
}

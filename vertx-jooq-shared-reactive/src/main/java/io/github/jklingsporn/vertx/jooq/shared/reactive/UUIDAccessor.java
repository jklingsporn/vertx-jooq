package io.github.jklingsporn.vertx.jooq.shared.reactive;

import io.vertx.core.buffer.Buffer;
import io.vertx.sqlclient.Row;

import java.util.UUID;

public class UUIDAccessor {
    public static UUID getUUID(Row row, String field) {
        Buffer buffer = row.getBuffer(field);
        if (buffer == null) {
            return null;
        }
        return new UUID(buffer.getLong(0), buffer.getLong(8));
    }
}

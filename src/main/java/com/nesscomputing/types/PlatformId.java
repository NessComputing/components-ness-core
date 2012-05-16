package com.nesscomputing.types;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.UUID;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

import com.google.common.base.Function;

/**
 * Typed UUID which ensures you do not mix ids of one service with another.
 *
 * @param <T> the type for which this ID can reference
 */
public final class PlatformId<T>
{
    private static final Function<PlatformId<?>, UUID> TO_UUID = new Function<PlatformId<?>, UUID>() {
        @Override
        @CheckForNull
        public UUID apply(@Nullable final PlatformId<?> id) {
            return id == null ? null : id.getId();
        }
    };

    private static final Function<UUID, PlatformId<?>> FROM_UUID = new Function<UUID, PlatformId<?>>() {
        @Override
        public PlatformId<?> apply(@Nullable final UUID uuid)
        {
            return PlatformId.fromUuid(uuid);
        }
    };

    private static final Function<String, PlatformId<?>> FROM_STRING = new Function<String, PlatformId<?>>() {
        @Override
        public PlatformId<?> apply(@Nullable final String input)
        {
            return PlatformId.valueOf(input);
        }
    };

    private static final Function<PlatformId<?>, String> TO_STRING = new Function<PlatformId<?>, String>() {
        @Override
        @CheckForNull
        public String apply(@Nullable final PlatformId<?> input)
        {
            return input == null ? null : input.getValue();
        }
    };

    private static final Function<PlatformId<?>, byte[]> TO_BYTES = new Function<PlatformId<?>, byte[]>() {
        @Override
        @CheckForNull
        public byte[] apply(@Nullable PlatformId<?> input) {
            if (input != null) {
                final ByteBuffer buffer = ByteBuffer.allocate(128 / 8);
                final LongBuffer longBuffer = buffer.asLongBuffer();
                longBuffer.put(input.uuid.getMostSignificantBits());
                longBuffer.put(input.uuid.getLeastSignificantBits());
                return buffer.array();
            }
            else {
                return null;
            }
        }
    };

    private static final Function<byte[], PlatformId<?>> FROM_BYTES = new Function<byte[], PlatformId<?>>() {
        @Override
        public PlatformId<?> apply(@Nullable final byte[] input) {
            if (input != null) {
                LongBuffer buffer = ByteBuffer.wrap(input).asLongBuffer();
                return PlatformId.fromUuid(new UUID(buffer.get(), buffer.get()));
            }
            else {
                return PlatformId.valueOf(null);
            }
        }
    };

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<byte[], PlatformId<T>> fromBytesFunction() {
        return (Function) FROM_BYTES;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<UUID, PlatformId<T>> fromUuidFunction() {
        return (Function) FROM_UUID;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<String, PlatformId<T>> fromStringFunction() {
        return (Function) FROM_STRING;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<PlatformId<T>, byte[]> toBytesFunction() {
        return (Function) TO_BYTES;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<PlatformId<T>, UUID> toUuidFunction() {
        return (Function) TO_UUID;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<PlatformId<T>, String> toStringFunction() {
        return (Function) TO_STRING;
    }


    @JsonCreator
    public static <T> PlatformId<T> valueOf(@Nullable final String value)
    {
        return new PlatformId<T>(value == null ? null : UUID.fromString(value));
    }

    public static <T> PlatformId<T> fromUuid(@Nullable final UUID uuid)
    {
        return new PlatformId<T>(uuid == null ? null : uuid);
    }

    private final UUID uuid;

    private PlatformId(@Nullable final UUID uuid)
    {
        this.uuid = uuid;
    }

    public UUID getId()
    {
        return uuid;
    }

    @JsonValue
    public String getValue()
    {
        return uuid == null ? null : uuid.toString();
    }

    @Override
    public String toString()
    {
        return "PlatformId [uuid=" + uuid + "]";
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof PlatformId))
            return false;
        PlatformId<?> castOther = (PlatformId<?>) other;
        return new EqualsBuilder().append(uuid, castOther.uuid).isEquals();
    }

    private transient int hashCode;


    @Override
    public int hashCode()
    {
        if (hashCode == 0) {
            hashCode = new HashCodeBuilder().append(uuid).toHashCode();
        }
        return hashCode;
    }
}

/**
 * Copyright (C) 2012 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.types;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;
import java.util.UUID;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

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
        @CheckForNull
        public PlatformId<?> apply(@Nullable final UUID uuid)
        {
            return PlatformId.fromUuid(uuid);
        }
    };

    private static final Function<String, PlatformId<?>> FROM_STRING = new Function<String, PlatformId<?>>() {
        @Override
        @CheckForNull
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
        @CheckForNull
        public PlatformId<?> apply(@Nullable final byte[] input) {
            if (input != null) {
                LongBuffer buffer = ByteBuffer.wrap(input).asLongBuffer();
                return PlatformId.fromUuid(new UUID(buffer.get(), buffer.get()));
            }
            else {
                return null;
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
    @CheckForNull
    public static <T> PlatformId<T> valueOf(@Nullable final String value)
    {
        return value == null ? null : new PlatformId<T>(UUID.fromString(value));
    }

    @CheckForNull
    public static <T> PlatformId<T> fromUuid(@Nullable final UUID uuid)
    {
        return uuid == null ? null : new PlatformId<T>(uuid);
    }

    private final UUID uuid;

    private PlatformId(@Nonnull final UUID uuid)
    {
        Preconditions.checkArgument(uuid != null, "uuid can not be null");
        this.uuid = uuid;
    }

    @Nonnull
    public UUID getId()
    {
        return uuid;
    }

    @JsonValue
    public String getValue()
    {
        return uuid.toString();
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

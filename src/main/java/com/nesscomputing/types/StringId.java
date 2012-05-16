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

import java.util.Locale;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonValue;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

/**
 * A typed string. The value is case insensitive and by default lowercased.
 */
public final class StringId<T>
{
    private static final Function<String, StringId<?>> FROM_STRING = new Function<String, StringId<?>>() {
        @Override
        @CheckForNull
        public StringId<?> apply(@Nullable final String input)
        {
            return StringId.valueOf(input);
        }
    };

    private static final Function<StringId<?>, String> TO_STRING = new Function<StringId<?>, String>() {
        @Override
        @CheckForNull
        public String apply(@Nullable final StringId<?> input)
        {
            return input == null ? null : input.getValue();
        }
    };

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<String, StringId<T>> fromStringFunction()
    {
        return (Function) FROM_STRING;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> Function<StringId<T>, String> toStringFunction()
    {
        return (Function) TO_STRING;
    }

    @JsonCreator
    public static <T> StringId<T> valueOf(@Nullable final String value)
    {
        return value == null ? null : new StringId<T>(value);
    }

    private final String value;

    /**
     * Creates a new typed String id.
     */
    private StringId(@Nonnull final String value)
    {
        Preconditions.checkArgument(value != null, "value must not be null!");
        this.value = value.toLowerCase(Locale.ENGLISH);
    }

    @JsonValue
    public String getValue()
    {
        return value;
    }

    @Override
    public boolean equals(final Object other)
    {
        if (!(other instanceof StringId))
            return false;
        StringId<?> castOther = (StringId<?>) other;
        return new EqualsBuilder().append(value, castOther.value).isEquals();
    }

    private transient int hashCode;

    @Override
    public int hashCode()
    {
        if (hashCode == 0) {
            hashCode = new HashCodeBuilder().append(value).toHashCode();
        }
        return hashCode;
    }

    private transient String toString;

    @Override
    public String toString()
    {
        if (toString == null) {
            toString = new ToStringBuilder(this).appendSuper(super.toString()).append("value", value).toString();
        }
        return toString;
    }
}

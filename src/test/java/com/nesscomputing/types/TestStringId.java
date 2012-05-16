package com.nesscomputing.types;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Function;
import com.google.inject.TypeLiteral;

public class TestStringId
{
    @Test
    public void testBasic()
    {
        final String val1 = "val1";

        final StringId<User> userId = StringId.valueOf(val1);
        Assert.assertNotNull(userId);
        Assert.assertEquals(val1, userId.getValue());

        final StringId<User> userId2 = StringId.valueOf(val1);

        Assert.assertEquals(userId, userId2);
    }

    public void testNull()
    {
        Assert.assertNull(StringId.valueOf(null));
    }

    @Test
    public void testDifferent()
    {
        final String val1 = "val1";
        final String val2 = "val2";

        final StringId<User> userId = StringId.valueOf(val1);
        final StringId<User> userId2 = StringId.valueOf(val2);
        Assert.assertFalse(val1.equals(val2));
        Assert.assertFalse(userId.equals(userId2));
        Assert.assertFalse(userId.getValue().equals(userId2.getValue()));
    }

    @Test
    public void testMultitypes()
    {
        final TypeLiteral<StringId<User>> t1 = new TypeLiteral<StringId<User>>() {};
        final TypeLiteral<StringId<Place>> t2 = new TypeLiteral<StringId<Place>>() {};

        Assert.assertFalse(t1.equals(t2));
    }

    @Test
    public void testString()
    {
        final String val1 = "val1";
        final StringId<User> p1 = StringId.valueOf(val1);
        final Function<StringId<User>, String> f1 = StringId.toStringFunction();
        final Function<String, StringId<User>> f2 = StringId.fromStringFunction();
        final StringId<User> p2 = f2.apply(f1.apply(p1));
        Assert.assertEquals(p1, p2);
    }

    @Test
    public void testNullOk()
    {
        final StringId<User> p1 = null;
        final Function<StringId<User>, String> f1 = StringId.toStringFunction();
        final Function<String, StringId<User>> f2 = StringId.fromStringFunction();
        final String value = f1.apply(p1);
        Assert.assertNull(value);
        final StringId<User> p2 = f2.apply(value);
        Assert.assertNull(p2);
    }


    public static interface User
    {
    }

    public static interface Place
    {
    }
}



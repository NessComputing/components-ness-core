package com.nesscomputing.uuid;

import java.util.UUID;

import com.google.caliper.Runner;
import com.google.caliper.SimpleBenchmark;

public class PerformanceComparison extends SimpleBenchmark
{

    private static final int N_UUIDS = 1000;
    private static final UUID[] testUuids;
    private static final String[] testStrings;

    static {
        testUuids = new UUID[N_UUIDS];
        testStrings = new String[N_UUIDS];

        for (int i = 0; i < N_UUIDS; i++)
        {
            testUuids[i] = UUID.randomUUID();
            testStrings[i] = testUuids[i].toString();
        }
    }

    public static void main(String[] args)
    {
        Runner.main(PerformanceComparison.class, args);
    }

    public long timeJdkUuidFromString(int reps)
    {
        long accum = 0;
        for (int i = 0; i < reps; i++)
        {
            accum += UUID.fromString(testStrings[i % N_UUIDS]).getMostSignificantBits();
        }
        return accum;
    }

    public long timeNessUuidFromString(int reps)
    {
        long accum = 0;
        for (int i = 0; i < reps; i++)
        {
            accum += NessUUID.fromString(testStrings[i % N_UUIDS]).getMostSignificantBits();
        }
        return accum;
    }

    public long timeJdkUuidToString(int reps)
    {
        long accum = 0;
        for (int i = 0; i < reps; i++)
        {
            accum += testUuids[i % N_UUIDS].toString().charAt(0);
        }
        return accum;
    }

    public long timeNessUuidToString(int reps)
    {

        long accum = 0;
        for (int i = 0; i < reps; i++)
        {
            accum += NessUUID.toString(testUuids[i % N_UUIDS]).charAt(0);
        }
        return accum;
    }
}

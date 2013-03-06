/**
 * Copyright (C) 2013 Ness Computing, Inc.
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

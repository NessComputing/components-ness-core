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
package com.nesscomputing.callback;


/**
 * Thrown by {@link com.nesscomputing.callback.Callback#call(Object)} to signal that the caller should stop
 * processing data (and therefore calling the callback method).
 */
public class CallbackRefusedException extends Exception
{
    private static final long serialVersionUID = 1L;

    public CallbackRefusedException()
    {
    }

    public CallbackRefusedException(final Exception e)
    {
        super(e);
    }

    public CallbackRefusedException(final String message, final Object [] args)
    {
        super(String.format(message, args));
    }

    public CallbackRefusedException(final Exception e, final String message, final Object [] args)
    {
        super(String.format(message, args), e);
    }
}

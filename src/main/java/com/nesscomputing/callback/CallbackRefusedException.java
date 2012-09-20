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

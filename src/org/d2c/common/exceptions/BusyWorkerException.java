package org.d2c.common.exceptions;

public class BusyWorkerException extends Exception {

    public BusyWorkerException()
    {
    }

    public BusyWorkerException(String message)
    {
        super(message);
    }
}

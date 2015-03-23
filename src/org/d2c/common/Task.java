package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Task<T> extends Remote {

    /**
     * Execute an ambiguous operation
     */
    public abstract T run() throws RemoteException;

}

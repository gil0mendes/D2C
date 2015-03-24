package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface Task<T> extends Remote {

    /**
     * Execute an ambiguous operation
     */
    public abstract T run() throws RemoteException;

    /**
     * Get the task UUID
     *
     * @return
     * @throws RemoteException
     */
    public abstract UUID getUID() throws RemoteException;

}

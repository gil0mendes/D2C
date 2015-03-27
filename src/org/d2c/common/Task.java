package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface Task<T> extends Remote {

    /**
     * Execute an ambiguous operation
     */
    T run() throws RemoteException;

    /**
     * Get the task UUID
     *
     * @return
     *
     * @throws RemoteException
     */
    UUID getUID() throws RemoteException;

    /**
     * Get the Master owner of this task.
     *
     * @return
     *
     * @throws RemoteException
     */
    Master getMaster() throws RemoteException;

}

package org.d2c.common;

import org.d2c.common.exceptions.BusyWorkerException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface Worker extends Remote {

    /**
     * Respond to a ping request.
     *
     * @return
     */
    String ping() throws RemoteException;

    /**
     * Receive a task to be processed.
     *
     * @param task
     *
     * @throws RemoteException
     */
    void receive(Task task) throws RemoteException, BusyWorkerException;

    /**
     * Get UUID
     *
     * @return
     */
    UUID getUUID() throws RemoteException;

}

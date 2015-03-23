package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Worker extends Remote {

    /**
     * Respond to a ping request.
     *
     * @return
     */
    public abstract int ping() throws RemoteException;

    /**
     * Receive a task to be processed.
     *
     * @param task
     * @throws RemoteException
     */
    public abstract void receive(Task task) throws RemoteException;

}

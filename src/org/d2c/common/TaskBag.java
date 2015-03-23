package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskBag extends Remote {

    /**
     * Receive a task from a Master.
     *
     * @param task
     */
    public abstract void receive(Task task) throws RemoteException;

}

package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Master extends Remote {

    /**
     * Receive a task from the TaskBag
     *
     * @param task
     * @throws RemoteException
     */
    public abstract void receive(Task task, Object result) throws Exception;

}

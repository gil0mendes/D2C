package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Task extends Remote {

    /**
     * Execute an ambiguous operation
     */
    public abstract void run() throws RemoteException;

}
package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskBag extends Remote {

    /**
     * Receive a task from a Master.
     *
     * @param task
     */
    public abstract void receiveTask(Task task) throws RemoteException;

    /**
     * Register a new Worker.
     *
     * @param worker
     *
     * @throws RemoteException
     */
    public abstract void registerWorker(Worker worker) throws RemoteException;

    /**
     * This method receives the tasks results and redirect to the owner Master
     *
     * @param task
     * @param result
     *
     * @throws RemoteException
     */
    public abstract void responseTaskCallback(Task task, Object result) throws RemoteException;

}

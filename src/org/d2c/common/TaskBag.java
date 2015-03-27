package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TaskBag extends Remote {

    /**
     * Receive a task from a Master.
     *
     * @param task
     */
    void receiveTask(Task task) throws RemoteException;

    /**
     * Register a new Worker.
     *
     * @param worker
     *
     * @throws RemoteException
     */
    void registerWorker(Worker worker) throws RemoteException;

    /**
     * This method receives the tasks results and redirect to the owner Master
     *
     * @param task
     * @param result
     *
     * @throws RemoteException
     */
    void responseTaskCallback(Task task, Object result) throws RemoteException;

}

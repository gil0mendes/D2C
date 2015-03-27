package org.d2c.common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.UUID;

public interface TaskBag extends Remote {

    /**
     * Receive a task from a Master.
     *
     * @param task
     */
    void registerTask(Task task) throws RemoteException;

    /**
     * Register a new Worker.
     *
     * @param worker
     *
     * @throws RemoteException
     */
    void registerWorker(Worker worker) throws RemoteException;

    /**
     * Remove the worker
     *
     * @param workerUUID
     *
     * @throws RemoteException
     */
    void removeWorker(UUID workerUUID) throws RemoteException;

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

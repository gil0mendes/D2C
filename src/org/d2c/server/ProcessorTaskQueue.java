package org.d2c.server;

import org.d2c.common.Logger;
import org.d2c.common.Task;
import org.d2c.common.Worker;
import org.d2c.common.exceptions.BusyWorkerException;

import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.util.UUID;

public class ProcessorTaskQueue extends Thread {

    /**
     * Time to wait for new tasks
     */
    private final static int TIME_TO_WAIT = 1000;

    /**
     * TaskBag instance
     */
    private TaskBagServer taskBagServer;

    public ProcessorTaskQueue(TaskBagServer taskBagServer)
    {
        // save the TaskBag instance
        this.taskBagServer = taskBagServer;

        // log processor starter
        Logger.info("ProcessorTaskQueue are started");
    }

    /**
     * Wait for new tasks arrive and schedule then
     * to a free Worker,
     */
    @Override
    public void run()
    {
        // var to handle the next Task
        Task nextTask;

        // var to handle the next free Worker
        UUID nextWorkerUUID;

        // worker instance
        Worker nextWorker;

        while (true) {
            if ((nextTask = this.taskBagServer.getNextTask()) != null) {
                // Schedule task to a free worker
                while (true) {
                    if ((nextWorkerUUID = this.taskBagServer.getFreeWorker()) != null) {
                        // get the worker instance
                        nextWorker = this.taskBagServer.getWorkerByUUID(nextWorkerUUID);

                        try {
                            // send the task to the free worker
                            try {
                                // associate task to worker
                                this.taskBagServer.busyWorkers.put(nextTask, nextWorkerUUID);

                                nextWorker.receive(nextTask);
                            } catch (Exception ex) {
                                // remove the Worker
                                this.taskBagServer.removeWorker(nextWorkerUUID);
                            }

                            Logger.info("The task " + nextTask.getUUID() + " has been scheduled to a free worker");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }

                        // break the cycle
                        break;
                    }
                    else {
                        try {
                            Logger.info("Waiting for a free worker");
                            Thread.sleep(TIME_TO_WAIT);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            else {
                /**
                 * Wait some seconds to check again if exists
                 * a new Task to be process
                 */
                try {
                    Thread.sleep(TIME_TO_WAIT);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}

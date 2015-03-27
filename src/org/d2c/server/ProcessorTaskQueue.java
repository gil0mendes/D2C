package org.d2c.server;

import org.d2c.common.Logger;
import org.d2c.common.Task;
import org.d2c.common.Worker;
import org.d2c.common.exceptions.BusyWorkerException;

import java.rmi.RemoteException;

public class ProcessorTaskQueue extends Thread {

    /**
     * Time to wait for new tasks
     */
    private final static int TIME_TO_WAIT = 1000;

    /**
     * TaskBag instance
     */
    private TaskBagServer taskBagServer;

    /**
     * This var informs the thread if is to running
     */
    private boolean running = false;

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
        // put thread running
        this.running = true;

        // var to handle the next Task
        Task nextTask;

        // var to handle the next free Worker
        Worker nextWorker;

        while (this.running) {
            if ((nextTask = this.taskBagServer.getNextTask()) != null) {
                // Schedule task to a free worker
                while (this.running) {
                    if ((nextWorker = this.taskBagServer.getFreeWorker()) != null) {
                        try {
                            // send the task to the free worker
                            nextWorker.receive(nextTask);

                            // associate task to worker
                            this.taskBagServer.busyWorkers.put(nextTask.getUID(), nextWorker);

                            Logger.info("The task " + nextTask.getUID() + " has been scheduled to a free worker");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (BusyWorkerException e) {
                            Logger.error("The worker are busy");
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

    /**
     * Stop processor thread
     */
    public void stopProcessor()
    {
        this.running = false;
        this.interrupt();
    }
}

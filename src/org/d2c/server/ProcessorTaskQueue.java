package org.d2c.server;

import org.d2c.common.Task;

import java.rmi.RemoteException;

public class ProcessorTaskQueue implements Runnable {

    /**
     * Time to wait for new tasks
     */
    private final static int TIME_TO_WAIT = 3000;

    TaskBagServer taskBagServer;

    public ProcessorTaskQueue(TaskBagServer taskBagServer)
    {
        this.taskBagServer = taskBagServer;
    }

    /**
     * Wait for new tasks arrive and schedule then
     * to a free Worker,
     */
    @Override
    public void run()
    {
        Task nextTask;

        while (true) {
            if ((nextTask = this.taskBagServer.getNextTask()) != null) {
                // @TODO: Schedule task to a free worker
                try {
                    System.out.print("=> task started executing... ");
                    nextTask.run();
                    System.out.println("Done");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            else {
                /**
                 * Wait some seconds to check again if exists
                 * a new Tasks to process
                 */
                try {
                    Thread.sleep(TIME_TO_WAIT);
                } catch (InterruptedException e) {
                }
            }
        }
    }
}

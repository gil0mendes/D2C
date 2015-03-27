package org.d2c.server;

import org.d2c.common.Worker;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This thread will monitor the Workers time to
 * time in order to discard the Slow Workers or
 * an Worker who has been disconnected in the
 * wrong way.
 */
public class WorkersMonitorThread extends Thread {

    /**
     * TaskBagInstance
     */
    private TaskBagServer taskBagServer;

    /**
     * Set 1 second of interval
     */
    private final long TIME_INTERVAL = 1000;

    /**
     * Ping time limit for a Worker
     */
    private final long PING_TIME_LIMIT = 1500;

    public WorkersMonitorThread(TaskBagServer taskBagServer)
    {
        // save the TaskBagServer instance
        this.taskBagServer = taskBagServer;
    }

    @Override
    public void run()
    {
        while (true) {
            // iterate through list of workers
            this.taskBagServer.registeredWorks.forEach((uuid, worker) -> {
                // test the worker
                try {
                    Timer timer = new Timer(true);
                    TimerTask interruptTimerTask = new InterruptTimerTask(Thread.currentThread());
                    timer.schedule(interruptTimerTask, PING_TIME_LIMIT);
                    worker.ping();
                    timer.cancel();
                } catch (RemoteException e) {
                    try {
                        this.taskBagServer.removeWorker(uuid);
                    } catch (Exception ex) {
                    }
                }

                try {
                    // wait some time to test another worker
                    Thread.sleep(TIME_INTERVAL);
                } catch (InterruptedException e) {
                }
            });
        }
    }

    private static class InterruptTimerTask extends TimerTask {
        private Thread thread;

        public InterruptTimerTask(Thread thread)
        {
            this.thread = thread;
        }

        @Override
        public void run()
        {
            thread.interrupt();
        }

    }
}

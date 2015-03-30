package org.d2c.worker;

import org.d2c.common.Logger;
import org.d2c.common.Task;
import org.d2c.common.TaskBag;
import org.d2c.common.Worker;
import org.d2c.common.exceptions.BusyWorkerException;

import java.net.URL;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.UUID;

public class WorkerMain extends UnicastRemoteObject implements Worker {

    /**
     * This var informs if the Worker are busy
     */
    private boolean isBusy;

    /**
     * Instance for the TaskBag
     */
    private TaskBag taskBag;

    /**
     * UUID for the Worker
     */
    private UUID workerUUID;

    public WorkerMain() throws RemoteException
    {
        // generate UUID
        this.workerUUID = UUID.randomUUID();
    }

    @Override
    public String ping() throws RemoteException
    {
        return "OK";
    }

    @Override
    public void receive(Task task) throws RemoteException, BusyWorkerException
    {
        // check if the Worker are busy
        if (this.isBusy) {
            throw new BusyWorkerException("I'm busy right now");
        }

        // run a new task on a separated thread
        (new ProcessTask(task)).start();
    }

    @Override
    public UUID getUUID() throws RemoteException
    {
        return this.workerUUID;
    }

    private class ProcessTask extends Thread {

        /**
         * Task to be processed
         */
        private Task task;

        public ProcessTask(Task task)
        {
            this.task = task;
        }

        /**
         * Process the task
         */
        @Override
        public void run()
        {
            Object taskResult = null;

            // put worker in a busy state
            WorkerMain.this.isBusy = true;

            // process the task
            try {
                Logger.info("Task (" + this.task.getUUID() + ") is now running");
                taskResult = this.task.run();
            } catch (RemoteException e) {
                e.printStackTrace();
            }

            // put worker in a free state
            WorkerMain.this.isBusy = false;

            // inform the TaskBag
            try {
                WorkerMain.this.taskBag.responseTaskCallback(task, taskResult);
                Logger.info("Task (" + this.task.getUUID() + ") was end their job");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        // start Logger
        Logger.config(1);

        // check if have enough arguments
        if (args.length < 1) {
            System.out.println("usage: java org.d2c.worker.WorkerMain <REMOTE_SERVER_IP>");
            return;
        }

        // set configurations for the JAVA Security Policy
        ClassLoader cl = WorkerMain.class.getClassLoader();
        URL policyURL = cl.getResource("org/d2c/common/policy.all");
        System.setProperty("java.security.policy", policyURL.toString());

        // set the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // get the registry
            Registry registry = LocateRegistry.getRegistry(args[0], Registry.REGISTRY_PORT);

            // register the new worker
            WorkerMain worker = new WorkerMain();
            registry.bind(worker.getUUID().toString(), worker);

            // get the TaskBag
            worker.taskBag = (TaskBag) registry.lookup("TaskBag");

            // register the new worker on the TaskBag
            worker.taskBag.registerWorker(worker);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (AlreadyBoundException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}

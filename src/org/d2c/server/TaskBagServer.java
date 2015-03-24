package org.d2c.server;

import org.d2c.common.Logger;
import org.d2c.common.Task;
import org.d2c.common.TaskBag;
import org.d2c.common.Worker;

import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class TaskBagServer implements TaskBag {

    /**
     * Queue with all tasks who needs be processed
     */
    private Queue<Task> tasks = new LinkedList<Task>();

    /**
     * List of registered workers
     */
    protected List<Worker> registeredWorks = new LinkedList<Worker>();

    /**
     * List of free workers
     */
    protected List<Worker> freeWorkers = new LinkedList<Worker>();

    /**
     * List of busy workers
     */
    protected HashMap<UUID, Worker> busyWorkers = new LinkedHashMap<UUID, Worker>();

    public TaskBagServer()
    {
    }

    /**
     * Receive a new tasks and add it to the queue.
     *
     * @param task
     *
     * @throws RemoteException
     */
    @Override
    public void receiveTask(Task task) throws RemoteException
    {
        // add the new received task to the queue
        // to be processed later
        synchronized (tasks) {
            System.out.println("=> new task received");
            tasks.add(task);
        }
    }

    @Override
    public void registerWorker(Worker worker) throws RemoteException
    {
        // register the new worker
        this.registeredWorks.add(worker);

        // add to the list of free workers
        this.freeWorkers.add(worker);

        // Inform the registry
        Logger.info("A new Worker as been registered");
    }

    @Override
    public void responseTaskCallback(Task task, Object result) throws RemoteException
    {
        // inform the execution end of task
        Logger.info("Task (" + task.getUID() + ") was end their job");

        // free the worker
        this.freeWorkers.add(this.busyWorkers.remove(task.getUID()));

        // get task master owner
        try {
            task.getMaster().receive(task, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * If exists, this method returns the next task
     * who needs to be scheduled. Otherwise, returns
     * null;
     *
     * @return
     */
    protected synchronized Task getNextTask()
    {
        // get the next task to be processed
        return this.tasks.poll();
    }

    /**
     * If exists, this method returns the next free
     * worker.
     *
     * @return
     *
     * @TODO Test if the Worker is up and if have good pings
     */
    protected synchronized Worker getFreeWorker()
    {
        // get the next free worker
        if (this.freeWorkers.size() == 0) {
            return null;
        }
        else {
            return this.freeWorkers.remove(0);
        }
    }

    public static void main(String[] args)
    {
        // start Logger
        Logger.config(1);

        // set configurations for the JAVA Security Policy
        ClassLoader cl = TaskBagServer.class.getClassLoader();
        URL policyURL = cl.getResource("org/d2c/common/policy.all");
        System.setProperty("java.security.policy", policyURL.toString());

        // set the security manager
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // create a new registry
            Registry registry;

            try {
                registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            } catch (Exception ex) {
                registry = LocateRegistry.getRegistry();
            }

            // registry TaskBag class
            TaskBag taskBagInstance = new TaskBagServer();
            TaskBag taskBagStub = (TaskBag) UnicastRemoteObject.exportObject(taskBagInstance, 0);
            registry.rebind("TaskBag", taskBagStub);
            Logger.info("TaskBag registered!\nServer is now running...");

            // start processing the tasks queue
            (new ProcessorTaskQueue((TaskBagServer) taskBagInstance)).run();
        } catch (Exception ex) {
            System.out.println("Server exception:");
            ex.printStackTrace();
        }
    }
}

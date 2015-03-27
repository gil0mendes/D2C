package org.d2c.server;

import org.d2c.common.Logger;
import org.d2c.common.Task;
import org.d2c.common.TaskBag;
import org.d2c.common.Worker;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class TaskBagServer extends RemoteObject implements TaskBag {

    /**
     * Save this class instance
     */
    private static TaskBagServer instance = new TaskBagServer();

    /**
     * Return the TaskBagServer class instance
     *
     * @return
     */
    public static TaskBagServer getInstance()
    {
        return instance;
    }

    /**
     * Save the server state
     */
    private boolean connected = false;

    /**
     * Queue with all tasks who needs be processed
     */
    private Queue<Task> tasks = new LinkedList<>();

    /**
     * List of registered workers
     */
    protected List<Worker> registeredWorks = new LinkedList<>();

    /**
     * List of free workers
     */
    protected List<Worker> freeWorkers = new LinkedList<>();

    /**
     * List of busy workers
     */
    protected HashMap<UUID, Worker> busyWorkers = new LinkedHashMap<>();

    /**
     * Instance for registry
     */
    private Registry registry;

    /**
     * ProcessorTaskQueue instance
     */
    private ProcessorTaskQueue processorTaskQueue = new ProcessorTaskQueue(this);

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
            Logger.info("New task received");
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
        Logger.info("A new Worker (" + worker.getUUID() + ") as been registered");
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

    /**
     * Return the current server state
     *
     * @return
     */
    public boolean isConnected()
    {
        return connected;
    }

    /**
     * Startup the server
     */
    public void connect()
    {
        // reset the TaskBag state
        this.reset();

        // start the server
        try {
            // try get the registry, if not exists create one
            try {
                this.registry = LocateRegistry.getRegistry();
                this.registry.list();
            } catch (Exception ex) {
                this.registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            }

            // registry TaskBag class
            TaskBag taskBagStub = this;
            try {
                taskBagStub = (TaskBag) UnicastRemoteObject.exportObject(this, 0);
            } catch (Exception ex) {
            }

            this.registry.rebind("TaskBag", taskBagStub);
            Logger.info("Server is now running...");

            // start processing the tasks queue
            this.processorTaskQueue = new ProcessorTaskQueue(this);
            this.processorTaskQueue.start();

            // make server has started
            this.connected = true;
        } catch (Exception ex) {
            System.out.println("Server exception:");
            ex.printStackTrace();
        }
    }

    /**
     * Disconnect the server
     */
    public void disconnect()
    {
        // stop processor queue tasks
        this.processorTaskQueue.stopProcessor();

        // unregister the TaskBag
        try {
            this.registry.unbind("TaskBag");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }

        // make the server disconnected
        this.connected = false;
    }

    /**
     * Reset the TaskBag state
     */
    private void reset()
    {
        this.tasks.clear();
        this.registeredWorks.clear();
        this.freeWorkers.clear();
        this.busyWorkers.clear();
    }

    /**
     * Get the number of waiting tasks
     *
     * @return
     */
    public int getNumberOfWaitingTasks()
    {
        return this.tasks.size();
    }

    /**
     * Get the number of registered workers.
     *
     * @return
     */
    public int getNumberOfWorkers()
    {
        return this.registeredWorks.size();
    }
}

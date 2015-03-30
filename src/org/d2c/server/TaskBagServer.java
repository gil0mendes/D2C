package org.d2c.server;

import org.d2c.common.*;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteObject;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
    protected ConcurrentMap<UUID, Worker> registeredWorks = new ConcurrentHashMap<>();

    /**
     * List of free workers
     */
    protected List<UUID> freeWorkers = new LinkedList<>();

    /**
     * List of busy workers
     */
    protected HashMap<Task, UUID> busyWorkers = new LinkedHashMap<>();

    /**
     * Instance for registry
     */
    protected Registry registry;

    /**
     * ProcessorTaskQueue instance
     */
    private ProcessorTaskQueue processorTaskQueue;

    /**
     * Thread to monitor the Workers if their are alive
     * and with energy to preform work
     */
    private WorkersMonitorThread workersMonitorThread;

    /**
     * Registered callbacks for handle server state changes
     */
    private List<CallbackHandler> callbackForServerChanges = new LinkedList<>();

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
    public synchronized void registerTask(Task task) throws RemoteException
    {
        // add the new received task to the queue
        // to be processed later
        Logger.info("New task (" + task.getUUID() + ") received");
        tasks.add(task);

        // notify changes
        this.notifyServerStateChanges();
    }

    @Override
    public synchronized void registerWorker(Worker worker) throws RemoteException
    {
        // register the new worker
        this.registeredWorks.put(worker.getUUID(), worker);

        // add to the list of free workers
        this.freeWorkers.add(worker.getUUID());

        // Inform the registry
        Logger.info("A new Worker (" + worker.getUUID() + ") as been registered");

        // notify changes
        this.notifyServerStateChanges();
    }

    @Override
    public synchronized void removeWorker(UUID workerUUID) throws RemoteException
    {
        // remove worker from the TaskBag
        this.freeWorkers.remove(workerUUID);
        this.registeredWorks.remove(workerUUID);

        // remove the worker from Registry
        try {
            this.registry.unbind(workerUUID.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // If the Worker are busy transfer the task
        // to another free worker
        for (Map.Entry entry : this.busyWorkers.entrySet()) {
            if (workerUUID.equals(entry.getValue())) {
                Task task = (Task) entry.getKey();
                this.registerTask(task);
                this.busyWorkers.remove(task);
                break;
            }
        }

        // inform the worker kill
        Logger.info("The Worker(" + workerUUID + ") has been killed");

        // notify changes
        this.notifyServerStateChanges();
    }

    @Override
    public void responseTaskCallback(Task task, Object result) throws RemoteException
    {
        // inform the execution end of task
        Logger.info("Task (" + task.getUUID() + ") was end their job");

        // free the worker
        this.freeWorkers.add(this.busyWorkers.remove(task));

        // get task master owner
        try {
            task.getMaster().receive(task, result);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // notify changes
        this.notifyServerStateChanges();
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
     */
    protected synchronized UUID getFreeWorker()
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
     * Get a worker by their UUID
     *
     * @param uuid
     *
     * @return
     */
    protected Worker getWorkerByUUID(UUID uuid)
    {
        return this.registeredWorks.get(uuid);
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

            // start the workers monitor thread
            this.workersMonitorThread = new WorkersMonitorThread(this);
            this.workersMonitorThread.start();

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
        // stop processor for the tasks
        this.processorTaskQueue.interrupt();

        // stop workers monitor thread
        this.workersMonitorThread.interrupt();

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

    /**
     * Register a new callback to handle the server state changes
     *
     * @param handler
     */
    public void registerCallbackForServerChanges(CallbackHandler handler)
    {
        this.callbackForServerChanges.add(handler);
    }

    /**
     * Unregister a callback to handle the server state changes
     *
     * @param handler
     */
    public void removeCallbackForServerChanges(CallbackHandler handler)
    {
        this.callbackForServerChanges.remove(handler);
    }

    /**
     * Notify changes to all handlers
     */
    private void notifyServerStateChanges()
    {
        this.callbackForServerChanges.forEach(handler -> handler.callback());
    }
}

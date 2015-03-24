package org.d2c.server;

import org.d2c.common.Task;
import org.d2c.common.TaskBag;

import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.Queue;

public class TaskBagServer implements TaskBag {

    /**
     * Queue with all tasks who needs be processed
     */
    Queue<Task> tasks = new LinkedList<Task>();

    public TaskBagServer() {}

    /**
     * Receive a new tasks and add it to the queue.
     *
     * @param task
     *
     * @throws RemoteException
     */
    @Override
    public void receive(Task task) throws RemoteException
    {
        // add the new received task to the queue
        // to be processed later
        synchronized (tasks) {
            System.out.println("=> new task received");
            tasks.add(task);
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
        return this.tasks.poll();
    }

    public static void main(String[] args)
    {
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
            Registry registry = null;

            try {
                registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
            } catch (Exception ex) {
                registry = LocateRegistry.getRegistry();
            }

            // registry TaskBag class
            TaskBag taskBagInstance = new TaskBagServer();
            TaskBag taskBagStub = (TaskBag) UnicastRemoteObject.exportObject(taskBagInstance, 0);
            registry.rebind("TaskBag", taskBagStub);

            System.out.println("TaskBag registered!\nServer is now running...");

            // start processing the tasks queue
            (new ProcessorTaskQueue((TaskBagServer) taskBagInstance)).run();
        } catch (Exception ex) {
            System.out.println("Server exception:");
            ex.printStackTrace();
        }
    }
}

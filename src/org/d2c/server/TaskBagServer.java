package org.d2c.server;

import org.d2c.common.Task;
import org.d2c.common.TaskBag;

import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class TaskBagServer implements TaskBag {

    @Override
    public void receive(Task task) throws RemoteException
    {

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
        } catch (Exception ex) {
            System.out.println("Server exception:");
            ex.printStackTrace();
        }
    }
}

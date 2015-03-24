package org.d2c.client;

import org.d2c.common.Master;
import org.d2c.common.Task;
import org.d2c.common.TaskBag;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

public class MasterEngine extends UnicastRemoteObject implements Master {

    /**
     * JAVA RMI Registry
     */
    private Registry registry = null;

    /**
     * TaskBag object
     */
    private TaskBag taskBag;

    /**
     * Empty constructor
     */
    public MasterEngine(int registryPort) throws RemoteException, NotBoundException
    {
        // get Registry
        this.registry = LocateRegistry.getRegistry(registryPort);

        // get TaskBag object from RMI Registry
        this.taskBag = (TaskBag) this.registry.lookup("TaskBag");
    }

    protected List<Integer> calculatePrimeNumbers(Integer lower, Integer upper) throws Exception
    {
        // test numbers
        if (lower > upper) {
            throw new Exception("Invalid parameters!");
        }

        // run until lower becomes bigger than upper
        // or when the cycle is break
        while (lower <= upper) {
            // if the deference between upper and lower values
            // are greater than 1000 a new sub set needs to be
            // created
            if ((upper - lower) > 1000) {
                // send a new task for the TaskBag
                this.taskBag.receiveTask(new PrimeTask(lower, lower + 1000));

                // increment lower value in 1000
                lower += 1000;
            }
            else {
                // send a new task for the TaskBag
                this.taskBag.receiveTask(new PrimeTask(lower, upper));
                break;
            }
        }

        return null;
    }

    @Override
    public void receive(Task task) throws RemoteException
    {

    }
}

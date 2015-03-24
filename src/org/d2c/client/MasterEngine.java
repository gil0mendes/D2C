package org.d2c.client;

import org.d2c.common.CallbackHandler;
import org.d2c.common.Master;
import org.d2c.common.Task;
import org.d2c.common.TaskBag;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.LinkedList;
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
     * List with final results
     */
    private List<Integer> finalResult;

    /**
     * Callback method
     */
    private CallbackHandler callback = null;

    private int receivedResponses = 0;
    private int sendedResponses = 0;

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

    /**
     * Divide task in sub-tasks and send than to the TaskBag
     *
     * @param lower
     * @param upper
     *
     * @throws Exception
     */
    protected void calculatePrimeNumbers(Integer lower, Integer upper) throws Exception
    {
        // test numbers
        if (lower > upper) {
            throw new Exception("Invalid parameters!");
        }

        // reset counters
        this.receivedResponses = this.sendedResponses = 0;

        // set result list
        this.finalResult = new LinkedList<Integer>();

        // run until lower becomes bigger than upper
        // or when the cycle is break
        while (lower <= upper) {
            // increment sender counter
            this.sendedResponses++;

            // if the deference between upper and lower values
            // are greater than 1000 a new sub set needs to be
            // created
            if ((upper - lower) > 1000) {
                // send a new task for the TaskBag
                this.taskBag.receiveTask(new PrimeTask(lower, lower + 1000, this));

                // increment lower value in 1000
                lower += 1000;
            }
            else {
                // send a new task for the TaskBag
                this.taskBag.receiveTask(new PrimeTask(lower, upper, this));
                break;
            }
        }
    }

    @Override
    public void receive(Task task, Object result) throws Exception
    {
        // increment received counter
        this.receivedResponses++;

        // register response
        if (result instanceof List) {
            // cast result to List<Integer>
            List<Integer> list = (List<Integer>) result;

            // merge the two lists
            this.finalResult.addAll(list);
        }
        else {
            throw new Exception("Invalid result object");
        }

        // check if is the final receive
        if (this.sendedResponses == this.receivedResponses) {
            // sort the list
            Collections.sort(this.finalResult);

            // execute the callback
            if (this.callback != null) {
                this.callback.callback(this.finalResult);
            }
        }
    }

    protected void setCallback(CallbackHandler callback)
    {
        this.callback = callback;
    }
}

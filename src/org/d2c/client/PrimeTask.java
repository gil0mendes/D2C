package org.d2c.client;

import org.d2c.common.Master;
import org.d2c.common.Task;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class PrimeTask extends UnicastRemoteObject implements Task<List<Integer>> {

    /**
     * Lower limit
     */
    private final Integer LOWER_LIMIT;

    /**
     * Upper limit
     */
    private final Integer UPPER_LIMIT;

    /**
     * List with all calculated prime number in the given range
     */
    private final List<Integer> resultList = new LinkedList<Integer>();

    /**
     * Task Master owner
     */
    private Master masterOwner;

    /**
     * Task UUID
     */
    private UUID taskUUID;

    /**
     * Set the bound of the calculus.
     * <p/>
     * The difference between the lower and upper
     * values never should greater than 1000. And
     * lees or equals to zero.
     *
     * @param lower
     * @param upper
     */
    public PrimeTask(Integer lower, Integer upper, Master owner) throws Exception
    {
        // test the values
        if ((upper - lower) > 1000 || (upper - lower) <= 0) {
            throw new Exception("The bounds are invalid!");
        }

        // set values
        this.LOWER_LIMIT = lower;
        this.UPPER_LIMIT = upper;
        this.masterOwner = owner;

        // generate Task UUID
        this.taskUUID = UUID.randomUUID();
    }

    @Override
    public List<Integer> run() throws RemoteException
    {
        // calculate all prime numbers in the range
        this.calculatePrimeNumbers();

        // return the result list
        return this.resultList;
    }

    /**
     * checks whether an int is prime or not.
     *
     * @param n
     *
     * @return
     */
    private static boolean isPrime(Integer n)
    {
        if (n < 2) {
            return false;
        }

        if (n == 2 || n == 3) {
            return true;
        }

        if (n % 2 == 0 || n % 3 == 0) {
            return false;
        }

        long sqrtN = (long) Math.sqrt(n) + 1;
        for (long i = 6L; i <= sqrtN; i += 6) {
            if (n % (i - 1) == 0 || n % (i + 1) == 0) {
                return false;
            }
        }
        return true;
    }

    public void calculatePrimeNumbers()
    {
        // get lower limit
        Integer currentNumber = LOWER_LIMIT;

        // test all numbers between the interval
        for (; currentNumber <= UPPER_LIMIT; currentNumber++) {
            // check if the current number is a prime number
            if (isPrime(currentNumber)) {
                // add to the list of prime numbers
                resultList.add(currentNumber);
            }
        }
    }

    @Override
    public UUID getUUID() throws RemoteException
    {
        return this.taskUUID;
    }

    @Override
    public Master getMaster() throws RemoteException
    {
        return this.masterOwner;
    }
}

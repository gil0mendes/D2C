package org.d2c.client;

import org.d2c.common.Task;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;
import java.util.List;

public class PrimeTask extends UnicastRemoteObject implements Task<List<Integer>> {

    private final Integer LOWER_LIMIT;
    private final Integer UPPER_LIMIT;
    private final List<Integer> resultList = new LinkedList<Integer>();

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
    public PrimeTask(Integer lower, Integer upper) throws Exception
    {
        // test the values
        if ((upper - lower) > 1000 || (upper - lower) <= 0) {
            throw new Exception("The bounds are invalid!");
        }

        // set values
        this.LOWER_LIMIT = lower;
        this.UPPER_LIMIT = upper;
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
        // check if n is a multiple of 2
        if (n % 2 == 0) {
            return false;
        }
        // if not, then just check the odds
        for (int i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
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

}

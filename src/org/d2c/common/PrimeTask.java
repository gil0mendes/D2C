package org.d2c.common;

import java.rmi.RemoteException;
import java.util.List;

public interface PrimeTask extends Task {

    /**
     * Get the list of computed results.
     *
     * @return
     */
    public abstract List<Integer> getResult() throws RemoteException;

}

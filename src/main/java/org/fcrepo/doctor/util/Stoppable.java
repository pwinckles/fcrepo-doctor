/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.util;

/**
 * Extension of Runnable that allows the Runnable to be gracefully terminated
 *
 * @author winckles
 */
public interface Stoppable extends Runnable {

    /**
     * Gracefully stop
     */
    void terminate();

    /**
     * @return true if has stopped
     */
    boolean hasStopped();

}

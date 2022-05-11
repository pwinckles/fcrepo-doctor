/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.writer;

import org.fcrepo.doctor.problem.ObjectProblems;

/**
 * Writes problems. The writer must be closed when done to ensure all problems are written.
 *
 * @author winckles
 */
public interface ProblemWriter extends AutoCloseable {

    /**
     * Write the given problem
     *
     * @param problems problem to write
     */
    void write(final ObjectProblems problems);

}

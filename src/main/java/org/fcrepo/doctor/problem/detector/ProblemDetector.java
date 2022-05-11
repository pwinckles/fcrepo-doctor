/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.detector;

import org.fcrepo.doctor.analyzer.reader.ContentReader;
import org.fcrepo.doctor.problem.ProblemType;
import org.fcrepo.storage.ocfl.ResourceHeaders;

import java.util.Set;

/**
 * Detects problems in resources
 *
 * @author winckles
 */
public interface ProblemDetector {

    /**
     * Detects any problems in a resource and returns a set of ProblemTypes for any problems found.
     *
     * @param headers the resource's headers
     * @param contentReader a reader for the resource's content
     * @return a set of problem found
     */
    Set<ProblemType> detect(final ResourceHeaders headers, final ContentReader contentReader);

}

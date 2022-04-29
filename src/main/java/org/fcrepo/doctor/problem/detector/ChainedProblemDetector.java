/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.detector;

import org.fcrepo.doctor.analyzer.reader.ContentReader;
import org.fcrepo.doctor.problem.ProblemType;
import org.fcrepo.storage.ocfl.ResourceHeaders;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Chains together a list of ProblemDetectors and applies all of the detectors to each resource, aggregating the
 * results.
 *
 * @author winckles
 */
public class ChainedProblemDetector implements ProblemDetector {

    private final List<ProblemDetector> detectors;

    public ChainedProblemDetector(final List<ProblemDetector> detectors) {
        this.detectors = detectors;
    }

    @Override
    public Set<ProblemType> detect(final ResourceHeaders headers, final ContentReader contentReader) {
        final var problems = new HashSet<ProblemType>();

        for (final var detector : detectors) {
            problems.addAll(detector.detect(headers, contentReader));
        }

        return problems;
    }
}

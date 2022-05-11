/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer;

import org.fcrepo.doctor.analyzer.reader.ContentReaderFactory;
import org.fcrepo.doctor.problem.ObjectProblems;
import org.fcrepo.doctor.problem.detector.ProblemDetector;
import org.fcrepo.storage.ocfl.OcflObjectSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analyzes Fedora OCFL objects for problems and returns any problems found.
 *
 * @author winckles
 */
public class ObjectAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectAnalyzer.class);

    private final OcflObjectSessionFactory objectSessionFactory;
    private final ProblemDetector problemDetector;
    private final ContentReaderFactory contentReaderFactory;

    /**
     * @param objectSessionFactory OCFL object session factory
     * @param problemDetector problem detector that's run on every resource in an object
     * @param contentReaderFactory content reader factory
     */
    public ObjectAnalyzer(final OcflObjectSessionFactory objectSessionFactory,
                          final ProblemDetector problemDetector,
                          final ContentReaderFactory contentReaderFactory) {
        this.objectSessionFactory = objectSessionFactory;
        this.problemDetector = problemDetector;
        this.contentReaderFactory = contentReaderFactory;
    }

    /**
     * Analyzes all of the resources in an object and reports any problems found
     *
     * @param objectId the OCFL object id of the object to analyze
     * @return any problems found
     */
    public ObjectProblems analyze(final String objectId) {
        LOG.debug("Analyzing object {}", objectId);

        final var objectProblems = new ObjectProblems(objectId);

        try (final var session = objectSessionFactory.newSession(objectId)) {
            try (final var headers = session.streamResourceHeaders()) {
                headers.forEach(header -> {
                    try (final var contentReader = contentReaderFactory.newReader(header.getId(), session)) {
                        final var problems = problemDetector.detect(header, contentReader);

                        if (!problems.isEmpty()) {
                            LOG.info("Identified problems for resource {}: {}", header.getId(), problems);
                            objectProblems.addProblems(header.getId(), problems);
                        }
                    } catch (Exception e) {
                        LOG.error("Failed to analyze resource {} in object {}", header.getId(), objectId, e);
                    }
                });
            }
        }

        return objectProblems;
    }

}

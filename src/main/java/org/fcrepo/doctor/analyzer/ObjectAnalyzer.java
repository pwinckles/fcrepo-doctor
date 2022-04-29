/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer;

import org.fcrepo.doctor.analyzer.reader.ContentReaderFactory;
import org.fcrepo.doctor.problem.ObjectProblems;
import org.fcrepo.doctor.problem.detector.ProblemDetector;
import org.fcrepo.doctor.problem.writer.ProblemWriter;
import org.fcrepo.doctor.util.Stoppable;
import org.fcrepo.storage.ocfl.OcflObjectSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Analyzes Fedora OCFL objects for problems and records any problems found. Each ObjectAnalyzer is intended to be run
 * in its own thread, and pulls objects to analyze off a queue. It will run indefinitely until explicitly stopped.
 *
 * @author winckles
 */
public class ObjectAnalyzer implements Stoppable {

    private static final Logger LOG = LoggerFactory.getLogger(ObjectAnalyzer.class);

    private final BlockingQueue<String> objectIdQueue;
    private final OcflObjectSessionFactory objectSessionFactory;
    private final ProblemDetector problemDetector;
    private final ContentReaderFactory contentReaderFactory;
    private final ProblemWriter problemWriter;

    private boolean stop = false;
    private boolean stopped = false;

    /**
     * @param objectIdQueue queue of object ids to process
     * @param objectSessionFactory OCFL object session factory
     * @param problemDetector problem detector that's run on every resource in an object
     * @param contentReaderFactory content reader factory
     * @param problemWriter writer for recording any problems found
     */
    public ObjectAnalyzer(final BlockingQueue<String> objectIdQueue,
                          final OcflObjectSessionFactory objectSessionFactory,
                          final ProblemDetector problemDetector,
                          final ContentReaderFactory contentReaderFactory,
                          final ProblemWriter problemWriter) {
        this.objectIdQueue = objectIdQueue;
        this.objectSessionFactory = objectSessionFactory;
        this.problemDetector = problemDetector;
        this.contentReaderFactory = contentReaderFactory;
        this.problemWriter = problemWriter;
    }

    @Override
    public void run() {
        while (!stop) {
            final var objectId = getObjectId();
            if (objectId == null) {
                continue;
            }

            LOG.debug("Analyzing object {}", objectId);

            try {
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

                if (objectProblems.hasProblems()) {
                    problemWriter.write(objectProblems);
                }
            } catch (RuntimeException e) {
                LOG.error("Failed to analyze object {}", objectId, e);
            }
        }

        stopped = true;
    }

    @Override
    public void terminate() {
        stop = true;
    }

    @Override
    public boolean hasStopped() {
        return stopped;
    }

    private String getObjectId() {
        try {
            return objectIdQueue.take();
        } catch (InterruptedException e) {
            stop = true;
            return null;
        }
    }

}

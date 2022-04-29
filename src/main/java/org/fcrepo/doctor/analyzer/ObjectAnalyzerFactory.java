/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer;

import org.fcrepo.doctor.analyzer.reader.ContentReaderFactory;
import org.fcrepo.doctor.problem.detector.ProblemDetector;
import org.fcrepo.doctor.problem.writer.ProblemWriter;
import org.fcrepo.storage.ocfl.OcflObjectSessionFactory;

import java.util.concurrent.BlockingQueue;

/**
 * Factory for creating new ObjectAnalyzers
 *
 * @author winckles
 */
public class ObjectAnalyzerFactory {

    private final OcflObjectSessionFactory objectSessionFactory;
    private final ProblemDetector problemDetector;
    private final ContentReaderFactory contentReaderFactory;
    private final ProblemWriter problemWriter;

    /**
     * @param objectSessionFactory OCFL object session factory
     * @param problemDetector problem detector for detecting problems in resources
     * @param contentReaderFactory content reader factory
     * @param problemWriter writer for recording problems
     */
    public ObjectAnalyzerFactory(final OcflObjectSessionFactory objectSessionFactory,
                                 final ProblemDetector problemDetector,
                                 final ContentReaderFactory contentReaderFactory,
                                 final ProblemWriter problemWriter) {
        this.objectSessionFactory = objectSessionFactory;
        this.problemDetector = problemDetector;
        this.contentReaderFactory = contentReaderFactory;
        this.problemWriter = problemWriter;
    }

    /**
     * Creates a new ObjectAnalyzer that reads off the specified queue.
     *
     * @param objectIdQueue object id queue containing objects that need analysis
     * @return object analyzer
     */
    public ObjectAnalyzer newObjectAnalyzer(final BlockingQueue<String> objectIdQueue) {
        return new ObjectAnalyzer(objectIdQueue,
                objectSessionFactory,
                problemDetector,
                contentReaderFactory,
                problemWriter);
    }

}

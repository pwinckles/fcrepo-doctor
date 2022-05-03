/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.writer;

import org.fcrepo.doctor.problem.ObjectProblems;
import org.fcrepo.doctor.problem.ProblemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * This writer collects stats about the problems found and logs them on close
 *
 * @author winckles
 */
public class StatsProblemWriter implements ProblemWriter {

    private static final Logger LOG = LoggerFactory.getLogger(StatsProblemWriter.class);

    private final ProblemWriter inner;
    private final Map<ProblemType, AtomicLong> counts;

    /**
     * @param inner delegate to call after collecting stats
     */
    public StatsProblemWriter(final ProblemWriter inner) {
        this.inner = inner;
        // can only use a regular hashmap here if we prepopulate it with all possible keys
        this.counts = new HashMap<>();
        for (final var type : ProblemType.values()) {
            this.counts.put(type, new AtomicLong(0));
        }
    }

    @Override
    public void write(final ObjectProblems problems) {
        problems.getResourceProblems().forEach((resourceId, rProblems) -> {
            rProblems.forEach(type -> {
                counts.get(type).incrementAndGet();
            });
        });
        inner.write(problems);
    }

    @Override
    public void close() throws Exception {
        if (LOG.isInfoEnabled()) {
            LOG.info(buildReport());
        }

        inner.close();
    }

    private String buildReport() {
        final var problemsFound = new AtomicBoolean(false);
        final var builder = new StringBuilder("Report:");

        counts.forEach((type, count) -> {
            final var total = count.get();
            if (total > 0) {
                problemsFound.set(true);
                builder.append("\n  - ")
                        .append(type)
                        .append(": ")
                        .append(total)
                        .append(" resource");

                if (total > 1) {
                    builder.append("s");
                }
            }
        });

        if (!problemsFound.get()) {
            builder.append(" No problems found");
        }

        return builder.toString();
    }
}

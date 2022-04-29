/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.writer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SequenceWriter;
import org.fcrepo.doctor.problem.ObjectProblems;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

/**
 * Writes problems to a file on disk formatted as a json array.
 *
 * @author winckles
 */
public class FileProblemWriter implements ProblemWriter {

    private final SequenceWriter writer;

    public FileProblemWriter(final ObjectMapper mapper, final Path destination) {
        try {
            this.writer = mapper.writerFor(ObjectProblems.class)
                    .writeValuesAsArray(destination.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public synchronized void write(final ObjectProblems problems) {
        try {
            writer.write(problems);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}

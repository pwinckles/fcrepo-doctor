/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.writer;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.fcrepo.doctor.problem.ObjectProblems;
import org.fcrepo.doctor.problem.ProblemType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

/**
 * @author winckles
 */
public class FileProblemWriterTest {

    @TempDir
    private Path tempDir;

    private FileProblemWriter writer;
    private ObjectMapper mapper;
    private Path destination;

    @BeforeEach
    public void setup() {
        mapper = new ObjectMapper();
        destination = tempDir.resolve("output.json");
        writer = new FileProblemWriter(mapper, destination);
    }

    @AfterEach
    public void cleanup() throws IOException {
        writer.close();
    }

    @Test
    public void writeSingleEntryToFile() throws IOException {
        final var problems = new ObjectProblems("obj1");
        problems.addProblems("resource1", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ));
        problems.addProblems("resource2", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ));

        writer.write(problems);
        writer.close();

        final var output = readOutput();

        assertThat(output, containsInAnyOrder(problems));
    }

    @Test
    public void writeMultipleEntriesToFile() throws IOException {
        final var problems = new ObjectProblems("obj1");
        problems.addProblems("resource1", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ));
        problems.addProblems("resource2", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ));

        final var problems2 = new ObjectProblems("obj2");
        problems2.addProblems("resource3", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ));

        final var problems3 = new ObjectProblems("obj3");
        problems3.addProblems("resource4", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ));
        problems3.addProblems("resource5", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ));
        problems3.addProblems("resource6", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ));

        writer.write(problems);
        writer.write(problems2);
        writer.write(problems3);
        writer.close();

        final var output = readOutput();

        assertThat(output, containsInAnyOrder(problems, problems2, problems3));
    }

    @Test
    public void writeNoEntriesToFile() throws IOException {
        writer.close();

        final var output = readOutput();

        assertThat(output, empty());
    }

    private List<ObjectProblems> readOutput() throws IOException {
        return mapper.readValue(destination.toFile(), new TypeReference<>(){});
    }

}

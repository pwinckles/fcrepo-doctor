/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import edu.wisc.library.ocfl.core.OcflRepositoryBuilder;
import edu.wisc.library.ocfl.core.path.mapper.LogicalPathMappers;
import org.apache.commons.lang3.SystemUtils;
import org.fcrepo.doctor.analyzer.reader.DefaultContentReaderFactory;
import org.fcrepo.doctor.problem.ObjectProblems;
import org.fcrepo.doctor.problem.ProblemType;
import org.fcrepo.doctor.problem.detector.BinaryDescSubjectProblemDetector;
import org.fcrepo.doctor.problem.detector.ChainedProblemDetector;
import org.fcrepo.doctor.problem.writer.FileProblemWriter;
import org.fcrepo.doctor.problem.writer.ProblemWriter;
import org.fcrepo.doctor.util.StoppableThread;
import org.fcrepo.storage.ocfl.CommitType;
import org.fcrepo.storage.ocfl.DefaultOcflObjectSessionFactory;
import org.fcrepo.storage.ocfl.OcflObjectSessionFactory;
import org.fcrepo.storage.ocfl.cache.NoOpCache;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;

/**
 * @author winckles
 */
public class ObjectAnalyzerTest {

    @TempDir
    Path tempDir;
    private Path output;
    private Path ocflRoot;
    private Path ocflTemp;

    private ObjectAnalyzer analyzer;
    private BlockingQueue<String> objectIdQueue;
    private ProblemWriter problemWriter;
    private ObjectMapper mapper;

    @BeforeEach
    public void setup() throws IOException {
        output = tempDir.resolve("output.json");
        ocflTemp = Files.createDirectories(tempDir.resolve("temp"));
        ocflRoot = Paths.get("src/test/resources/repos/invalid-binary-desc-subjects");

        objectIdQueue = new LinkedBlockingQueue<>();
        final var objectSessionFactory = createObjectSessionFactory();
        final var problemDetector = new ChainedProblemDetector(List.of(new BinaryDescSubjectProblemDetector()));
        final var contentReaderFactory = new DefaultContentReaderFactory();
        mapper = new ObjectMapper();
        problemWriter = new FileProblemWriter(mapper, output);

        analyzer = new ObjectAnalyzer(objectIdQueue,
                objectSessionFactory,
                problemDetector,
                contentReaderFactory,
                problemWriter);
    }

    @AfterEach
    public void cleanup() {
        try {
            problemWriter.close();
        } catch (Exception e) {
            // don't care
        }
    }

    @Test
    public void identifyProblemInAtomicBinaryDesc() {
        final var objectId = "info:fedora/binary-invalid";

        final var results = analyzeObject(objectId);

        assertThat(results, containsInAnyOrder(
                problems(objectId, Map.of(objectId + "/fcr:metadata", Set.of(ProblemType.INVALID_BIN_DESC_SUBJ)))
        ));
    }

    @Test
    public void identifyMultipleProblemsInAgBinaryDescs() {
        final var objectId = "info:fedora/ag";

        final var results = analyzeObject(objectId);

        assertThat(results, containsInAnyOrder(
                problems(objectId, Map.of(objectId + "/child-1/fcr:metadata",
                        Set.of(ProblemType.INVALID_BIN_DESC_SUBJ),
                        objectId + "/child-2/grandchild-2/fcr:metadata",
                        Set.of(ProblemType.INVALID_BIN_DESC_SUBJ))
        )));
    }

    @Test
    public void identifyNoProblemInEmptyBinaryDesc() {
        final var objectId = "info:fedora/binary-empty";
        final var results = analyzeObject(objectId);
        assertThat(results, empty());
    }

    @Test
    public void identifyNoProblemInNonEmptyBinaryDescWithValidSubject() {
        final var objectId = "info:fedora/binary-valid";
        final var results = analyzeObject(objectId);
        assertThat(results, empty());
    }

    @Test
    public void ignoreFailuresInInvalidFedoraObject() {
        final var objectId = "info:fedora/bogus";
        final var results = analyzeObject(objectId);
        assertThat(results, empty());
    }

    private List<ObjectProblems> analyzeObject(final String objectId) {
        try {
            objectIdQueue.put(objectId);

            final var thread = new StoppableThread(analyzer, "ObjectAnalyzer");
            thread.start();

            // Need to give enough time for the thread to start
            TimeUnit.MILLISECONDS.sleep(300);
            thread.terminate();
            if (!thread.hasStopped()) {
                Thread.onSpinWait();
            }

            return readOutput();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ObjectProblems> readOutput() {
        try {
            problemWriter.close();
            return mapper.readValue(output.toFile(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectProblems problems(final String objectId,
                                    final Map<String, Set<ProblemType>> resourceProblems) {
        final var problems = new ObjectProblems(objectId);
        problems.setResourceProblems(resourceProblems);
        return problems;
    }

    private OcflObjectSessionFactory createObjectSessionFactory() {
        final var logicalPathMapper = SystemUtils.IS_OS_WINDOWS ?
                LogicalPathMappers.percentEncodingWindowsMapper() : LogicalPathMappers.percentEncodingLinuxMapper();

        final var ocflRepo = new OcflRepositoryBuilder()
                .logicalPathMapper(logicalPathMapper)
                .storage(builder -> builder.fileSystem(ocflRoot))
                .workDir(ocflTemp)
                .buildMutable();

        final var objectMapper = new ObjectMapper()
                .configure(WRITE_DATES_AS_TIMESTAMPS, false)
                .registerModule(new JavaTimeModule())
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        return new DefaultOcflObjectSessionFactory(ocflRepo,
                ocflTemp,
                objectMapper,
                new NoOpCache<>(),
                new NoOpCache<>(),
                CommitType.NEW_VERSION,
                "message",
                "fedoraAdmin",
                "info:fedora/fedoraAdmin");
    }

}

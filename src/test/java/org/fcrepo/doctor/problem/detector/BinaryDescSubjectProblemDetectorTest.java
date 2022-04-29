/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.detector;

import org.fcrepo.doctor.analyzer.reader.StringContentReader;
import org.fcrepo.doctor.problem.ProblemType;
import org.fcrepo.storage.ocfl.InteractionModel;
import org.fcrepo.storage.ocfl.ResourceHeaders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author winckles
 */
public class BinaryDescSubjectProblemDetectorTest {

    private BinaryDescSubjectProblemDetector detector;

    @BeforeEach
    public void setup() {
        detector = new BinaryDescSubjectProblemDetector();
    }

    @Test
    public void returnProblemWhenBinaryDescWithMetadataSubject() {
        final var result = detector.detect(binaryDescHeaders(), new StringContentReader(
                "<info:fedora/ag-01/child-01> <http://purl.org/dc/elements/1.1/title> \"My title 2\" .\n" +
                "<info:fedora/ag-01/child-01/fcr:metadata> <http://purl.org/dc/elements/1.1/title> \"My title 3\" .\n"
        ));

        assertEquals(Set.of(ProblemType.INVALID_BIN_DESC_SUBJ), result);
    }

    @Test
    public void returnNoProblemWhenBinaryDescWithoutMetadataSubject() {
        final var result = detector.detect(binaryDescHeaders(), new StringContentReader(
                "<info:fedora/ag-01/child-01> <http://purl.org/dc/elements/1.1/title> \"My title 2\" .\n"
        ));

        assertEquals(Collections.emptySet(), result);
    }

    @Test
    public void returnNoProblemWhenBinaryWithMetadataSubject() {
        final var result = detector.detect(binaryHeaders(), new StringContentReader(
                "<info:fedora/ag-01/child-01> <http://purl.org/dc/elements/1.1/title> \"My title 2\" .\n" +
                "<info:fedora/ag-01/child-01/fcr:metadata> <http://purl.org/dc/elements/1.1/title> \"My title 3\" .\n"
        ));

        assertEquals(Collections.emptySet(), result);
    }

    private ResourceHeaders binaryDescHeaders() {
        return ResourceHeaders.builder()
                .withInteractionModel(InteractionModel.NON_RDF_DESCRIPTION.getUri())
                .build();
    }

    private ResourceHeaders binaryHeaders() {
        return ResourceHeaders.builder()
                .withInteractionModel(InteractionModel.NON_RDF.getUri())
                .build();
    }

}

/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.detector;

import org.fcrepo.doctor.analyzer.reader.ContentReader;
import org.fcrepo.doctor.problem.ProblemType;
import org.fcrepo.storage.ocfl.InteractionModel;
import org.fcrepo.storage.ocfl.ResourceHeaders;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Identifies binary descriptions with RDF that contains subjects that end in "/fcr:metadata". The binary itself,
 * rather than its description, should be used as the subject.
 *
 * @author winckles
 */
public class BinaryDescSubjectProblemDetector implements ProblemDetector {

    private static final Pattern SEARCH = Pattern.compile("^<[^>]+/fcr:metadata>.+");

    @Override
    public Set<ProblemType> detect(final ResourceHeaders headers, final ContentReader contentReader) {
        if (isBinaryDesc(headers)) {
            try (final var reader = new BufferedReader(new InputStreamReader(contentReader.read()))) {
                while (reader.ready()) {
                    final var matcher = SEARCH.matcher(reader.readLine());
                    if (matcher.matches()) {
                        return Set.of(ProblemType.INVALID_BIN_DESC_SUBJ);
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        return Collections.emptySet();
    }

    private boolean isBinaryDesc(final ResourceHeaders headers) {
        return InteractionModel.NON_RDF_DESCRIPTION.getUri().equals(headers.getInteractionModel());
    }

}

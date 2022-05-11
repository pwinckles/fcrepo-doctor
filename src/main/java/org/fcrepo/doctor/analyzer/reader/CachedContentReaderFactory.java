/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer.reader;

import org.fcrepo.storage.ocfl.OcflObjectSession;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Factory for generating CachedContentReaders. Should only be used with S3 based repositories.
 *
 * @author winckles
 */
public class CachedContentReaderFactory implements ContentReaderFactory {

    private final Path tempDir;

    /**
     * @param tempDir temporary directory to put cache files in
     */
    public CachedContentReaderFactory(final Path tempDir) {
        this.tempDir = tempDir;
    }

    @Override
    public ContentReader newReader(final String resourceId, final OcflObjectSession session) {
        return new CachedContentReader(session, resourceId, newTempFile());
    }

    private Path newTempFile() {
        try {
            return Files.createTempFile(tempDir, "content-", null);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}

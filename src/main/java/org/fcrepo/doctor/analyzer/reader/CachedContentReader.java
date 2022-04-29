/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer.reader;

import org.fcrepo.storage.ocfl.OcflObjectSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Lazily reads a resource's content and caches it in a temp file. This should only be used when reading from
 * S3 based repositories.
 *
 * @author winckles
 */
public class CachedContentReader implements ContentReader {

    private static final Logger LOG = LoggerFactory.getLogger(CachedContentReader.class);

    private final OcflObjectSession session;
    private final String resourceId;
    private final Path tempFile;

    private boolean downloaded = false;

    /**
     * @param session the OCFL session that contains the resource
     * @param resourceId the id of the resource
     * @param tempFile the temp file to cache the resource content in
     */
    public CachedContentReader(final OcflObjectSession session,
                               final String resourceId,
                               final Path tempFile) {
        this.session = session;
        this.resourceId = resourceId;
        this.tempFile = tempFile;
    }

    public InputStream read() throws IOException {
        if (!downloaded) {
            download();
        }
        return new BufferedInputStream(Files.newInputStream(tempFile));
    }

    private void download() throws IOException {
        try (final var content = session.readContent(resourceId)) {
            content.getContentStream().ifPresentOrElse(contentStream -> {
                try {
                    Files.copy(contentStream, tempFile);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            }, () -> {
                try {
                    Files.writeString(tempFile, "");
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });

            downloaded = true;
        }
    }

    @Override
    public void close() {
        try {
            Files.deleteIfExists(tempFile);
        } catch (IOException e) {
            LOG.warn("Failed to delete temporary file: {}", tempFile, e);
        }
    }
}

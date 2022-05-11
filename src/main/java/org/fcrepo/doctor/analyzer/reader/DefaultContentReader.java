/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer.reader;

import org.fcrepo.storage.ocfl.OcflObjectSession;

import java.io.InputStream;

/**
 * Default content reader that simply fetches the content from OCFL without caching. This should be used for
 * filesystem based repositories.
 *
 * @author winckles
 */
public class DefaultContentReader implements ContentReader {

    private final String resourceId;
    private final OcflObjectSession session;

    /**
     * @param resourceId the id of the resource
     * @param session the OCFL session that contains the resource
     */
    public DefaultContentReader(final String resourceId, final OcflObjectSession session) {
        this.resourceId = resourceId;
        this.session = session;
    }

    public InputStream read() {
        return session.readContent(resourceId).getContentStream()
                .orElseGet(InputStream::nullInputStream);
    }

    @Override
    public void close() {
        // noop
    }
}

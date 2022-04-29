/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer.reader;

import org.fcrepo.storage.ocfl.OcflObjectSession;

/**
 * Factory for creating default content readers. This should be used with filesystem based OCFL repositories.
 *
 * @author winckles
 */
public class DefaultContentReaderFactory implements ContentReaderFactory {

    @Override
    public ContentReader newReader(final String resourceId, final OcflObjectSession session) {
        return new DefaultContentReader(resourceId, session);
    }

}

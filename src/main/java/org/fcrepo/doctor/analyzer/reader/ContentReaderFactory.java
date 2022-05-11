/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer.reader;

import org.fcrepo.storage.ocfl.OcflObjectSession;

/**
 * Factory for creating resource content readers
 *
 * @author winckles
 */
public interface ContentReaderFactory {

    /**
     * Creates a new content reader for the given resource
     *
     * @param resourceId the id of the resource to read
     * @param session the OCFL session the resource is in
     * @return content reader
     */
    ContentReader newReader(final String resourceId, final OcflObjectSession session);

}

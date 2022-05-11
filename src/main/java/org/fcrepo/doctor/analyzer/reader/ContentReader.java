/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer.reader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Abstraction over reading a resources content that allows the content to only be read when needed and optionally
 * cached.
 *
 * @author winckles
 */
public interface ContentReader extends AutoCloseable {

    /**
     * Returns a buffered input stream to the resource's content. The caller is responsible for closing the stream.
     *
     * @return content input stream
     * @throws IOException when unable to read the stream
     */
    InputStream read() throws IOException;

}

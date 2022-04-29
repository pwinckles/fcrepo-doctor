/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.analyzer.reader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * ContentReader used for testing purposes. Streams an input string
 *
 * @author winckles
 */
public class StringContentReader implements ContentReader {

    private final String content;

    public StringContentReader(final String content) {
        this.content = content;
    }

    @Override
    public InputStream read() {
        return new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void close() {
        // noop
    }
}

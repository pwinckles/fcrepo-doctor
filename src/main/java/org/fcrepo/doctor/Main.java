/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor;

import org.fcrepo.doctor.cli.DoctorCmd;
import picocli.CommandLine;

/**
 * Application entry point
 *
 * @author winckles
 */
public final class Main {

    private Main() {
        // cannot construct
    }

    public static void main(final String... args) {
        System.exit(new CommandLine(new DoctorCmd()).execute(args));
    }

}

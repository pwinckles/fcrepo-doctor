/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.cli;

import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * Main application command
 *
 * @author winckles
 */
@CommandLine.Command(name = "fcrepo-doctor",
        version = "0.0.1-SNAPSHOT",
        mixinStandardHelpOptions = true,
        description = "A tool for diagnosing and repairing OCFL-based Fedora repositories.")
public class DoctorCmd implements Callable<Integer> {

    @Override
    public Integer call() {
        System.out.println("Hello, world.");
        return 0;
    }

}

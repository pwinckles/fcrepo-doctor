/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem.fixer;

import org.fcrepo.storage.ocfl.OcflObjectSession;

/**
 * Fixes binary descriptions with RDF that contain subjects that end in "/fcr:metadata". The binary itself,
 * rather than its description, should be used as the subject.
 *
 * @author winckles
 */
public class BinaryDescSubjectProblemFixer implements ProblemFixer {


    @Override
    public void fix(String resourceId, OcflObjectSession objectSession) {

    }

}

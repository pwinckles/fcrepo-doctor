/*
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree.
 */

package org.fcrepo.doctor.problem;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Represents any problems found with any of the resources in an object
 *
 * @author winckles
 */
public class ObjectProblems {

    private String ocflObjectId;
    private Map<String, Set<ProblemType>> resourceProblems;

    public ObjectProblems() {
        // no-arg construct for Jackson
    }

    /**
     * @param ocflObjectId the id of the object
     */
    public ObjectProblems(final String ocflObjectId) {
        this.ocflObjectId = ocflObjectId;
        this.resourceProblems = new HashMap<>();
    }

    /**
     * Adds a new resource with its problems
     *
     * @param resourceId id of the resource with problems
     * @param problems the identified problems
     */
    public void addProblems(final String resourceId, final Set<ProblemType> problems) {
        if (problems != null && !problems.isEmpty()) {
            resourceProblems.put(resourceId, problems);
        }
    }

    /**
     * @return true if the object contains any resources with problems
     */
    public boolean hasProblems() {
        return !resourceProblems.isEmpty();
    }

    /**
     * @return the id of the object
     */
    public String getOcflObjectId() {
        return ocflObjectId;
    }

    /**
     * @param ocflObjectId the id of the object
     */
    public void setOcflObjectId(final String ocflObjectId) {
        this.ocflObjectId = ocflObjectId;
    }

    /**
     * @return the resources with problems
     */
    public Map<String, Set<ProblemType>> getResourceProblems() {
        return resourceProblems;
    }

    /**
     * @param resourceProblems the resources with problems
     */
    public void setResourceProblems(final Map<String, Set<ProblemType>> resourceProblems) {
        this.resourceProblems = resourceProblems;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ObjectProblems that = (ObjectProblems) o;
        return Objects.equals(ocflObjectId, that.ocflObjectId)
                && Objects.equals(resourceProblems, that.resourceProblems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ocflObjectId, resourceProblems);
    }

    @Override
    public String toString() {
        return "ObjectProblems{" +
                "ocflObjectId='" + ocflObjectId + '\'' +
                ", resourceProblems=" + resourceProblems +
                '}';
    }

}

package com.hedgehogsmind.springcouch2r.rest.problemdetail.problems;

import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDescriptor;

public final class Couch2rProblems {

    public static final ProblemDescriptor UNKNOWN_PROBLEM = new ProblemDescriptor(
            "couch2r-unknown-problem",
            "unknownProblem.title",
            "unknownProblem.detail",
            500
    );

    public static final ProblemDescriptor TOO_MANY_PATH_VARIABLES = new ProblemDescriptor(
            "couch2r-too-many-path-variables",
            "tooManyPathVariables.title",
            "tooManyPathVariables.detail",
            400
    );

}

package com.hedgehogsmind.springcouch2r.rest.problemdetail.problems;


import com.hedgehogsmind.springcouch2r.rest.problemdetail.I18nProblemDetailDescriptor;

public final class Couch2rProblems {

    public static final I18nProblemDetailDescriptor NOT_FOUND = new I18nProblemDetailDescriptor(
            "couch2r-not-found",
            "notFound.title",
            "notFound.detail",
            404
    );

    public static final I18nProblemDetailDescriptor UNKNOWN_PROBLEM = new I18nProblemDetailDescriptor(
            "couch2r-unknown-problem",
            "unknownProblem.title",
            "unknownProblem.detail",
            500
    );

    public static final I18nProblemDetailDescriptor TOO_MANY_PATH_VARIABLES = new I18nProblemDetailDescriptor(
            "couch2r-too-many-path-variables",
            "tooManyPathVariables.title",
            "tooManyPathVariables.detail",
            400
    );

    public static final I18nProblemDetailDescriptor WRONG_ID_TYPE = new I18nProblemDetailDescriptor(
            "couch2r-wrong-id-type",
            "wrongIdType.title",
            "wrongIdType.detail",
            400
    );

    public static final I18nProblemDetailDescriptor ID_TYPE_PARSING_NOT_SUPPORTED = new I18nProblemDetailDescriptor(
            "couch2r-id-parsing-not-supported",
            "idTypeParsingNotSupported.title",
            "idTypeParsingNotSupported.detail",
            500
    );

    public static final I18nProblemDetailDescriptor INVALID_DATA = new I18nProblemDetailDescriptor(
            "couch2r-invalid-data",
            "invalidData.title",
            "invalidData.detail",
            400
    );

}

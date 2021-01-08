package com.hedgehogsmind.springcouchrest.rest.problemdetail.problems;


import com.hedgehogsmind.springcouchrest.rest.problemdetail.I18nProblemDetailDescriptor;

public final class CouchRestProblems {

    public static final I18nProblemDetailDescriptor NOT_FOUND = new I18nProblemDetailDescriptor(
            "couch-rest-not-found",
            "notFound.title",
            "notFound.detail",
            404
    );

    public static final I18nProblemDetailDescriptor UNKNOWN_PROBLEM = new I18nProblemDetailDescriptor(
            "couch-rest-unknown-problem",
            "unknownProblem.title",
            "unknownProblem.detail",
            500
    );

    public static final I18nProblemDetailDescriptor WRONG_ID_TYPE = new I18nProblemDetailDescriptor(
            "couch-rest-wrong-id-type",
            "wrongIdType.title",
            "wrongIdType.detail",
            400
    );

    public static final I18nProblemDetailDescriptor ID_TYPE_PARSING_NOT_SUPPORTED = new I18nProblemDetailDescriptor(
            "couch-rest-id-parsing-not-supported",
            "idTypeParsingNotSupported.title",
            "idTypeParsingNotSupported.detail",
            500
    );

    public static final I18nProblemDetailDescriptor INVALID_DATA = new I18nProblemDetailDescriptor(
            "couch-rest-invalid-data",
            "invalidData.title",
            "invalidData.detail",
            400
    );

    public static final I18nProblemDetailDescriptor FORBIDDEN = new I18nProblemDetailDescriptor(
            "couch-rest-forbidden",
            "forbidden.title",
            "forbidden.detail",
            403
    );

}

package com.hedgehogsmind.springcouch2r.docs;

import com.hedgehogsmind.springcouch2r.rest.problemdetail.I18nProblemDetailDescriptor;
import com.hedgehogsmind.springcouch2r.rest.problemdetail.problems.Couch2rProblemsTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ProblemDetailsDocumentedTest {

    private static final String LINK_FILE_TO_PROBLEM_DETAILS_DOC = "LINK_ProblemDetails.md";

    protected URL getLinkFileURL() {
        return Thread.currentThread().getContextClassLoader().getResource(LINK_FILE_TO_PROBLEM_DETAILS_DOC);
    }

    protected String getContentOfFile() {
        final Scanner fileScanner = new Scanner(
                Thread.currentThread().getContextClassLoader().getResourceAsStream(LINK_FILE_TO_PROBLEM_DETAILS_DOC)
        );

        final StringBuilder sb = new StringBuilder();
        while ( fileScanner.hasNextLine() ) {
            sb.append(fileScanner.nextLine()).append(System.lineSeparator());
        }

        return sb.toString();
    }

    @Test
    void checkFileExists() {
        Assertions.assertNotNull(
                getLinkFileURL(),
                "The file (link) to the markdown file, which documents all the used " +
                        "ProblemDetail types in Couch2r, does not exist anymore."
        );
    }

    @Test
    void checkAllProblemDetailTypesDocumented() {
        final String fileContent = getContentOfFile();
        final List<I18nProblemDetailDescriptor> allProblemDescriptors = Couch2rProblemsTest.getAllProblemDescriptors();

        final List<I18nProblemDetailDescriptor> notDocumentedProblemDescriptors =
                allProblemDescriptors.stream()
                    .filter(descriptor -> !fileContent.contains(descriptor.getType().toString()))
                    .collect(Collectors.toList());

        if ( !notDocumentedProblemDescriptors.isEmpty() ) {
            Assertions.fail("Found ProblemDescriptors whose type has not been documented in the documentation:" +
                notDocumentedProblemDescriptors.stream()
                    .map(descriptor -> descriptor.getType().toString())
                    .collect(Collectors.joining("\n\t- ", "\n\t- ", ""))
            );
        }
    }
}

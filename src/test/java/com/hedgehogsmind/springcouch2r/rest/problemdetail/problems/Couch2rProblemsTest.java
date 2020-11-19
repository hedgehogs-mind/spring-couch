package com.hedgehogsmind.springcouch2r.rest.problemdetail.problems;

import com.hedgehogsmind.springcouch2r.rest.problemdetail.ProblemDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;

public class Couch2rProblemsTest {

    private static final Class CLAZZ = Couch2rProblems.class;

    private static final Locale[] REQUIRED_LOCALES = new Locale[]{
            Locale.ENGLISH,
            Locale.GERMAN
    };

    private static final ResourceBundle.Control NO_FALLBACK_RESOURCE_CONTROL = new ResourceBundle.Control() {
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            return null;
        }
    };

    @Test
    public void testOnlyPublicFields() {
        final Field[] fields = CLAZZ.getDeclaredFields();
        final Set<Field> nonPublicFields =
                Arrays.stream(fields)
                    .filter(f -> (f.getModifiers() & Modifier.PUBLIC) == 0)
                    .collect(Collectors.toSet());

        if ( !nonPublicFields.isEmpty() ) {
            final StringBuilder sb = new StringBuilder().append("Couch2rProblems contains non public fields:");
            nonPublicFields.forEach(f -> sb.append("\n\t> ").append(f.getName()));

            Assertions.fail(sb.toString());
        }
    }

    @Test
    public void testOnlyFinalFields() {
        final Field[] fields = CLAZZ.getDeclaredFields();
        final Set<Field> nonFinalFields =
                Arrays.stream(fields)
                        .filter(f -> (f.getModifiers() & Modifier.FINAL) == 0)
                        .collect(Collectors.toSet());

        if ( !nonFinalFields.isEmpty() ) {
            final StringBuilder sb = new StringBuilder().append("Couch2rProblems contains non final fields:");
            nonFinalFields.forEach(f -> sb.append("\n\t> ").append(f.getName()));

            Assertions.fail(sb.toString());
        }
    }

    @Test
    public void testOnlyStaticFields() {
        final Field[] fields = CLAZZ.getDeclaredFields();
        final Set<Field> nonStaticFields =
                Arrays.stream(fields)
                        .filter(f -> (f.getModifiers() & Modifier.STATIC) == 0)
                        .collect(Collectors.toSet());

        if ( !nonStaticFields.isEmpty() ) {
            final StringBuilder sb = new StringBuilder().append("Couch2rProblems contains non static fields:");
            nonStaticFields.forEach(f -> sb.append("\n\t> ").append(f.getName()));

            Assertions.fail(sb.toString());
        }
    }

    @Test
    public void testOnlyProblemDescriptors() {
        final Field[] fields = CLAZZ.getDeclaredFields();

        final Set<Field> nonProblemDescriptorFields =
                Arrays.stream(fields)
                .filter(f -> f.getType() != ProblemDescriptor.class)
                .collect(Collectors.toSet());

        if ( !nonProblemDescriptorFields.isEmpty() ) {
            final StringBuilder sb = new StringBuilder().append("Couch2rProblems contains non ProblemDescriptor fields:");
            nonProblemDescriptorFields.forEach(f -> sb.append("\n\t> ").append(f.getName()));

            Assertions.fail(sb.toString());
        }
    }

    @Test
    public void testAllRequiredLocalesDefined() {
        final Set<Locale> missingLocales = new HashSet<>();

        for ( final Locale locale : REQUIRED_LOCALES ) {
            try {
                ResourceBundle.getBundle(ProblemDescriptor.RESOURCE_BUNDLE_BASE_NAME, locale, NO_FALLBACK_RESOURCE_CONTROL);
            } catch ( MissingResourceException e ) {
                missingLocales.add(locale);
            }
        }

        if ( !missingLocales.isEmpty() ) {
            Assertions.fail("Missing locales for resource bundle '"+ProblemDescriptor.RESOURCE_BUNDLE_BASE_NAME+"': " +
                    missingLocales.stream().map(Locale::toString).collect(Collectors.joining(", ")));
        }
    }

    @Test
    public void testNoDuplicatedTypes() {
        final Field[] allFields = CLAZZ.getDeclaredFields();
        final Set<String> allTypes = new HashSet<>();
        final Set<String> duplicatedTypes = new HashSet<>();

        Arrays.stream(allFields)
                .filter(f -> f.getType() == ProblemDescriptor.class)
                .map(f -> {
                    try {
                        return (ProblemDescriptor) f.get(null);
                    } catch ( Throwable t ) {
                        throw new RuntimeException(t); // if thrown here, probably not public static!
                    }
                })
                .map(d -> d.getProblemType())
                .forEach(problemType -> {
                    if ( allTypes.contains(problemType) ) {
                        duplicatedTypes.add(problemType);
                    } else {
                        allTypes.add(problemType);
                    }
                });

        if ( !duplicatedTypes.isEmpty() ) {
            Assertions.fail("Found duplicated problem types:"+
                    duplicatedTypes.stream().collect(Collectors.joining("\n\t> ", "\n\t> ", "")));
        }
    }

    @Test
    public void testAllKeysImplemented() {
        final HashMap<Locale, List<String>> missingLocaleKeys = new HashMap<>();

        final List<String> allKeys = new ArrayList<>();
        final Field[] allFields = CLAZZ.getDeclaredFields();

        boolean foundMissingKey = false;

        Arrays.stream(allFields)
                .filter(f -> f.getType() == ProblemDescriptor.class)
                .forEach(f -> {
                    try {
                        final ProblemDescriptor descriptor = (ProblemDescriptor) f.get(null);
                        allKeys.add(descriptor.getTitleKey());
                        allKeys.add(descriptor.getMessageKey());

                    } catch ( Throwable t ) {
                        throw new RuntimeException(t); // if thrown here, probably not public static!
                    }
                });

        for ( final Locale locale : REQUIRED_LOCALES ) {
            final List<String> missingKeys = new ArrayList<>();
            missingLocaleKeys.put(locale, missingKeys);

            final ResourceBundle rb = ResourceBundle.getBundle(ProblemDescriptor.RESOURCE_BUNDLE_BASE_NAME, locale, NO_FALLBACK_RESOURCE_CONTROL);

            for ( final String key : allKeys ) {
                try {
                    rb.getString(key);
                } catch ( MissingResourceException e ) {
                    missingKeys.add(key);
                    foundMissingKey = true;
                }
            }
        }

        if ( foundMissingKey ) {
            final StringBuilder sb = new StringBuilder();
            missingLocaleKeys.forEach((locale, keys) -> {
                if ( !keys.isEmpty() ) {
                    sb.append("Found missing keys for locale '").append(locale).append("':\n");
                    keys.forEach(key -> sb.append("\t> ").append(key).append("\n"));
                }
            });

            Assertions.fail(sb.toString());
        }

    }



}

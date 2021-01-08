package com.hedgehogsmind.springcouchrest.rest.problemdetail.problems;

import com.hedgehogsmind.springcouchrest.rest.problemdetail.I18nProblemDetailDescriptor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class CouchRestProblemsTest {

    private static final Class CLAZZ = CouchRestProblems.class;

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
            final StringBuilder sb = new StringBuilder().append("CouchRestProblems contains non public fields:");
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
            final StringBuilder sb = new StringBuilder().append("CouchRestProblems contains non final fields:");
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
            final StringBuilder sb = new StringBuilder().append("CouchRestProblems contains non static fields:");
            nonStaticFields.forEach(f -> sb.append("\n\t> ").append(f.getName()));

            Assertions.fail(sb.toString());
        }
    }

    @Test
    public void testOnlyI18nProblemDetailDescriptors() {
        final Field[] fields = CLAZZ.getDeclaredFields();

        final Set<Field> nonI18nProblemDetailDescriptorFields =
                Arrays.stream(fields)
                .filter(f -> f.getType() != I18nProblemDetailDescriptor.class)
                .collect(Collectors.toSet());

        if ( !nonI18nProblemDetailDescriptorFields.isEmpty() ) {
            final StringBuilder sb = new StringBuilder().append("CouchRestProblems contains non I18nProblemDetailDescriptor fields:");
            nonI18nProblemDetailDescriptorFields.forEach(f -> sb.append("\n\t> ").append(f.getName()));

            Assertions.fail(sb.toString());
        }
    }

    @Test
    public void testAllRequiredLocalesDefined() {
        final Set<Locale> missingLocales = new HashSet<>();

        for ( final Locale locale : REQUIRED_LOCALES ) {
            try {
                ResourceBundle.getBundle(I18nProblemDetailDescriptor.RESOURCE_BUNDLE_BASE_NAME, locale, NO_FALLBACK_RESOURCE_CONTROL);
            } catch ( MissingResourceException e ) {
                missingLocales.add(locale);
            }
        }

        if ( !missingLocales.isEmpty() ) {
            Assertions.fail("Missing locales for resource bundle '"+ I18nProblemDetailDescriptor.RESOURCE_BUNDLE_BASE_NAME+"': " +
                    missingLocales.stream().map(Locale::toString).collect(Collectors.joining(", ")));
        }
    }

    @Test
    public void testNoDuplicatedTypes() {
        final Field[] allFields = CLAZZ.getDeclaredFields();
        final Set<URI> allTypes = new HashSet<>();
        final Set<URI> duplicatedTypes = new HashSet<>();

        Arrays.stream(allFields)
                .filter(f -> f.getType() == I18nProblemDetailDescriptor.class)
                .map(f -> {
                    try {
                        return (I18nProblemDetailDescriptor) f.get(null);
                    } catch ( Throwable t ) {
                        throw new RuntimeException(t); // if thrown here, probably not public static!
                    }
                })
                .map(d -> d.getType())
                .forEach(problemType -> {
                    if ( allTypes.contains(problemType) ) {
                        duplicatedTypes.add(problemType);
                    } else {
                        allTypes.add(problemType);
                    }
                });

        if ( !duplicatedTypes.isEmpty() ) {
            Assertions.fail("Found duplicated problem types:"+
                    duplicatedTypes.stream()
                            .map(uri -> uri.toString())
                            .collect(Collectors.joining("\n\t> ", "\n\t> ", "")));
        }
    }

    public static List<I18nProblemDetailDescriptor> getAllProblemDescriptors() {
        final Field[] allFields = CLAZZ.getDeclaredFields();

        return Arrays.stream(allFields)
                .filter(f -> f.getType() == I18nProblemDetailDescriptor.class)
                .map(f -> {
                    try {
                        return (I18nProblemDetailDescriptor) f.get(null);
                    } catch ( IllegalAccessException e ) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    @Test
    public void testAllKeysImplemented() {
        final HashMap<Locale, List<String>> missingLocaleKeys = new HashMap<>();

        final List<String> allKeys = new ArrayList<>();


        boolean foundMissingKey = false;

        getAllProblemDescriptors().forEach(descriptor -> {
                    allKeys.add(descriptor.getTitleKey());
                    allKeys.add(descriptor.getDetailKey());
                }
        );

        for ( final Locale locale : REQUIRED_LOCALES ) {
            final List<String> missingKeys = new ArrayList<>();
            missingLocaleKeys.put(locale, missingKeys);

            final ResourceBundle rb = ResourceBundle.getBundle(I18nProblemDetailDescriptor.RESOURCE_BUNDLE_BASE_NAME, locale, NO_FALLBACK_RESOURCE_CONTROL);

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

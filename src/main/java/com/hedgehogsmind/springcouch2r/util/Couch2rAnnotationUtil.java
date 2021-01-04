package com.hedgehogsmind.springcouch2r.util;

import com.hedgehogsmind.springcouch2r.annotations.Couch2rModifierAnnotation;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.annotation.MergedAnnotations;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Couch2rAnnotationUtil {

    private Couch2rAnnotationUtil() {}

    /**
     * Simple container for an annotation occurrence.
     * @param <AT> Type of annotation.
     */
    public static class AnnotationOccurrence<AT extends Annotation> {

        private final AT annotation;

        private final Object source;

        public AnnotationOccurrence(AT annotation, Object source) {
            this.annotation = annotation;
            this.source = source;
        }

        public AT getAnnotation() {
            return annotation;
        }

        public Object getSource() {
            return source;
        }

    }

    /**
     * Fetches a single annotation.
     *
     * @param clazz Class where to search on.
     * @param annotationType Type of desired annotation.
     * @param <AT> Inferred annotation type.
     * @return Annotation occurrence or null.
     */
    public static <AT extends Annotation> AnnotationOccurrence<AT> getAnnotation(final Class clazz, final Class<AT> annotationType) {
        final MergedAnnotation<AT> mergedAnnotation = MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                .get(annotationType);

        if ( !mergedAnnotation.isPresent() ) return null;

        return new AnnotationOccurrence<>(mergedAnnotation.synthesize(), mergedAnnotation.getSource());
    }

    /**
     * Calls {@link #getAnnotation(Class, Class)} and if result is null, this method throws
     * a {@link IllegalArgumentException}.
     * @param clazz Class to search on.
     * @param annotationType Type of desired annotation.
     * @param <AT> Inferred annotation type.
     * @return Annotation occurrence.
     * @throws IllegalArgumentException if given clazz has no annotation of given type.
     */
    public static <AT extends Annotation> AnnotationOccurrence<AT> getRequiredAnnotation(final Class clazz, final Class<AT> annotationType) {
        final AnnotationOccurrence<AT> occurrence = getAnnotation(clazz, annotationType);

        if ( occurrence == null )
            throw new IllegalArgumentException("Class "+clazz+" does not have annotation of type "+annotationType);

        return occurrence;
    }

    /**
     * Searches source class, where annotation has been defined.
     * @param clazz Class to start search on.
     * @param annotationType Type of annotation.
     * @return Class which declares the annotation.
     */
    public static Class getDeclaringClassOfAnnotation(final Class clazz, final Class<? extends Annotation> annotationType) {
        final Object source = MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY)
                .get(annotationType).getSource();

        if ( !(source instanceof Class) )
            throw new IllegalStateException("Annotation source is not a class");

        return (Class) source;
    }

    /**
     * Convenience method. Calls {@link #getAllAnnotationsAnnotatedWith(Class, Class)} with
     * {@link Couch2rModifierAnnotation} as annoAnnoType.
     *
     * @param clazz Class to search on.
     * @return All Annotations which are in turn annotated with {@link Couch2rModifierAnnotation}.
     */
    public static List<Annotation> getAllCouch2rModifierAnnotations(final Class clazz) {
        return getAllAnnotationsAnnotatedWith(clazz, Couch2rModifierAnnotation.class);
    }

    /**
     * Finds all annotations which are in turn annotated by an annotation of the given annoAnnoType.
     *
     * @param clazz Class to start search on.
     * @param annoAnnoType Type of annotation the desired annotations must be annotated with.
     * @return Annotations which are in turn annotated with the annotations of the given annoAnnoType.
     */
    public static List<Annotation> getAllAnnotationsAnnotatedWith(
            final Class clazz,
            final Class<? extends Annotation> annoAnnoType
    ) {
        final List<Annotation> annotations = new ArrayList<>();

        final MergedAnnotations mergedAnnotations =
                MergedAnnotations.from(clazz, MergedAnnotations.SearchStrategy.TYPE_HIERARCHY);

        final Set<Object> fetchedSources = new HashSet<>();

        // We get each merged annotation for the meta meta annotation
        mergedAnnotations.stream(annoAnnoType).forEach(ma -> {

            // We will now fetch the actual annotation from the source, where
            // the meta meta annotation has been specified
            final Object source = ma.getSource();
            if ( fetchedSources.contains(source) ) return;

            final List<Class<? extends Annotation>> metaTypes = ma.getMetaTypes();

            if ( source instanceof Class ) {
                final Class sourceClass = (Class) source;

                // We have to find the meta type which is not the meta meta anno type, but rather the
                // wanted anno (type)
                for ( final Class<? extends Annotation> metaType : metaTypes ) {
                    if ( metaType != annoAnnoType ) {

                        // Now we fetch it again directly from the source
                        MergedAnnotations.from(sourceClass, MergedAnnotations.SearchStrategy.DIRECT)
                                .stream(metaType).forEach(sourceMa -> {
                                    annotations.add(sourceMa.synthesize());
                                });
                    }
                }
            }

            fetchedSources.add(source);
        });

        return annotations;
    }

}

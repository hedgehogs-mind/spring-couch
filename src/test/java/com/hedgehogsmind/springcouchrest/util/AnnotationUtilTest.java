package com.hedgehogsmind.springcouchrest.util;

import com.hedgehogsmind.springcouchrest.annotations.CouchRest;
import com.hedgehogsmind.springcouchrest.annotations.CouchRestModifierAnnotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AnnotationUtilTest {

    @Retention(RetentionPolicy.RUNTIME)
    @CouchRestModifierAnnotation
    @Repeatable(DummyModifierAnnotation1.DummyModifierAnnotation1s.class)
    public @interface DummyModifierAnnotation1 {
        String value();

        @Retention(RetentionPolicy.RUNTIME)
        public @interface DummyModifierAnnotation1s {
            DummyModifierAnnotation1[] value();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @CouchRestModifierAnnotation
    @Repeatable(DummyModifierAnnotation2.DummyModifierAnnotation2s.class)
    public @interface DummyModifierAnnotation2 {
        String value();

        @Retention(RetentionPolicy.RUNTIME)
        public @interface DummyModifierAnnotation2s {
            DummyModifierAnnotation2[] value();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface OtherNonModifierAnnotation {

    }

    @DummyModifierAnnotation1("1.1")
    @DummyModifierAnnotation1("1.2")
    @DummyModifierAnnotation1("1.3")
    @OtherNonModifierAnnotation
    public static class SuperDemoClass {

    }

    @CouchRest
    @DummyModifierAnnotation2("2.1")
    @DummyModifierAnnotation2("2.2")
    @OtherNonModifierAnnotation
    public static class ChildDemoClass extends SuperDemoClass {

    }

    @Test
    public void testGetAllModifierAnnotations() {
        final List<Annotation> modifierAnnotations = AnnotationUtil.getAllAnnotationsAnnotatedWith(
                ChildDemoClass.class, CouchRestModifierAnnotation.class
        );

        Assertions.assertEquals(5, modifierAnnotations.size(), "Expected only two annotations");

        final Set<String> dummy1Values = modifierAnnotations.stream()
                .filter(a -> a instanceof DummyModifierAnnotation1)
                .map(a -> ((DummyModifierAnnotation1)a).value())
                .collect(Collectors.toSet());

        final Set<String> dummy2Values = modifierAnnotations.stream()
                .filter(a -> a instanceof DummyModifierAnnotation2)
                .map(a -> ((DummyModifierAnnotation2)a).value())
                .collect(Collectors.toSet());

        Assertions.assertEquals(3, dummy1Values.size(), "Expected 3 dummy1 annos");
        Assertions.assertEquals(2, dummy2Values.size(), "Expected 3 dummy2 annos");

        Assertions.assertTrue(dummy1Values.contains("1.1"), "Missing value");
        Assertions.assertTrue(dummy1Values.contains("1.2"), "Missing value");
        Assertions.assertTrue(dummy1Values.contains("1.3"), "Missing value");

        Assertions.assertTrue(dummy2Values.contains("2.1"), "Missing value");
        Assertions.assertTrue(dummy2Values.contains("2.2"), "Missing value");
    }

    @Test
    public void testGetAnnotationExisting() {
        final AnnotationUtil.AnnotationOccurrence<CouchRest> o =
                AnnotationUtil.getAnnotation(ChildDemoClass.class, CouchRest.class);

        Assertions.assertNotNull(o);
        Assertions.assertEquals(ChildDemoClass.class, o.getSource());
    }

    @Test
    public void testGetAnnotationNonExisting() {
        Assertions.assertNull(
                AnnotationUtil.getAnnotation(SuperDemoClass.class, CouchRest.class)
        );
    }

    @Test
    public void testGetRequiredAnnotationExisting() {
        // Must not throw an exception, because annotation exists
        final AnnotationUtil.AnnotationOccurrence<CouchRest> o =
            AnnotationUtil.getRequiredAnnotation(ChildDemoClass.class, CouchRest.class);

        Assertions.assertEquals(ChildDemoClass.class, o.getSource());
    }

    @Test
    public void testGetRequiredAnnotationNonExisting() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> AnnotationUtil.getRequiredAnnotation(SuperDemoClass.class, CouchRest.class)
        );
    }
}

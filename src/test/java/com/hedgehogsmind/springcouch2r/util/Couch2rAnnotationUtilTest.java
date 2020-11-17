package com.hedgehogsmind.springcouch2r.util;

import com.hedgehogsmind.springcouch2r.annotations.Couch2r;
import com.hedgehogsmind.springcouch2r.annotations.Couch2rModifierAnnotation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Couch2rAnnotationUtilTest {

    @Retention(RetentionPolicy.RUNTIME)
    @Couch2rModifierAnnotation
    @Repeatable(DummyModifierAnnotation1.DummyModifierAnnotation1s.class)
    public @interface DummyModifierAnnotation1 {
        String value();

        @Retention(RetentionPolicy.RUNTIME)
        public @interface DummyModifierAnnotation1s {
            DummyModifierAnnotation1[] value();
        }
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Couch2rModifierAnnotation
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

    @Couch2r
    @DummyModifierAnnotation2("2.1")
    @DummyModifierAnnotation2("2.2")
    @OtherNonModifierAnnotation
    public static class ChildDemoClass extends SuperDemoClass {

    }

    @Test
    public void testGetAllModifierAnnotations() {
        final List<Annotation> modifierAnnotations = Couch2rAnnotationUtil.getAllAnnotationsAnnotatedWith(
                ChildDemoClass.class, Couch2rModifierAnnotation.class
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
        final Couch2rAnnotationUtil.AnnotationOccurrence<Couch2r> o =
                Couch2rAnnotationUtil.getAnnotation(ChildDemoClass.class, Couch2r.class);

        Assertions.assertNotNull(o);
        Assertions.assertEquals(ChildDemoClass.class, o.getSource());
    }

    @Test
    public void testGetAnnotationNonExisting() {
        Assertions.assertNull(
                Couch2rAnnotationUtil.getAnnotation(SuperDemoClass.class, Couch2r.class)
        );
    }

    @Test
    public void testGetRequiredAnnotationExisting() {
        // Must not throw an exception, because annotation exists
        final Couch2rAnnotationUtil.AnnotationOccurrence<Couch2r> o =
            Couch2rAnnotationUtil.getRequiredAnnotation(ChildDemoClass.class, Couch2r.class);

        Assertions.assertEquals(ChildDemoClass.class, o.getSource());
    }

    @Test
    public void testGetRequiredAnnotationNonExisting() {
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> Couch2rAnnotationUtil.getRequiredAnnotation(SuperDemoClass.class, Couch2r.class)
        );
    }
}

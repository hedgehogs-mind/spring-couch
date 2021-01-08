package com.hedgehogsmind.springcouchrest.rest.problemdetail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProblemDetailDtoTest {

    @Test
    public void testGetters() {
        final URI type = URI.create("urn:problem-type:test");
        final URI instance = URI.create("urn:uuid:"+ UUID.randomUUID());
        final Map<String, Object> data = new HashMap<>();
        data.put("now", new Date());

        final ProblemDetailDto dto = new ProblemDetailDto(
                type,
                "Hello",
                "World",
                401,
                instance,
                data
        );

        Assertions.assertEquals(type, dto.getType());
        Assertions.assertEquals("Hello", dto.getTitle());
        Assertions.assertEquals("World", dto.getDetail());
        Assertions.assertEquals(401, dto.getStatus());
        Assertions.assertEquals(instance, dto.getInstance());
        Assertions.assertEquals(data, dto.getData());
    }

    @Test
    public void testEqualsAndHashCode() {
        final Map<String, Object> data1 = new HashMap<>();
        data1.put("now", new Date());

        final Map<String, Object> data2 = new HashMap<>();
        data2.put("now", new Date());

        final String instanceString = "urn:uuid:"+ UUID.randomUUID();

        final ProblemDetailDto dto1 = new ProblemDetailDto(
                URI.create("urn:problem-type:test"),
                "One",
                "Two",
                204,
                URI.create(instanceString),
                data1
        );

        final ProblemDetailDto dto2 = new ProblemDetailDto(
                URI.create("urn:problem-type:test"),
                "One",
                "Two",
                204,
                URI.create(instanceString),
                data2
        );

        Assertions.assertEquals(dto1.hashCode(), dto2.hashCode());
        Assertions.assertTrue(dto1.equals(dto2));
    }

    @Test
    void testStringTypeConstructor() {
        final URI instance = URI.create("urn:uuid:"+UUID.randomUUID());

        final ProblemDetailDto dto = new ProblemDetailDto(
                "stringType",
                "Hello",
                "World",
                302,
                instance
        );

        Assertions.assertEquals(URI.create("urn:problem-type:stringType"), dto.getType());
        Assertions.assertEquals("Hello", dto.getTitle());
        Assertions.assertEquals("World", dto.getDetail());
        Assertions.assertEquals(302, dto.getStatus());
        Assertions.assertEquals(instance, dto.getInstance());
        Assertions.assertNull(dto.getData());
    }

    @Test
    void testStringTypeConstructor2() {
        final URI instance = URI.create("urn:uuid:"+UUID.randomUUID());
        final Map<String, Object> data = new HashMap<>();
        data.put("key", "value");

        final ProblemDetailDto dto = new ProblemDetailDto(
                "stringType",
                "Hello",
                "World",
                302,
                instance,
                data
        );

        Assertions.assertEquals(URI.create("urn:problem-type:stringType"), dto.getType());
        Assertions.assertEquals("Hello", dto.getTitle());
        Assertions.assertEquals("World", dto.getDetail());
        Assertions.assertEquals(302, dto.getStatus());
        Assertions.assertEquals(instance, dto.getInstance());
        Assertions.assertNotNull(dto.getData());
        Assertions.assertTrue(dto.getData().containsKey("key"));
        Assertions.assertEquals("value", dto.getData().get("key"));
    }
}

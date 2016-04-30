package com.turnos.datos.vo;

import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class ETLTObjectMapperProvider implements ContextResolver<ObjectMapper> {

	private final ObjectMapper defaultObjectMapper;
	
	public ETLTObjectMapperProvider() {
		defaultObjectMapper = createDefaultMapper();
	}

	public ObjectMapper getContext(Class<?> type) {
		return defaultObjectMapper;
	}

	private static ObjectMapper createDefaultMapper() {
		return new ObjectMapper().setAnnotationIntrospector(createJaxbJacksonAnnotationIntrospector());
	}

	private static AnnotationIntrospector createJaxbJacksonAnnotationIntrospector() {

		final AnnotationIntrospector jaxbIntrospector = new JaxbAnnotationIntrospector(TypeFactory.defaultInstance());
		final AnnotationIntrospector jacksonIntrospector = new JacksonAnnotationIntrospector();

		return AnnotationIntrospector.pair(jacksonIntrospector, jaxbIntrospector);
	}
}

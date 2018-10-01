package com.fujitsu.fnc.sdnfw.vidya.mula.restApi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


import javax.ws.rs.Consumes;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.*;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.Produces;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

@Produces("application/json")
@Consumes("application/json") 
@Provider

public class JsonProvider implements MessageBodyReader<Object>,MessageBodyWriter<Object> 
{

	public static final String ALWAYS="ALWAYS";
	public static final String NON_NULL="NON_NULL";
	public static final String NON_DEFAULT="NON_DEFAULT";
	public static final String NON_EMPTY="NON_EMPTY";

	private ObjectMapper mapper = new ObjectMapper();
	// these default values represent the defaults on the fasterxml jackson ObjectMapper;
	private String  serializationInclusion = ALWAYS;
	private boolean serializationWrapRootValue = false;
	private boolean serializationIndentOutput = false;

	private boolean deserialize_fail_on_unknown_properties = true;
	private boolean deserializationUnwrapRootValue = false;

	public JsonProvider() {
	}

	public ObjectMapper getMapper() {
		return mapper;
	}

	public String getSerializationInclusion() {
		return serializationInclusion;
	}

	public void setSerializationInclusion(String serializationInclusion) {
		this.serializationInclusion = serializationInclusion.toUpperCase();
		mapper.setSerializationInclusion(JsonInclude.Include.valueOf(this.serializationInclusion));
	}

	public boolean isDeserialize_fail_on_unknown_properties() {
		return deserialize_fail_on_unknown_properties;
	}

	public void setDeserialize_fail_on_unknown_properties(
			boolean deserialize_fail_on_unknown_properties) {
		this.deserialize_fail_on_unknown_properties = deserialize_fail_on_unknown_properties;
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, this.deserialize_fail_on_unknown_properties);
	}

	public boolean isDeserializationUnwrapRootValue() {
		return deserializationUnwrapRootValue;
	}

	public void setDeserializationUnwrapRootValue(
			boolean deserializationUnwrapRootValue) {
		this.deserializationUnwrapRootValue = deserializationUnwrapRootValue;
		mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, this.deserializationUnwrapRootValue);
	}
	public boolean isSerializationWrapRootValue() {
		return serializationWrapRootValue;
	}

	public void setSerializationWrapRootValue(
			boolean serializationWrapRootValue) {
		this.serializationWrapRootValue = serializationWrapRootValue;
		mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, this.serializationWrapRootValue);
	}

	public boolean isSerializationIndentOutput() {
		return serializationIndentOutput;
	}

	public void setSerializationIndentOutput(boolean serializationIndentOutput) {
		this.serializationIndentOutput = serializationIndentOutput;
		mapper.configure(SerializationFeature.INDENT_OUTPUT, this.serializationIndentOutput);
	}

	@Override
	public boolean isWriteable ( Class<?> type, Type genericType, 
			Annotation[] annotations, MediaType mediaType ) {
		return MediaType.APPLICATION_JSON_TYPE.getType ().equals ( mediaType.getType () )
				&& MediaType.APPLICATION_JSON_TYPE.getSubtype ().equals ( mediaType.getSubtype () )

				;
	}

	// Implement properly if getSize is really needed, came to know that cxf will take care if
	//proper value is not provided.
	@Override
	public long getSize ( Object t, Class<?> type, Type genericType, 
			Annotation[] annotations, MediaType mediaType ) {
		return -1;
	}

	@Override
	public void writeTo ( Object t, Class<?> type, Type genericType, 
			Annotation[] annotations, MediaType mediaType, 
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream ) 
					throws IOException, WebApplicationException {

		ObjectWriter writer = mapper.writer();
		JsonEncoding enc = findEncoding(mediaType, httpHeaders);
		JsonGenerator g = _createGenerator(writer, entityStream, enc);
		boolean isWriteSuccessful = false;
		try{

			mapper.writeValue(g, t);

			isWriteSuccessful = true;
		} finally {
			if (isWriteSuccessful) {
				g.close();
			} else {
				try {
					g.close();
				} catch (Exception e) { }
			}
		}

	}

	@Override
	public boolean isReadable ( Class<?> type, Type genericType, 
			Annotation[] annotations, MediaType mediaType ) {
		return MediaType.APPLICATION_JSON_TYPE.getType ().equals ( mediaType.getType () )
				&& MediaType.APPLICATION_JSON_TYPE.getSubtype ().equals ( mediaType.getSubtype () )

				;
	}

	@Override
	public Object readFrom ( Class<Object> type, Type genericType, 
			Annotation[] annotations, MediaType mediaType, 
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream ) 
					throws IOException, WebApplicationException {

		ObjectReader reader = mapper.reader();
		JsonParser p = _createParser(reader, entityStream);

		Class<?> rawType = type;
		if (rawType == JsonParser.class) {
			return p;
		}

		//final TypeFactory tf = reader.getTypeFactory();
		//final JavaType resolvedType = tf.constructType(genericType);

		boolean multiValued = (rawType == MappingIterator.class);
		if (multiValued) {
			return mapper.readValues(p,rawType);
		}
		return mapper.readValue(p,rawType);

	}



	protected JsonGenerator _createGenerator(ObjectWriter writer, OutputStream rawStream, JsonEncoding enc)
			throws IOException
	{
		JsonGenerator g = writer.getFactory().createGenerator(rawStream, enc);
		g.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
		return g;
	}

	protected JsonEncoding findEncoding(MediaType mediaType, MultivaluedMap<String,Object> httpHeaders)
	{
		return JsonEncoding.UTF8;
	}


	protected JsonParser _createParser(ObjectReader reader, InputStream rawStream)
			throws IOException
	{
		JsonParser p = reader.getFactory().createParser(rawStream);
		// Important: we are NOT to close the underlying stream after
		// mapping, so we need to instruct parser:
		p.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
		return p;
	    }

}

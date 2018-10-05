package com.ramesh.smartagent.retrofit;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import retrofit.Converter;

public class GsonStringConverterFactory extends Converter.Factory
{
    private static final MediaType MEDIA_TYPE = MediaType.parse("text/plain");

    @Override
    public Converter<?, RequestBody> toRequestBody(Type type, Annotation[] annotations)
    {
        //Log.i("Request",type.toString());
        if (String.class.equals(type))// || (type instanceof Class && ((Class<?>) type).isEnum()))
        {
            return new Converter<String, RequestBody>()
            {
                @Override
                public RequestBody convert(String value) {
                    //Log.i("Request",value);
                    return RequestBody.create(MEDIA_TYPE, value);
                }
            };
        }
        return null;
    }
}

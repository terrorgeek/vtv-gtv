package com.miquido.vtv.codsservices.internal.httpjsonclient;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 30.08.12
 * Time: 00:03
 * To change this template use File | Settings | File Templates.
 */
public class UrlParams {

    @AllArgsConstructor(suppressConstructorProperties=true)
    private class Parameter {
        @Getter private final String name;
        @Getter private final String value;
    }

    ArrayList<Parameter> parameters = new ArrayList<Parameter>();

    public static UrlParams createNew() {
        return new UrlParams();
    }

    public UrlParams add(String paramName, String paramValue) {
        parameters.add(new Parameter(paramName, paramValue));
        return this;
    }

    public String toString() {
        if (parameters.size()==0)
            return "";
        StringBuilder sb = new StringBuilder();
        sb.append("?");
        boolean first = true;
        for (Parameter parameter : parameters ) {
            if (!first) sb.append('&');
            else first = false;
            sb.append(parameter.getName()).append('=').append(parameter.getValue());
        }
        return sb.toString();
    }

    CodsUrlParams copy() {
        CodsUrlParams copy = new CodsUrlParams();
        Collections.copy(copy.parameters, parameters);
        return copy;
    }

}

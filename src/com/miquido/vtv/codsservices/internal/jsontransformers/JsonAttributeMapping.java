package com.miquido.vtv.codsservices.internal.jsontransformers;

import lombok.Data;

/**
* Not used.
*/
@Data
public class JsonAttributeMapping {
    public static enum MappingDirection {
        BOTH, ONLY_FROM_JSON, ONLY_TO_JSON
    }
    private String jsonAttributeName;
    private String beanPropertyName;
    private boolean mandatory = false;
    private MappingDirection mappingDirection = MappingDirection.BOTH;

    public JsonAttributeMapping(String jsonAttributeName, String beanPropertyName, boolean mandatory, MappingDirection mappingDirection) {
        this.jsonAttributeName = jsonAttributeName;
        this.beanPropertyName = beanPropertyName;
        this.mandatory = mandatory;
        this.mappingDirection = mappingDirection;
    }
    public JsonAttributeMapping(String jsonAttributeName, String beanPropertyName) {
        this.jsonAttributeName = jsonAttributeName;
        this.beanPropertyName = beanPropertyName;
    }
}

package com.miquido.vtv.codsservices.internal.jsontransformers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Not used.
 */
public class JsonObjectMapping {
    List<JsonAttributeMapping> attrMappingList = new ArrayList<JsonAttributeMapping>();
    Map<String, JsonAttributeMapping> jsonAttributeNameMap = new HashMap<String, JsonAttributeMapping>();
    Map<String, JsonAttributeMapping> beanPropertyNameMap = new HashMap<String, JsonAttributeMapping>();

    public static JsonObjectMapping newInstance() {
        return new JsonObjectMapping();
    }

    public JsonObjectMapping add(JsonAttributeMapping jsonAttributeMapping) {
        attrMappingList.add(jsonAttributeMapping);
        jsonAttributeNameMap.put(jsonAttributeMapping.getJsonAttributeName(), jsonAttributeMapping);
        beanPropertyNameMap.put(jsonAttributeMapping.getBeanPropertyName(), jsonAttributeMapping);
        return this;
    }
    public JsonObjectMapping add(String jsonAttributeName, String beanPropertyName) {
        return add(new JsonAttributeMapping(jsonAttributeName, beanPropertyName));
    }
    public JsonObjectMapping add(String jsonAttributeName, String beanPropertyName,
                                 boolean mandatory, JsonAttributeMapping.MappingDirection mappingDirection) {
        return add(new JsonAttributeMapping(jsonAttributeName, beanPropertyName, mandatory, mappingDirection));
    }

    public List<JsonAttributeMapping> getAttrMappingsList() {
        return attrMappingList;
    }

    public List<JsonAttributeMapping> getToJsonAttrMappingList() {
        List<JsonAttributeMapping> toJsonAttrMappingList = new ArrayList<JsonAttributeMapping>();
        for (JsonAttributeMapping jsonAttributeMapping : attrMappingList) {
            JsonAttributeMapping.MappingDirection direction = jsonAttributeMapping.getMappingDirection();
            if (direction == JsonAttributeMapping.MappingDirection.BOTH
                    || direction == JsonAttributeMapping.MappingDirection.ONLY_TO_JSON) {
                toJsonAttrMappingList.add(jsonAttributeMapping);
            }
        }
        return toJsonAttrMappingList;
    }
}

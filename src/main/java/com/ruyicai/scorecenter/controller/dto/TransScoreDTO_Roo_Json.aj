// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.scorecenter.controller.dto;

import com.ruyicai.scorecenter.controller.dto.TransScoreDTO;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect TransScoreDTO_Roo_Json {
    
    public String TransScoreDTO.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static TransScoreDTO TransScoreDTO.fromJsonToTransScoreDTO(String json) {
        return new JSONDeserializer<TransScoreDTO>().use(null, TransScoreDTO.class).deserialize(json);
    }
    
    public static String TransScoreDTO.toJsonArray(Collection<TransScoreDTO> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<TransScoreDTO> TransScoreDTO.fromJsonArrayToTransScoes(String json) {
        return new JSONDeserializer<List<TransScoreDTO>>().use(null, ArrayList.class).use("values", TransScoreDTO.class).deserialize(json);
    }
    
}

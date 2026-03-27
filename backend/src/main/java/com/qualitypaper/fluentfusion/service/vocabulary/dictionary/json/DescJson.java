package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DescJson {

  @JsonProperty("desc")
  private String desc;

//    public DescJson(String desc){
//        this.desc =desc;
//    }
}

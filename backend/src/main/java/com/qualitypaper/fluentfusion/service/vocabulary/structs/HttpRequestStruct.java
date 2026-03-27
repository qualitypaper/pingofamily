package com.qualitypaper.fluentfusion.service.vocabulary.structs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class HttpRequestStruct {

  private String url;
  private String body;
  private HttpHeaders headers;
  private boolean logBody;
}

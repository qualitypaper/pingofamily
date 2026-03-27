package com.qualitypaper.fluentfusion.service.vocabulary.interfaces;

import com.qualitypaper.fluentfusion.service.vocabulary.structs.HttpRequestStruct;
import com.qualitypaper.fluentfusion.service.vocabulary.word.TranslationJson;

public interface TranslationInterface {

  String sendExampleTranslationRequest(HttpRequestStruct httpRequestStruct);

  TranslationJson sendNormalTranslationRequest(HttpRequestStruct httpRequestStruct);
}

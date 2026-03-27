package com.qualitypaper.fluentfusion.service.vocabulary.dictionary;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Desc;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.DescTranslation;
import com.qualitypaper.fluentfusion.repository.DescRepository;
import com.qualitypaper.fluentfusion.service.vocabulary.dictionary.json.DescJson;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class PosDescService {

  private final DescRepository descRepository;
  private final Lock lock = new ReentrantLock();

  public Desc createAndSave(DescJson descJson, List<DescTranslation> descTranslationList) {
    lock.lock();
    try {
      var posDesc = Desc.builder()
              .description(descJson.getDesc())
              .descriptionTranslations(descTranslationList)
              .createdAt(LocalDateTime.now())
              .build();
      descRepository.saveAndFlush(posDesc);
      return posDesc;
    } finally {
      lock.unlock();
    }
  }

  public Desc createEmpty() {
    var desc = Desc.builder()
            .description("")
            .descriptionTranslations(Collections.emptyList())
            .createdAt(LocalDateTime.now())
            .build();
    descRepository.save(desc);
    return desc;
  }
}

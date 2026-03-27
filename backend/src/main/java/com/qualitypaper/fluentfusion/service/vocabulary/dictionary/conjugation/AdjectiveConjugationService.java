package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.conjugation;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.AdjectiveConjugation;
import com.qualitypaper.fluentfusion.repository.AdjectiveConjugationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdjectiveConjugationService {

  private final AdjectiveConjugationRepository adjectiveConjugationRepository;

  public AdjectiveConjugation create(Map<String, String> mappings) {
    return new AdjectiveConjugation(mappings);
  }

  public AdjectiveConjugation create(String infinitive, Map<String, String> mappings) {
    return new AdjectiveConjugation(infinitive, mappings);
  }

  public void save(AdjectiveConjugation adjectiveConjugation) {
    adjectiveConjugationRepository.save(adjectiveConjugation);
  }
}

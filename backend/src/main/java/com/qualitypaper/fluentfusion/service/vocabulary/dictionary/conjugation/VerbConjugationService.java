package com.qualitypaper.fluentfusion.service.vocabulary.dictionary.conjugation;

import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.ConjugationJsonEntity;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.VerbConjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.VerbTenseConjugation;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.dictionary.Conjugation;
import com.qualitypaper.fluentfusion.repository.VerbConjugationRepository;
import com.qualitypaper.fluentfusion.repository.VerbTenseConjugationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class VerbConjugationService {

  private final VerbConjugationRepository verbConjugationRepository;
  private final VerbTenseConjugationRepository verbTenseConjugationRepository;

  public VerbConjugation createAndSave(String tense, Map<String, String> conjugations) {
    return VerbConjugation.builder()
            .verbTenseConjugation(List.of(createVerbTenseConjugation(tense, conjugations)))
            .build();
  }

  public VerbConjugation createAndSave(String tense, Map<String, String> conjugations, Conjugation conjugation) {
    var tenseConjugation = createAndSave(tense, conjugations);
    tenseConjugation.setConjugation(conjugation);
    return tenseConjugation;
  }

  public void save(VerbConjugation verbConjugation) {
    verbConjugationRepository.save(verbConjugation);
  }

  public ConjugationJsonEntity createAndSave(Object value, String infinitive) {
    if (value == null)
      throw new NullPointerException("While mapping verb object a null value what noticed");

    List<VerbTenseConjugation> list = new ArrayList<>();
    addTenseConjugationsToList(value, list);
    var verbConjugation = VerbConjugation.builder()
            .verbTenseConjugation(list)
            .build();
    verbConjugation.setPartOfSpeech(PartOfSpeech.VERB);
    verbConjugation.setInfinitive(infinitive);

    return verbConjugationRepository.save(verbConjugation);
  }

  // only one type of json can be mapped, as changes appear, all the structure must be updated and changed
  @SuppressWarnings("unchecked")
  private void addTenseConjugationsToList(Object value, List<VerbTenseConjugation> list) {
    Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) value;
    for (var entry : map.entrySet())
      list.add(createVerbTenseConjugation(entry.getKey(), entry.getValue()));
  }


  private VerbTenseConjugation createVerbTenseConjugation(String tense, Map<String, String> tenseConjugations) {
    var tenseConjugation = new VerbTenseConjugation(null, tense, tenseConjugations);
    verbTenseConjugationRepository.save(tenseConjugation);
    return tenseConjugation;
  }
}

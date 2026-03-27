package com.qualitypaper.fluentfusion.service.vocabulary.word;

import com.qualitypaper.fluentfusion.model.Language;
import com.qualitypaper.fluentfusion.model.vocabulary.learning.conjugations.PartOfSpeech;
import com.qualitypaper.fluentfusion.util.StringUtils;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UnneededWordsService {

  public static final Set<String> spanishArticles = new HashSet<>(List.of("el", "la", "los", "las", "un", "una", "unos", "unas"));
  public static final Set<String> germanArticles = new HashSet<>(List.of("der", "die", "das", "ein", "eine"));
  public static final Set<String> englishArticles = new HashSet<>(List.of("the", "a", "an"));
  private final Set<String> verbIdentifiers = new HashSet<>(List.of("to", "zu", "a"));
  private final Set<String> words = new HashSet<>();
  private final Set<String> phrasalVerbIdentifiers = new HashSet<>();
  private final Set<String> spreadGermanVerbs = new HashSet<>();
  private final Set<String> tenseIdentifiers = new HashSet<>();

  public static boolean isArticle(String str) {
    return spanishArticles.contains(str) || germanArticles.contains(str) || englishArticles.contains(str);
  }

  @PostConstruct
  public void init() {
    // English pronouns
    words.add("i");
    words.add("you");
    words.add("he");
    words.add("she");
    words.add("it");
    words.add("we");
    words.add("they");

    // English numbers
    words.add("one");
    words.add("two");
    words.add("three");
    words.add("four");
    words.add("five");
    words.add("six");
    words.add("seven");
    words.add("eight");
    words.add("nine");
    words.add("ten");
    words.add("eleven");
    words.add("twelve");
    words.add("thirteen");
    words.add("fourteen");
    words.add("fifteen");
    words.add("sixteen");
    words.add("seventeen");
    words.add("eighteen");
    words.add("twenty");
    words.add("thirty");
    words.add("forty");
    words.add("fifty");
    words.add("sixty");
    words.add("seventy");
    words.add("eighty");
    words.add("ninety");
    words.add("hundred");
    words.add("thousand");
    words.add("trillion");

    words.add("be");
    phrasalVerbIdentifiers.add("be");
    // English articles
    words.add("the");
    words.add("a");
    words.add("an");

    // English prepositions
    words.add("on");
    words.add("under");
    words.add("between");
    words.add("above");
    words.add("below");
    words.add("with");
    words.add("without");
    // English conjunctions
    words.add("and");
    words.add("but");
    words.add("or");
    words.add("if");
    words.add("when");
    words.add("because");

    // German numbers
    words.add("eins");
    words.add("zwei");
    words.add("drei");
    words.add("vier");
    words.add("fünf");
    words.add("sechs");
    words.add("sieben");
    words.add("acht");
    words.add("neun");
    words.add("zehn");
    words.add("elf");
    words.add("zwölf");
    words.add("dreizehn");
    words.add("vierzehn");
    words.add("fünfzehn");
    words.add("sechzehn");
    words.add("siebzehn");
    words.add("achtzehn");
    words.add("zwanzig");
    words.add("dreißig");
    words.add("vierzig");
    words.add("fünfzig");
    words.add("sechzig");
    words.add("siebzig");
    words.add("achtzig");
    words.add("neunzig");
    words.add("hundert");
    words.add("tausend");
    words.add("milliarde");
    words.add("billiarde");

    // German pronouns
    words.add("ich");
    words.add("du");
    words.add("er");
    words.add("es");
    words.add("wir");
    words.add("sie");

    // German articles
    words.add("der");
    words.add("die");
    words.add("das");
    words.add("ein");
    words.add("eine");

    // German prepositions
    words.add("in");
    words.add("auf");
    words.add("unter");
    words.add("zwischen");
    words.add("über");
    words.add("ohne");

    // German conjunctions
    words.add("und");
    words.add("aber");
    words.add("oder");
    words.add("wenn");
    words.add("weil");

    // Spanish pronouns
    words.add("yo");
    words.add("tú");
    words.add("él");
    words.add("ella");
    words.add("usted");
    words.add("nosotros");
    words.add("ellos");
    words.add("ellas");
    words.add("ustedes");

    // Spanish numbers
    words.add("uno");
    words.add("dos");
    words.add("tres");
    words.add("cuatro");
    words.add("cinco");
    words.add("seis");
    words.add("siete");
    words.add("ocho");
    words.add("nueve");
    words.add("diez");
    words.add("once");
    words.add("doce");
    words.add("trece");
    words.add("catorce");
    words.add("quince");
    words.add("dieciséis");
    words.add("diecisiete");
    words.add("dieciocho");
    words.add("veinte");
    words.add("treinta");
    words.add("cuarenta");
    words.add("cincuenta");
    words.add("sesenta");
    words.add("setenta");
    words.add("ochenta");
    words.add("noventa");
    words.add("cien");
    words.add("mil");
    words.add("millón");
    words.add("mil millones");
    words.add("billón");
    words.add("mil billones");
    // Spanish articles
    words.add("el");
    words.add("la");
    words.add("los");
    words.add("las");
    words.add("un");
    words.add("una");
    words.add("unos");
    words.add("unas");

    // Spanish prepositions
    words.add("en");
    words.add("sobre");
    words.add("bajo");
    words.add("entre");
    words.add("encima");
    words.add("debajo");
    words.add("con");
    words.add("sin");

    // Spanish conjunctions
    words.add("y");
    words.add("pero");
    words.add("o");
    words.add("si");
    words.add("cuando");
    words.add("porque");

    phrasalVerbIdentifiers.add("forward");
    phrasalVerbIdentifiers.add("across");
    phrasalVerbIdentifiers.add("to");
    phrasalVerbIdentifiers.add("up");
    phrasalVerbIdentifiers.add("about");
    phrasalVerbIdentifiers.add("into");
    phrasalVerbIdentifiers.add("along");
    phrasalVerbIdentifiers.add("through");
    phrasalVerbIdentifiers.add("of");
    phrasalVerbIdentifiers.add("off");
    phrasalVerbIdentifiers.add("out of");
    phrasalVerbIdentifiers.add("between");
    phrasalVerbIdentifiers.add("under");
    phrasalVerbIdentifiers.add("above");
    phrasalVerbIdentifiers.add("at");
    phrasalVerbIdentifiers.add("down");
    phrasalVerbIdentifiers.add("in");
    phrasalVerbIdentifiers.add("for");
    phrasalVerbIdentifiers.add("by");

    spreadGermanVerbs.add("sich");
    spreadGermanVerbs.add("mich");
    spreadGermanVerbs.add("dich");
    spreadGermanVerbs.add("euch");
    spreadGermanVerbs.add("uns");
    spreadGermanVerbs.add("ihnen");

    phrasalVerbIdentifiers.add("mit");
    phrasalVerbIdentifiers.add("bei");
    phrasalVerbIdentifiers.add("für");
    phrasalVerbIdentifiers.add("über");
    phrasalVerbIdentifiers.add("gegen");
    phrasalVerbIdentifiers.add("gegenüber");
    phrasalVerbIdentifiers.add("aus");
    phrasalVerbIdentifiers.add("zu");
    phrasalVerbIdentifiers.add("an");
    phrasalVerbIdentifiers.add("auf");
    phrasalVerbIdentifiers.add("nach");
    phrasalVerbIdentifiers.add("von");
    phrasalVerbIdentifiers.add("vor");
    phrasalVerbIdentifiers.add("her");
    phrasalVerbIdentifiers.add("ein");
    phrasalVerbIdentifiers.add("unten");
    phrasalVerbIdentifiers.add("unter");
    phrasalVerbIdentifiers.add("ab");
    phrasalVerbIdentifiers.add("hin");
    phrasalVerbIdentifiers.add("dar");


    tenseIdentifiers.add("have");
    tenseIdentifiers.add("habe");
    tenseIdentifiers.add("bin");
    tenseIdentifiers.add("sein");
    tenseIdentifiers.add("werden");
    tenseIdentifiers.add("worden");
    tenseIdentifiers.add("hatten");

    words.addAll(phrasalVerbIdentifiers);
    words.addAll(phrasalVerbIdentifiers);
    words.addAll(spreadGermanVerbs);
  }

  // method checks if word is going to be sent to CONJUGATION
  public boolean isNeeded(String word) {
    String mappedWord = word.replaceAll(" ", "").toLowerCase();
    return !words.contains(mappedWord);
  }

  public int removeUnneededWords(String string, PartOfSpeech pos, Language language) {
    String[] split = string.split(" ");
    int count = 0;
    if (split.length == 1 && !language.equals(Language.GERMAN)) return 1;
    else {
      for (String s : split) {
        if (words.contains(s.toLowerCase())) {
          count++;
        }
      }
    }

    if (language.equals(Language.GERMAN) && pos.equals(PartOfSpeech.VERB)) {
      if (isGermanPhrasalVerb(string).flag()) return 2;
    }

    return split.length - count;
  }

  public boolean isPhrasalVerb(String string) {
    String[] split = string.split(" ");
    int count = 0;

    for (String s : split) {
      String lowerCase = StringUtils.removeSpecialCharacters(s).toLowerCase();
      if (phrasalVerbIdentifiers.contains(lowerCase)) {
        count++;
      }
    }

    return count == 1 || count == 2;
  }


  public boolean isUnneededWord(String word) {
    String mappedWord = word.replaceAll(" ", "").toLowerCase();
    return words.contains(mappedWord);
  }

  public boolean isGermanSpreadVerb(String word) {
    String[] split = word.split(" ");

    for (String s : split) {
      if (spreadGermanVerbs.contains(s)) return true;
    }

    return false;
  }

  public boolean isGermanSpreadPhrasalVerb(String word) {
    String[] split = word.split(" ");

    if (split.length == 1) return isGermanPhrasalVerb(word).flag();

    for (String s : split) {
      if (phrasalVerbIdentifiers.contains(s) || spreadGermanVerbs.contains(s)) return true;
    }

    return false;
  }

  public CheckValue isGermanPhrasalVerb(String word) {
    String value = removeUnneededPart(word, Language.GERMAN)[0];

    return new CheckValue(!value.equals(word), value);
  }


  // return[0] -> filtered word
  // return[1] -> filtered part
  public String[] removeUnneededPart(String word, Language language) {
    String initialWord = word;
    if (language.equals(Language.GERMAN) && word.split(" ").length == 1) {
      StringBuilder builder = new StringBuilder();
      for (String s : phrasalVerbIdentifiers) {
        int index = word.indexOf(s);
        if (index != 0 && index != 2 && index != 3) continue;
        builder.append(s);
        if (index == 0) {
          word = word.substring(s.length());
        } else {
          word = word.substring(0, index) + word.substring(index + s.length());
        }
      }

      return new String[]{initialWord.substring(builder.length()), builder.toString()};
    } else if (language.equals(Language.SPANISH) && word.contains("se"))
      return new String[]{word.replaceFirst("se", word).replaceAll(" ", ""), "se"};


    return removeUnneededPart(word);
  }

  public String[] removeUnneededPart(String s) {
    String[] split = s.split(" ");
    StringBuilder builder = new StringBuilder();
    StringBuilder filteredPart = new StringBuilder();

    for (String sp : split) {
      if (words.contains(sp)) {
        filteredPart.append(sp);
        continue;
      }

      builder.append(sp).append(" ");
    }

    return new String[]{builder.toString().trim(), filteredPart.toString()};
  }

  public String removeTenseIdentifier(String value) {
    String[] split = value.split(" ");
    StringBuilder builder = new StringBuilder();

    for (String sp : split) {
      if (!tenseIdentifiers.contains(sp)) builder.append(sp);
    }

    return builder.toString();
  }

  public String removeVerbIdentifiers(String value) {
    String[] split = value.split(" ");
    StringBuilder builder = new StringBuilder();

    for (String sp : split) {
      if (!verbIdentifiers.contains(sp)) builder.append(sp);
    }

    return builder.toString();
  }

  public record CheckValue(boolean flag, String value) {
  }
}

package com.qualitypaper.fluentfusion.controller.dto.response.vocabulary;


import com.fasterxml.jackson.annotation.JsonInclude;

/* for now conjugation has Object type, as long as
 * it has much less priority than training algorithm
 * if any questions appear, please refer to the documentation

 * ConjugationJsonEntity is not used, as long as we want to change the
 * GermanNounConjugation, because of its storing structure*
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ConjugationResponse(
        Long id,
        String infinitive,
        Object conjugation
) {
}

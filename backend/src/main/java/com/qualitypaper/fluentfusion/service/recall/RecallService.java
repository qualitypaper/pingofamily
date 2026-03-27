package com.qualitypaper.fluentfusion.service.recall;

import com.qualitypaper.fluentfusion.model.user.User;
import com.qualitypaper.fluentfusion.model.vocabulary.VocabularyGroup;
import com.qualitypaper.fluentfusion.service.email.EmailRequest;
import com.qualitypaper.fluentfusion.service.email.NotificationService;
import com.qualitypaper.fluentfusion.service.email.template.model.EmailTemplate;
import com.qualitypaper.fluentfusion.service.user.UserDbService;
import com.qualitypaper.fluentfusion.service.vocabulary.userVocabulary.UserVocabularyDbService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service("recallCronService")
@RequiredArgsConstructor
public class RecallService {

    private final UserVocabularyDbService userVocabularyDbService;
    private final NotificationService notifgNotificationService;
	private final UserDbService userDbService;

    @Value("${microservice.frontend.host}")
    private String frontendUrl;

    @Async
    @Transactional
    public void recall() {
        List<User> users = userDbService.getActiveUsers();

        for (User user : users) {
			List<VocabularyGroup> vocabularyGroups;
			if (user.getLastPickedVocabulary() == null) {
				vocabularyGroups = user.getUserVocabularies().stream().map(e -> e.getVocabularyGroupList()).flatMap(Collection::stream).toList(); 
			} else {
				vocabularyGroups = user.getLastPickedVocabulary().getVocabularyGroupList();
			}
            for (VocabularyGroup vocabularyGroup : vocabularyGroups) {
                List<RecallProbability> probabilities = userVocabularyDbService.getRecallProbabilities(vocabularyGroup);
                long toRecallWords = probabilities.stream().filter(e -> e.prediction() < 0.8f).count();
                if (toRecallWords != 0) {
                    notifgNotificationService.sendNotification(EmailRequest.builder()
                            .subject("Don't Let These Words Slip Away – Quick Review Now!")
                            .message("You've got " + toRecallWords + " words from recent lessons waiting to be reviewed before they fade! A quick 5-minute session locks them in and keeps your streak alive. " +
                                    "\nWhy now? Boost retention with spaced repetition")
                            .buttonUrl(frontendUrl + "/training?vocabularyGroupId=" + vocabularyGroup.getId())
                            .buttonText("Review now")
                            .templateType(EmailTemplate.TemplateType.NOTIFICATION)
                            .recipients(new String[]{user.getEmail()})
                            .build());
                    continue;
                }
                long newWordsCount = userVocabularyDbService.countNewWords(vocabularyGroup);
                if (newWordsCount != 0) {
                    notifgNotificationService.sendNotification(EmailRequest.builder()
                            .subject("Ready to Kickstart Your Language Journey? Add Your First Words!")
                            .message("""
                                    You've added %s new words to your list – great start! But they won't stick without studying. Jump into your first review session now to lock them in with spaced repetition.
                                    Why act now?
                                    Maximize retention: Study soon to remember up to 80%% more effectively.
                                    Build your streak: Complete it to earn badges and keep momentum."""
                                    .formatted(newWordsCount))
                            .templateType(EmailTemplate.TemplateType.NOTIFICATION)
                            .buttonUrl(frontendUrl + "/training?vocabularyGroupId=" + vocabularyGroup.getId())
                            .buttonText("Start learning now")
                            .recipients(new String[]{user.getEmail()})
                            .build());
                    continue;
                }

                notifgNotificationService.sendNotification(EmailRequest.builder()
                        .subject("Ready to Kickstart Your Language Journey? Add Your First Words!")
                        .message("""
                                Welcome to ${appName}! You're just getting started, and the best way to begin is by adding your first words. No words added yet? That's your cue to dive in and build a strong foundation for your language journey.
                                Why add now?

                                Personalized start: Pick words tailored to your goals and level.
                                Effortless learning: Use spaced repetition to retain them easily."""
                        )
                        .templateType(EmailTemplate.TemplateType.NOTIFICATION)
                        .buttonUrl(frontendUrl + "/vocabulary")
                        .buttonText("Start now")
                        .recipients(new String[]{user.getEmail()})
                        .build());

            }
        }
    }
}

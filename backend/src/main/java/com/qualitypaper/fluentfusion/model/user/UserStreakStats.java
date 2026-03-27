package com.qualitypaper.fluentfusion.model.user;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStreakStats {
  @Column(columnDefinition = "bigint default 0")
  private Long streak;
  @Column(columnDefinition = "bigint default 0")
  private Long maxStreak;
  @Column(columnDefinition = "bigint default 0")
  private Long lastTrainingTime;

}

package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.user.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, Long> {
}
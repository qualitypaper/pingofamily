package com.qualitypaper.fluentfusion.repository;

import com.qualitypaper.fluentfusion.model.user.UserImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {
}
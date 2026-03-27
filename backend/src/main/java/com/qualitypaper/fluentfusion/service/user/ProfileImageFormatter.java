package com.qualitypaper.fluentfusion.service.user;

import com.qualitypaper.fluentfusion.model.user.UserImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ProfileImageFormatter {

  @Value("${url.static.images}")
  private String staticImagesUrl;

  public String format(UserImage image) {
    return switch (image.getImageType()) {
      case SOCIAL_MEDIA -> image.getImageUrl();
      case CUSTOM -> staticImagesUrl + image.getImageUrl();
      case DEFAULT -> null;
      default -> throw new IllegalArgumentException("Unknown image type: " + image.getImageType());
    };
  }
}

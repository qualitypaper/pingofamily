package com.qualitypaper.fluentfusion.service.pts.s3;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Service {
  private final S3Client s3client;

  @Value("${service.aws.s3.bucket}")
  private String bucketName;

  public String uploadObject(String filename, ObjectType type) {
    try {
      char separator = System.getProperty("os.name").contains("Windows") ? '\\' : '/';

      String key = filename.substring(filename.lastIndexOf(separator) + 1);
      String s3filename;
      switch (type) {
        case AUDIO -> s3filename = "audio/" + key;
        case IMAGE -> s3filename = "images/" + key;
        case CONFIG -> s3filename = "config/" + key;
        case null, default -> throw new IllegalArgumentException("Unknown object type: " + type);
      }
      if (objectExists(s3filename)) {
        return key;
      }

      PutObjectRequest putOb = PutObjectRequest.builder()
              .bucket(bucketName)
              .key(s3filename)
              .build();

      File file = new File(filename);
      s3client.putObject(putOb, RequestBody.fromFile(file));

      log.info("Successfully placed {} into bucket {}", filename, bucketName);

      return key;
    } catch (S3Exception e) {
      throw new IllegalStateException(e);
    }
  }

  public String[] getFolder(String folder) {
    try {
      ListObjectsV2Request req = ListObjectsV2Request.builder()
              .bucket(bucketName)
              .prefix(folder)
              .build();

      ListObjectsV2Response response = s3client.listObjectsV2(req);

      return response.contents().stream()
              .skip(1)
              .map(e -> e.key().substring(e.key().lastIndexOf('/') + 1))
              .toList().toArray(new String[0]);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }


  public boolean objectExists(String key) {
    try {
      HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
              .bucket(bucketName)
              .key(key)
              .build();

      HeadObjectResponse response = s3client.headObject(headObjectRequest);
      if (response.contentLength() == 0) {
        log.warn("Object {} exists but is empty", key);
        return false;
      }

      return true;
    } catch (NoSuchKeyException _) {
      return false;
    } catch (S3Exception e) {
      log.error("Error checking if object exists: {}", e.getMessage());
      throw new IllegalStateException("Failed to check object existence", e);
    }
  }

}
package app.ruzi.service.document;

import app.ruzi.configuration.jwt.UserJwt;
import io.minio.*;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MinioService {
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    /* private final DocumentArchiveHashRepository documentArchiveHashRepository; */

    public void uploadFile(
            UserJwt userJwt,
            MultipartFile multipartFile,
            String PATH_DOCUMENT,
            String parentKey,
            String parentName
    ) throws Exception {
        /* String fileHash = requestDto.getMultipartFileHash();
        boolean existsByHash = documentArchiveHashRepository.existsByHash(fileHash);

        if (existsByHash) {
            return;
        } */

        Map<String, String> tags = new HashMap<>();
        tags.put("parentKey", parentKey);
        tags.put("parentName", parentName);
        tags.put("isDeleted", "0");
        tags.put("insUser", userJwt.getUsername());

        try {
            assert multipartFile != null;
            try (InputStream inputStream = multipartFile.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(PATH_DOCUMENT + "/" + multipartFile.getOriginalFilename())
                                .stream(inputStream, multipartFile.getSize(), -1)
                                .tags(tags)
                                .contentType(multipartFile.getContentType())
                                .build()
                );
            }
        } catch (MinioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "ERROR0001");
        }

        /* DocumentArchiveHash documentArchiveHash = new DocumentArchiveHash();
        documentArchiveHash.setHash(fileHash);
        documentArchiveHashRepository.save(documentArchiveHash); */
    }

    public void uploadThumbnail(
            UserJwt userJwt,
            MultipartFile multipartFile,
            String path,
            String parentKey,
            String parentName
    ) {
        Map<String, String> tags = new HashMap<>();
        tags.put("parentKey", parentKey);
        tags.put("parentName", parentName);
        tags.put("isDeleted", "0");
        tags.put("insUser", userJwt.getUsername());
        tags.put("type", "thumbnail");

        try (InputStream inputStream = multipartFile.getInputStream()) {

            ByteArrayOutputStream thumbOutput = new ByteArrayOutputStream();
            Thumbnails.of(inputStream)
                    .size(256, 256)
                    .outputFormat("jpg")
                    .toOutputStream(thumbOutput);

            byte[] thumbBytes = thumbOutput.toByteArray();
            InputStream thumbStream = new ByteArrayInputStream(thumbBytes);

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(path + "/" + multipartFile.getOriginalFilename())
                            .stream(thumbStream, thumbBytes.length, -1)
                            .tags(tags)
                            .contentType("image/jpeg")
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Thumbnail yaratishda xatolik: " + e.getMessage(), e);
        }
    }

    public InputStream downloadFiles(String docNameUni) {

        try {

            GetObjectArgs getObjectArgs = GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(docNameUni)
                    .build();

            return minioClient.getObject(getObjectArgs);
        } catch (MinioException e) {
            throw new RuntimeException("Error loading file: " + docNameUni, e);
        } catch (IOException | NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteFile(String docNameUni) {
        Map<String, String> tags = new HashMap<>();
        tags.put("isDeleted", "1");

        try {
            // 🔹 Asosiy rasmni o‘chirish
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(docNameUni)
                            .build()
            );

            // 🔹 Thumbnail rasmni o‘chirish
            String thumbPath = docNameUni.replace("image/", "thumb/");
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(thumbPath)
                            .build()
            );

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла из MinIO: " + docNameUni, e);
        }
    }

    public void renameFile(String oldPath, String newPath) {
        try {
            // 1️⃣ Faylni yangi nom bilan ko‘chirib olamiz (rename)
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newPath)
                            .source(CopySource.builder()
                                    .bucket(bucketName)
                                    .object(oldPath)
                                    .build())
                            .build()
            );

            // 2️⃣ Eski faylni o‘chiramiz
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(oldPath)
                            .build()
            );

            String oldThumbPath = oldPath.replace("image/", "thumb/");
            String newThumbPath = newPath.replace("image/", "thumb/");

            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucketName)
                            .object(newThumbPath)
                            .source(CopySource.builder()
                                    .bucket(bucketName)
                                    .object(oldThumbPath)
                                    .build())
                            .build()
            );

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(oldThumbPath)
                            .build()
            );


        } catch (Exception e) {
            throw new RuntimeException("❌ MinIO faylni o‘zgartirishda xatolik: " + e.getMessage(), e);
        }
    }
}

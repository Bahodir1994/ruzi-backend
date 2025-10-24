package app.ruzi.service.document;

import app.ruzi.configuration.jwt.UserJwt;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.SetObjectTagsArgs;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

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
            minioClient.setObjectTags(
                    SetObjectTagsArgs.builder()
                            .bucket(bucketName)
                            .object(docNameUni)
                            .tags(tags)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при удалении файла из MinIO: " + docNameUni, e);
        }
    }
}

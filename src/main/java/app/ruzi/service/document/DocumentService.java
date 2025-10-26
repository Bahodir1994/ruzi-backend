package app.ruzi.service.document;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.entity.tasks.Document;
import app.ruzi.entity.tasks.DocumentHash;
import app.ruzi.repository.tasks.DocumentHashRepository;
import app.ruzi.repository.tasks.DocumentRepository;
import app.ruzi.service.mappers.DocumentMapper;
import app.ruzi.service.payload.tasks.DocumentRequestDto;
import app.ruzi.service.payload.tasks.DocumentResponseDto;
import app.ruzi.service.payload.tasks.DocumentSingleRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService implements DocumentServiceImplement {

    private final MinioService minioService;
    private final JwtUtils jwtUtils;

    private final DocumentHashRepository documentHashRepository;
    private final DocumentRepository documentRepository;

    private final String PATH_DOCUMENT = "image";
    private final String PATH_DOCUMENT_THUMB = "thumb";

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Integer create(DocumentRequestDto requestDto) {
        int countData = 0;

        final UserJwt userJwt;
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        boolean existsByHash = documentHashRepository.existsByHash(requestDto.getMultipartFileHash());
        if (!existsByHash) {
            Document document = new Document();
            document.setParentId(userJwt.getClientId());
            document.setDocName(requestDto.getMultipartFile().getOriginalFilename());
            document.setDocType(requestDto.getMultipartFile().getContentType());
            document.setHash(requestDto.getMultipartFileHash());
            document.setFileDate(new Date());
            UUID uuid = UUID.randomUUID();
            document.setDocNameUni(uuid.toString().replaceAll("[^a-zA-Z0-9]", ""));
            documentRepository.save(document);

            try {
                minioService.uploadFile(
                        userJwt,
                        requestDto.getMultipartFile(),
                        PATH_DOCUMENT + "/" + userJwt.getClientId(),
                        document.getParentId(),
                        Document.class.getSimpleName()
                );

                minioService.uploadThumbnail(
                        userJwt,
                        requestDto.getMultipartFile(),
                        PATH_DOCUMENT_THUMB + "/" + userJwt.getClientId(),
                        document.getParentId(),
                        Document.class.getSimpleName()
                );

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            DocumentHash documentArchiveHash = new DocumentHash();
            documentArchiveHash.setHash(requestDto.getMultipartFileHash());
            documentHashRepository.save(documentArchiveHash);
        } else {
            Document document = new Document();
            document.setParentId(userJwt.getClientId());
            document.setDocName(requestDto.getMultipartFile().getOriginalFilename());
            document.setDocType(requestDto.getMultipartFile().getContentType());
            document.setHash(requestDto.getMultipartFileHash());
            document.setFileDate(new Date());
            UUID uuid = UUID.randomUUID();
            document.setDocNameUni(uuid.toString().replaceAll("[^a-zA-Z0-9]", ""));
            documentRepository.save(document);

            try {
                minioService.uploadFile(
                        userJwt,
                        requestDto.getMultipartFile(),
                        PATH_DOCUMENT + "/" + userJwt.getClientId(),
                        userJwt.getClientId(),
                        Document.class.getSimpleName()
                );

                minioService.uploadThumbnail(
                        userJwt,
                        requestDto.getMultipartFile(),
                        "thumb/" + userJwt.getClientId(),
                        document.getParentId(),
                        Document.class.getSimpleName()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return countData;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> getDocumentPage(int page, int size) {
        final UserJwt userJwt;
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "fileDate"));
        Page<Document> pageResult = documentRepository.findAllByParentId(userJwt.getClientId(), pageable);

        Map<String, Object> result = new HashMap<>();
        result.put("data", pageResult.getContent());
        result.put("total", pageResult.getTotalElements());
        return result;
    }

    @Override
    public List<Document> read() {
        UserJwt userJwt;
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        UserJwt finalUserJwt = userJwt;

        List<Document> byParentId = documentRepository.findByParentId(finalUserJwt.getClientId());
        return byParentId;
    }

    @Override
    public DocumentResponseDto download(String id) {
        final UserJwt userJwt;
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        DocumentResponseDto documentResponseDto = new DocumentResponseDto();

        Optional<Document> optionalDocument = documentRepository.findById(id);

        if (optionalDocument.isPresent()) {
            Document document = optionalDocument.get();
            String docName = document.getDocName();

            try (InputStream fileStream = minioService.downloadFiles(PATH_DOCUMENT + "/" + userJwt.getClientId() + "/" + docName)) {
                documentResponseDto.setFileBytes(fileStream.readAllBytes());
                documentResponseDto.setDocName(document.getDocName());
                documentResponseDto.setDocType(document.getDocType());
                documentResponseDto.setDocNameUni(docName);
            } catch (IOException e) {
                throw new RuntimeException("Error when download MinIO: " + docName, e);
            }
        }

        return documentResponseDto;
    }

    @Override
    @Transactional
    public void update(DocumentSingleRequestDto singleRequestDto) {
        UserJwt userJwt;
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Document doc = documentRepository.findById(singleRequestDto.getId())
                .orElseThrow(() -> new IllegalArgumentException("Document topilmadi"));

        String currentName = doc.getDocName();
        String currentExt = "";

        // 🔹 formatni ajratamiz (.jpg, .png, .pdf, ...)
        int dotIndex = currentName.lastIndexOf('.');
        if (dotIndex != -1) {
            currentExt = currentName.substring(dotIndex);
        }

        String newName = singleRequestDto.getName();
        if (!newName.contains(".")) {
            newName = newName + currentExt;
        }

        // 🔹 MinIO fayl yo‘llari
        String oldPath = PATH_DOCUMENT + "/" + userJwt.getClientId() + "/" + currentName;
        String newPath = PATH_DOCUMENT + "/" + userJwt.getClientId() + "/" + newName;

        // 🔹 MinIO faylini ham nomini o‘zgartiramiz
        try {
            minioService.renameFile(oldPath, newPath);
        } catch (Exception e) {
            throw new RuntimeException("MinIO faylni o‘zgartirishda xatolik: " + e.getMessage(), e);
        }

        // 🔹 Bazada nomni yangilaymiz
        documentRepository.updateByIdAndDocName(singleRequestDto.getId(), userJwt.getClientId(), newName);
    }

    @Override
    @Transactional
    public void delete(DocumentSingleRequestDto singleRequestDto) {
        final UserJwt userJwt;
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        for (String idOne : singleRequestDto.getIdList()) {
            Optional<Document> optionalDocument = documentRepository.findById(idOne);

            if (optionalDocument.isPresent()) {
                Document document = optionalDocument.get();
                String docName = document.getDocName();

                documentRepository.delete(document);

                if (documentRepository.countAllByHash(document.getHash()) == 0) {
                    minioService.deleteFile(PATH_DOCUMENT + "/" + userJwt.getClientId() + "/" + docName);

                    documentRepository.deleteById(document.getHash());
                }
            } else {
                throw new RuntimeException("Документ не найден");
            }
        }
    }
}

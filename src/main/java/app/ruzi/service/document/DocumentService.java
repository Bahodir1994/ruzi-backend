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
import org.springframework.beans.factory.annotation.Value;
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
    private final String PATH_DOCUMENT = "image";
    private final DocumentRepository documentRepository;

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
            document.setParentId(requestDto.getParentId());
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            DocumentHash documentArchiveHash = new DocumentHash();
            documentArchiveHash.setHash(requestDto.getMultipartFileHash());
            documentHashRepository.save(documentArchiveHash);
        } else {
            Document bntDocument = new Document();
            bntDocument.setParentId(requestDto.getParentId());
            bntDocument.setDocName(requestDto.getMultipartFile().getOriginalFilename());
            bntDocument.setDocType(requestDto.getMultipartFile().getContentType());
            bntDocument.setHash(requestDto.getMultipartFileHash());
            bntDocument.setFileDate(new Date());
            UUID uuid = UUID.randomUUID();
            bntDocument.setDocNameUni(uuid.toString().replaceAll("[^a-zA-Z0-9]", ""));
            documentRepository.save(bntDocument);

            try {
                minioService.uploadFile(
                        userJwt, requestDto.getMultipartFile(),
                        PATH_DOCUMENT + "/" + userJwt.getClientId(),
                        bntDocument.getParentId(),
                        Document.class.getSimpleName()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return countData;
    }

    @Override
    public List<DocumentResponseDto> read(DocumentSingleRequestDto singleRequestDto) {
        List<Document> byParentId = documentRepository.findByParentId(singleRequestDto.getParentId());
        return DocumentMapper.INSTANCE.toDtoList(byParentId);
    }

    @Override
    public DocumentResponseDto download(DocumentSingleRequestDto singleRequestDto) {
        final UserJwt userJwt;
        try {
            userJwt = jwtUtils.extractUserFromToken();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        DocumentResponseDto documentResponseDto = new DocumentResponseDto();

        Optional<Document> optionalDocument = documentRepository.findById(singleRequestDto.getId());

        if (optionalDocument.isPresent()) {
            Document bntDocument = optionalDocument.get();
            String docName = bntDocument.getDocName();

            try (InputStream fileStream = minioService.downloadFiles(PATH_DOCUMENT + "/" + userJwt.getClientId() + "/" + docName)) {
                documentResponseDto.setFileBytes(fileStream.readAllBytes());
                documentResponseDto.setDocName(bntDocument.getDocName());
                documentResponseDto.setDocType(bntDocument.getDocType());
                documentResponseDto.setDocNameUni(docName);
            } catch (IOException e) {
                throw new RuntimeException("Error when download MinIO: " + docName, e);
            }
        }

        return documentResponseDto;
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
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

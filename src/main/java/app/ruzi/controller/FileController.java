package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.CustomAuthRole;
import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.service.document.DocumentService;
import app.ruzi.service.payload.tasks.DocumentRequestDto;
import app.ruzi.service.payload.tasks.DocumentResponseDto;
import app.ruzi.service.payload.tasks.DocumentSingleRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/route-file/crud")
@RequiredArgsConstructor
public class FileController {
    private final HandlerService handlerService;
    private final DocumentService documentService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @PreAuthorize("hasAuthority('ROLE_IMAGE_READ')")
    @CustomAuthRole(roles = {"ROLE_IMAGE_CREATE"})
    @MethodInfo(methodName = "file-create")
    public ResponseEntity<Object> create(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @ModelAttribute DocumentRequestDto documentRequestDto,
            BindingResult bindingResult
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> documentService.create(documentRequestDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_IMAGE_READ')")
    @MethodInfo(methodName = "file-read")
    public ResponseEntity<Object> read(
            @RequestHeader(value = "Accept-Language", required = false) String langType
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                documentService::read,
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }


    @GetMapping("/page")
    @PreAuthorize("hasAuthority('ROLE_IMAGE_READ')")
    @MethodInfo(methodName = "file-read-page")
    public ResponseEntity<Object> readPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "40") int size,
            @RequestHeader(value = "Accept-Language", required = false) String langType
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> documentService.getDocumentPage(page, size),
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }


    @GetMapping(value = "/{id}")
    @PreAuthorize("hasAuthority('ROLE_IMAGE_READ')")
    @MethodInfo(methodName = "file-download")
    public ResponseEntity<InputStreamResource> download(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable(value = "id") String id
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> documentService.download(id),
                langType
        );

        DocumentResponseDto fileForDownloadByHash = (DocumentResponseDto) messageResponse.getData();

        InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(fileForDownloadByHash.getFileBytes()));

        return ResponseEntity
                .ok()
                .contentType(MediaType.parseMediaType(fileForDownloadByHash.getDocType()))  // Динамический тип файла
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileForDownloadByHash.getDocName() + "\"")
                .body(resource);
    }

    @PatchMapping
    @PreAuthorize("hasAuthority('ROLE_IMAGE_READ')")
    @MethodInfo(methodName = "file-update")
    public ResponseEntity<Object> update(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @RequestBody @Valid DocumentSingleRequestDto singleRequestDto,
            BindingResult bindingResult
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> documentService.update(singleRequestDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('ROLE_IMAGE_DELETE')")
    @MethodInfo(methodName = "file-delete")
    public ResponseEntity<Object> delete(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @Valid @RequestBody DocumentSingleRequestDto singleRequestDto,
            BindingResult bindingResult
    ) {
        MessageResponse messageResponse = handlerService.handleRequest(
                () -> documentService.delete(singleRequestDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

}


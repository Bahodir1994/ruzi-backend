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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@RestController
@RequestMapping("/route-file/crud")
@RequiredArgsConstructor
public class FileController {
    private final HandlerService handlerService;
    private final DocumentService documentService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @CustomAuthRole(roles = {"ROLE_ITEM_CREATE"})
    @MethodInfo(methodName = "bnt-file-create")
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

    @GetMapping("/{type}/{id}")
    @CustomAuthRole(roles = {"ROLE_BNT_READ", "ROLE_BNT_READ_FULL"})
    @MethodInfo(methodName = "bnt-file-read")
    public ResponseEntity<Object> read(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable(value = "type") String type,
            @PathVariable(value = "id") String id,
            @Valid DocumentSingleRequestDto singleRequestDto,
            BindingResult bindingResult
    ) {
        singleRequestDto.setParentId(id);
        singleRequestDto.setType(type);

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> documentService.read(singleRequestDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @GetMapping(value = "/{type}/{docId}/{id}")
    @CustomAuthRole(roles = {"ROLE_BNT_READ", "ROLE_BNT_READ_FULL"})
    @MethodInfo(methodName = "bnt-file-download")
    public ResponseEntity<InputStreamResource> download(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable(value = "type") String type,
            @PathVariable(value = "docId") String docId,
            @PathVariable(value = "id") String id,
            @Valid DocumentSingleRequestDto singleRequestDto,
            BindingResult bindingResult
    ) {

        singleRequestDto.setType(type);
        singleRequestDto.setParentId(id);
        singleRequestDto.setId(docId);

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> documentService.download(singleRequestDto),
                bindingResult,
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

    @DeleteMapping
    @CustomAuthRole(roles = {"ROLE_BNT_DELETE"})
    @MethodInfo(methodName = "bnt-file-delete")
    public ResponseEntity<Object> delete(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @RequestParam("parentId") String summaryId,
            @RequestParam("type") String type,
            @RequestParam("documentId") List<String> documentIds,
            @Valid DocumentSingleRequestDto singleRequestDto,
            BindingResult bindingResult
    ) {
        singleRequestDto.setParentId(summaryId);
        singleRequestDto.setType(type);
        singleRequestDto.setIdList(documentIds);

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> documentService.delete(singleRequestDto),
                bindingResult,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

}


package app.ruzi.service.document;

import app.ruzi.service.payload.tasks.DocumentRequestDto;
import app.ruzi.service.payload.tasks.DocumentResponseDto;
import app.ruzi.service.payload.tasks.DocumentSingleRequestDto;

import java.util.List;

public interface DocumentServiceImplement {

    Integer create(DocumentRequestDto requestDto);

    List<DocumentResponseDto> read(DocumentSingleRequestDto singleRequestDto);

    DocumentResponseDto download(DocumentSingleRequestDto singleRequestDto);

    void delete(DocumentSingleRequestDto singleRequestDto);
}

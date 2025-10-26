package app.ruzi.service.document;

import app.ruzi.entity.tasks.Document;
import app.ruzi.service.payload.tasks.DocumentRequestDto;
import app.ruzi.service.payload.tasks.DocumentResponseDto;
import app.ruzi.service.payload.tasks.DocumentSingleRequestDto;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface DocumentServiceImplement {

    Integer create(DocumentRequestDto requestDto);

    Map<String, Object> getDocumentPage(int page, int size);

    List<Document> read();

    DocumentResponseDto download(String id);

    void update(DocumentSingleRequestDto singleRequestDto);

    void delete(DocumentSingleRequestDto singleRequestDto);
}

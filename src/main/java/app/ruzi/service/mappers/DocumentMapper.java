package app.ruzi.service.mappers;

import app.ruzi.entity.tasks.Document;
import app.ruzi.service.payload.tasks.DocumentResponseDto;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DocumentMapper {
    DocumentMapper INSTANCE = Mappers.getMapper(DocumentMapper.class);

    Document toEntity(DocumentResponseDto documentResponseDto);

    DocumentResponseDto toDto(Document document);

    List<DocumentResponseDto> toDtoList(List<Document> document);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Document partialUpdate(DocumentResponseDto documentResponseDto, @MappingTarget Document document);
}
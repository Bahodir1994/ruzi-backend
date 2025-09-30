package app.ruzi.service.app.category;

import app.ruzi.entity.app.Category;
import app.ruzi.service.payload.app.CategoryDto;
import app.ruzi.service.payload.app.CategoryTranslationDto;
import app.ruzi.service.payload.app.CategoryWithItemDto;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface CategoryServiceImplement {

    DataTablesOutput<CategoryWithItemDto> getCategories(DataTablesInput dataTablesInput);

    List<CategoryWithItemDto> getCategoriesWithItems();

    List<CategoryDto> getCategoryTree();

    List<CategoryTranslationDto> getCategoryTranslation(String categoryId);

}

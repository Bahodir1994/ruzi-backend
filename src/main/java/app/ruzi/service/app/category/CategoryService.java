package app.ruzi.service.app.category;

import app.ruzi.entity.app.Category;
import app.ruzi.entity.app.CategoryTranslation;
import app.ruzi.entity.app.Item;
import app.ruzi.repository.app.CategoryRepository;
import app.ruzi.repository.app.ItemRepository;
import app.ruzi.service.payload.app.CategoryDto;
import app.ruzi.service.payload.app.CategoryTranslationDto;
import app.ruzi.service.payload.app.CategoryWithItemDto;
import app.ruzi.service.payload.app.ItemDto;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceImplement{
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional(readOnly = true)
    public DataTablesOutput<CategoryWithItemDto> getCategories(DataTablesInput dataTablesInput) {
        DataTablesOutput<Category> all = categoryRepository.findAll(dataTablesInput);
        List<CategoryWithItemDto> categoryWithItemDtos = new ArrayList<>();
        for (Category cat : all.getData()) {
            CategoryWithItemDto dto = CategoryWithItemDto.builder()
                    .id(cat.getId())
                    .code(cat.getCode())
                    .items(new ArrayList<>())
                    .build();
            categoryWithItemDtos.add(dto);
        }
        DataTablesOutput<CategoryWithItemDto> newOutput = new DataTablesOutput<>();
        newOutput.setDraw(all.getDraw());
        newOutput.setError(all.getError());
        newOutput.setRecordsFiltered(all.getRecordsFiltered());
        newOutput.setRecordsTotal(all.getRecordsTotal());
        newOutput.setSearchPanes(all.getSearchPanes());
        return newOutput;
    }

    @Override
    public List<CategoryWithItemDto> getCategoriesWithItems() {
        List<Category> categories = categoryRepository.findAll();
        List<Item> items = itemRepository.findAll();

        Map<String, CategoryWithItemDto> mainCategoryMap = new HashMap<>();
        List<CategoryWithItemDto> mainCategories = new ArrayList<>();

        for (Category cat : categories) {
            if (cat.getParent() == null) {
                CategoryWithItemDto dto = CategoryWithItemDto.builder()
                        .id(cat.getId())
                        .code(cat.getCode())
                        .items(new ArrayList<>())
                        .build();
                mainCategoryMap.put(cat.getId(), dto);
                mainCategories.add(dto);
            }
        }

        for (Item item : items) {
            String catId = item.getCategory().getId();
            CategoryWithItemDto dto = mainCategoryMap.get(catId);
            if (dto != null) {
                dto.getItems().add(ItemDto.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .build());
            }
        }

        return mainCategories;
    }


    @Override
    public List<CategoryDto> getCategoryTree() {
        List<Category> flatList = categoryRepository.findAll();
        Map<String, CategoryDto> map = new HashMap<>();
        List<CategoryDto> tree = new ArrayList<>();

        for (Category cat : flatList) {
            CategoryDto dto = CategoryDto.builder()
                    .id(cat.getId())
                    .code(cat.getCode())
                    .parentId(cat.getParent() != null ? cat.getParent().getId() : null)
                    .children(new ArrayList<>())
                    .build();
            map.put(cat.getId(), dto);
        }

        for (Category cat : flatList) {
            CategoryDto node = map.get(cat.getId());
            if (cat.getParent() != null) {
                CategoryDto parent = map.get(cat.getParent().getId());
                if (parent != null) {
                    parent.getChildren().add(node);
                }
            } else {
                tree.add(node);
            }
        }

        return tree;
    }

    @Override
    public List<CategoryTranslationDto> getCategoryTranslation(String categoryId) {
        List<CategoryTranslation> translations = categoryRepository.findById(categoryId)
                .map(Category::getTranslations)
                .orElse(Collections.emptyList());

        return translations.stream().map(t -> {
            CategoryTranslationDto dto = new CategoryTranslationDto();
            dto.setId(t.getId());
            dto.setCategoryId(t.getCategory().getId());
            dto.setLanguageCode(t.getLanguageCode());
            dto.setName(t.getName());
            dto.setDescription(t.getDescription());
            return dto;
        }).collect(Collectors.toList());
    }
}

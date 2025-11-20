package app.ruzi.service.app.category;

import app.ruzi.entity.app.Category;
import app.ruzi.entity.app.Item;
import app.ruzi.repository.app.CategoryRepository;
import app.ruzi.repository.app.ItemRepository;
import app.ruzi.service.payload.app.CategoryDto;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CategoryService implements CategoryServiceImplement {
    private final CategoryRepository categoryRepository;
    private final ItemRepository itemRepository;

    @Override
    public DataTablesOutput<Category> getCategories(DataTablesInput dataTablesInput) {
        Specification<Category> specification = (root, query, criteriaBuilder) -> {
            root.join("items", JoinType.LEFT);
            return null;
        };
        return categoryRepository.findAll(dataTablesInput, specification);
    }

    @Override
    public List<Category> getCategoryList() {
        return categoryRepository.findAllByIsDeleted(false);
    }

    @Override
    @Transactional
    public void create(CategoryDto dto) {

        Category category = new Category();
        category.setCode(dto.getCode());
        category.setPrimaryImageUrl(dto.getPrimaryImageUrl());
        categoryRepository.saveAndFlush(category);

        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            itemRepository.assignCategoryToItems(category, dto.getItems());
        }
    }

    @Override
    @Transactional
    public void update(CategoryDto dto) {
        Category category = categoryRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getId()));

        category.setCode(dto.getCode());
        category.setPrimaryImageUrl(dto.getPrimaryImageUrl());
        categoryRepository.saveAndFlush(category);

        // Avval eski bog‘lanmalarni tozalaymiz
        itemRepository.unassignCategoryFromItems(dto.getId());

        // Yangi itemlarni bog‘laymiz
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            itemRepository.assignCategoryToItems(category, dto.getItems());
        }
    }

    @Override
    @Transactional
    public void delete(List<String> idList) {

        // 1) Hamma kategoriyalarni bir marta chaqirib olamiz
        List<Category> categories = categoryRepository.findAllById(idList);

        if (categories.size() != idList.size()) {
            throw new RuntimeException("Some categories not found");
        }

        // 2) Kategoriyalarni 2 guruhga ajratamiz
        List<String> categoriesWithoutItems = new ArrayList<>();
        List<String> categoriesWithItems = new ArrayList<>();

        for (Category c : categories) {
            if (c.getItems() == null || c.getItems().isEmpty()) {
                categoriesWithoutItems.add(c.getId());
            } else {
                categoriesWithItems.add(c.getId());
            }
        }

        // 3) Item yo‘q bo‘lgan kategoriyalarni to‘liq o‘chirib yuboramiz
        if (!categoriesWithoutItems.isEmpty()) {
            categoryRepository.deleteAllByIdList(categoriesWithoutItems);
        }

        // 4) Itemga ega bo‘lgan kategoriyalarni soft-delete qilamiz
        if (!categoriesWithItems.isEmpty()) {

            // SOFT DELETE
            categoryRepository.softDelete(categoriesWithItems);

            // 5) Ular ostidagi itemlarni 1 ta SQL bilan category = null qilamiz
            itemRepository.clearCategoryFromItems(categoriesWithItems);
        }
    }

}

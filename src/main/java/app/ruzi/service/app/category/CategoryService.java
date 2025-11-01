package app.ruzi.service.app.category;

import app.ruzi.entity.app.Category;
import app.ruzi.repository.app.CategoryRepository;
import app.ruzi.repository.app.ItemRepository;
import app.ruzi.service.payload.app.CategoryDto;
import jakarta.persistence.criteria.JoinType;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

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


}

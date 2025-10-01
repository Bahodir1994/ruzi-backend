package app.ruzi.service.app.category;

import app.ruzi.entity.app.Category;
import app.ruzi.repository.app.CategoryRepository;
import jakarta.persistence.criteria.JoinType;
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
        return categoryRepository.findAll();
    }

}

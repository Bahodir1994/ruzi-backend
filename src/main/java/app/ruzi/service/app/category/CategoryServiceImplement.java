package app.ruzi.service.app.category;

import app.ruzi.entity.app.Category;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;

import java.util.List;

public interface CategoryServiceImplement {

    DataTablesOutput<Category> getCategories(DataTablesInput dataTablesInput);

    List<Category> getCategoryList();

}

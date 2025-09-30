package app.ruzi.repository.app;

import app.ruzi.entity.app.Category;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String>, DataTablesRepository<Category, String> {

}
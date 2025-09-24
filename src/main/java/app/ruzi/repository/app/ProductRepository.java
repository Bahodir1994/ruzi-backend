package app.ruzi.repository.app;

import app.ruzi.entity.app.Product;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, DataTablesRepository<Product, Long> {

    @Modifying
    @Query("delete from Product as p where p.id in :idList")
    void deleteAllByIdList(@Param("idList") List<String> idList);

}
package app.ruzi.repository.app;

import app.ruzi.entity.app.Category;
import app.ruzi.entity.app.Item;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String>, DataTablesRepository<Item, String> {

    @Modifying
    @Query("delete from Item as p where p.id in (:idList)")
    void deleteAllByIdList(@Param("idList") List<String> idList);

    @Modifying
    @Query("update Item as itm set itm.isDeleted = true where itm.id in (:ids)")
    void softDelete(@Param("ids") List<String> ids);

    @Modifying
    @Query("update Item i set i.category = :category where i.id in :ids")
    void assignCategoryToItems(@Param("category") Category category, @Param("ids") List<String> ids);

    @Modifying
    @Query("update Item i set i.category = null where i.category.id = :categoryId")
    void unassignCategoryFromItems(@Param("categoryId") String categoryId);

    /**/
    @Query("select max(cast(i.code as integer)) from Item i where i.client.id = :clientId")
    Integer findMaxCodeByClient(@Param("clientId") String clientId);

    @Query("select max(i.internalSkuNumber) from Item i where i.client.id = :clientId")
    Integer findMaxSkuByClient(@Param("clientId") String clientId);

    boolean existsByBarcode(String barcode);
}
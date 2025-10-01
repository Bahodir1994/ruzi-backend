package app.ruzi.repository.app;

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
    @Query("delete from Item as p where p.id in :idList")
    void deleteAllByIdList(@Param("idList") List<String> idList);

}
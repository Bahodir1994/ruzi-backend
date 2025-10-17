package app.ruzi.repository.app;

import app.ruzi.entity.app.CartSession;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartSessionRepository extends JpaRepository<CartSession, String>, DataTablesRepository<CartSession, String> {

    @EntityGraph(attributePaths = {
            "customer", "referrer",
    })
    Optional<CartSession> findFirstByInsUserAndStatusOrderByInsTimeDesc(String insUser, CartSession.Status status);

    @Query("select c.cartNumber from CartSession c where c.cartNumber like :prefix order by c.cartNumber desc limit 1")
    String findLastCartNumberForDate(@Param("prefix") String prefix);

    @EntityGraph(attributePaths = {
            "items",
    })
    Optional<CartSession> findById(String id);

}
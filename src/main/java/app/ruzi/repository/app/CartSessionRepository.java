package app.ruzi.repository.app;

import app.ruzi.entity.app.CartSession;
import app.ruzi.service.payload.app.CartSessionStats;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface CartSessionRepository extends JpaRepository<CartSession, String>, DataTablesRepository<CartSession, String> {

    @EntityGraph(attributePaths = {"customer", "referrer"})
    Optional<CartSession> findFirstByInsUserAndStatusOrderByInsTimeDesc(String insUser, CartSession.Status status);

    @Query("select c.cartNumber from CartSession c where c.cartNumber like :prefix order by c.cartNumber desc limit 1")
    String findLastCartNumberForDate(@Param("prefix") String prefix);

    @EntityGraph(attributePaths = {"items"})
    Optional<CartSession> findById(String id);

    @Query("select " +
            "coalesce(sum(c.totalAmount), 0) as totalAmount, " +
            "coalesce(sum(c.paidAmount), 0) as paidAmount, " +
            "coalesce(sum(c.debtAmount), 0) as debtAmount " +
            "from CartSession c where c.isDeleted = false and c.createdAt >= :startDate")
    CartSessionStats getCartStatistics(LocalDateTime startDate);


}
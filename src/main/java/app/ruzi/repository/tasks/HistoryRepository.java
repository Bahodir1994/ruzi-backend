package app.ruzi.repository.tasks;

import app.ruzi.entity.tasks.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, String> {
    List<History> findByParentIdOrderByInsTimeDesc(String parentId);
}

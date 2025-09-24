package app.ruzi.repository.tasks;

import app.ruzi.entity.tasks.SqlLogics;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SqlLogicsRepository extends JpaRepository<SqlLogics, String> {
    List<SqlLogics> findByGroupName(String var1);
}

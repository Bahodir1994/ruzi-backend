package app.ruzi.repository.tasks;

import app.ruzi.entity.tasks.Messages;
import org.springframework.data.jpa.datatables.repository.DataTablesRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessagesRepository extends JpaRepository<Messages, String>, DataTablesRepository<Messages, String> {

}
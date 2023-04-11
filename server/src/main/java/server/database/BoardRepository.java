package server.database;

import commons.Board;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query(value = "SELECT * FROM BOARD WHERE CODE = ?1 LIMIT 1",
            nativeQuery = true)
    Optional<Board> findByCode(String code);
}

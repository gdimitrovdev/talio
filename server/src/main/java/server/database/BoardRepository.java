package server.database;

import commons.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    @Query(value = "SELECT * FROM BOARD WHERE HASH = ?1 LIMIT 1", nativeQuery = true)
    Optional<Board> findByCode(String hash);
}

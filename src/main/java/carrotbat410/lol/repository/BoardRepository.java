package carrotbat410.lol.repository;

import carrotbat410.lol.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
//    @EntityGraph(attributePaths = "user")
    @Query(value = "select b from Board b where b.user.id = :userId",
        countQuery = "select count(b) from Board b where b.user.id = :userId")
    Page<Board> findByUserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    @Query(value = "select b from Board b join fetch b.user",
            countQuery = "select count(b) from Board b")
    Page<Board> findAll(Pageable pageable);
}

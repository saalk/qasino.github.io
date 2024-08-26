package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.CardMove;
import cloud.qasino.games.database.entity.Playing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayingRepository extends JpaRepository<Playing, Long> {

    // finds
    @Query(value = "SELECT * FROM PLAYING WHERE GAME_ID = :gameId ", nativeQuery = true)
    List<Playing> findByGameId(Long gameId);

}

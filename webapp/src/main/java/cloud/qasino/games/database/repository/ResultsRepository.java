package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultsRepository extends JpaRepository<Result, Long> {

    // @formatter:off

    // BASIC FINDS
    @Query(value = "SELECT * FROM RESULT ORDER BY RESULT_ID", countQuery = "SELECT count(*) FROM RESULT", nativeQuery = true)
    Page<Result> findAllResultsWithPage(Pageable pageable);
    List<Result> findByGame(Game game);
    @Query(value = "SELECT * FROM RESULT where game_id = :gameId ", nativeQuery = true)
    List<Result> findByGameId(Long gameId);

    // SPECIAL FINDS
    public final static String FIND_ACTIVE_RESULT_BY_LEAGUE_ID  =        
            "SELECT r.*      FROM RESULT as r JOIN LEAGUE as l JOIN GAME as g WHERE r.GAME_ID = g.GAME_ID AND l.LEAGUE_ID = g.LEAGUE_ID AND l.LEAGUE_ID = :leagueId ";
    public final static String COUNT_ACTIVE_RESULT_BY_LEAGUE_ID = 
            "SELECT count(*) FROM RESULT as r JOIN LEAGUE as l JOIN GAME as g WHERE r.GAME_ID = g.GAME_ID AND l.LEAGUE_ID = g.LEAGUE_ID AND l.LEAGUE_ID = :leagueId ";
    @Query(value = FIND_ACTIVE_RESULT_BY_LEAGUE_ID, countQuery = COUNT_ACTIVE_RESULT_BY_LEAGUE_ID, nativeQuery = true)
    public List<Result> findAllResultForLeagueWithPage(@Param("leagueId") long leagueId, Pageable pageable);

//    public List<Result> findAllByGame(Game game);
}

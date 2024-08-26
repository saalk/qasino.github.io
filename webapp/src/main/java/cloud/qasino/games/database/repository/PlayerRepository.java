package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    // @formatter:off

    // BASIC FINDS
    @Query(value = "SELECT * FROM PLAYER ORDER BY PLAYER_ID", countQuery = "SELECT count(*) FROM PLAYER", nativeQuery = true)
    Page<Player> findAllPlayersWithPage(Pageable pageable);

    List<Player> findByGame(Game game);

    // SPECIAL FINDS
    String  FIND_PLAYERS_INVITED_FOR_A_GAME = "SELECT      b.* FROM GAME as a JOIN PLAYER as b WHERE a.GAME_ID = b.GAME_ID AND b.ROLE IN ('INVITED','ACCEPTED','REJECTED') AND a.GAME_ID = :GameId ";
    String COUNT_PLAYERS_INVITED_FOR_A_GAME = "SELECT count(*) FROM GAME as a JOIN PLAYER as b WHERE a.GAME_ID = b.GAME_ID AND a.ROLE IN ('INVITED','ACCEPTED','REJECTED') AND a.GAME_ID = :GameId ";
    @Query(value = FIND_PLAYERS_INVITED_FOR_A_GAME, countQuery = COUNT_PLAYERS_INVITED_FOR_A_GAME, nativeQuery = true)
    public List<Player> findAllPlayersInvitedForAGame(@Param("GameId") long gameId, Pageable pageable);

    String FIND_PLAYERS_FOR_A_GAME_ORDER_BY_SEAT = "SELECT * FROM PLAYER p WHERE p.GAME_ID = :GAME_ID ORDER BY seat ASC";
    List<Player> findByGameOrderBySeatAsc(Game game);
    String FIND_PLAYERS_FOR_A_GAME_ORDER_BY_CREATED = "SELECT * FROM PLAYER p WHERE p.GAME_ID = :GAME_ID ORDER BY created ASC";
    List<Player> findByGameOrderByCreatedDesc(Game game);

    // SPECIAL COUNTS
    String COUNT_ALL_PLAYERS_FOR_A_GAME = "SELECT count(*) FROM GAME a WHERE a.GAME_ID = :GAME_ID ";
    int countByGame(Game game);
    String COUNT_ALL_AILEVEL_PLAYERS_FOR_A_GAME = "SELECT count(*) FROM GAME a WHERE a.GAME_ID = :GAME_ID ";

    String COUNT_AILEVEL = "SELECT count(*) FROM PLAYER as p WHERE p.IS_HUMAN = :human AND p.AI_LEVEL = :aiLevel ";
    @Query(value = COUNT_AILEVEL, nativeQuery = true)
    Integer countByAiLevel(@Param(value = "human") String human, @Param(value = "aiLevel") String aiLevel);

    String COUNT_AILEVEL_FOR_INITIATOR = "SELECT count(*) FROM PLAYER as p JOIN GAME as g  WHERE p.GAME_ID = g.GAME_ID AND p.IS_HUMAN = :human AND p.AI_LEVEL = :aiLevel " +
            "AND g.initiator = :initiator";
    @Query(value = COUNT_AILEVEL_FOR_INITIATOR, nativeQuery = true)
    Integer countByAiLevelForInitiator(@Param(value = "human") String human, @Param(value = "aiLevel") String aiLevel, @Param(value = "initiator") String initiator);


    String COUNT_BOTS_FOR_A_GAME = "SELECT count(*) FROM PLAYER as p WHERE p.IS_HUMAN = false WHERE a.GAME_ID = b.GAME_ID ";
    @Query(value = COUNT_BOTS_FOR_A_GAME, nativeQuery = true)
    Integer countBotsForAGame(@Param("GameId") long GameId);

}

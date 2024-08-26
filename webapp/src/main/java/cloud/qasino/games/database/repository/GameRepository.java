package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.security.Visitor;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    // @formatter:off
    @Transactional
    @Query(value = "update GAME as g " +
            "set g.ANTE    = :ante, " +
            "    g.STYLE   = :style, " +
            "    g.STATE   = :state " +
            "where g.GAME_ID = :game_id "
            , nativeQuery = true)
    void update(@Param("ante") int ante, @Param("style") String style, @Param("state") String state, @Param("game_id") long game_id);

    // counts
    Integer countByLeague(League league);
    @Query(value = COUNT_STATES, nativeQuery = true)
    Integer countByStates(@Param(value = "states") String[] states);
    @Query(value = COUNT_STATES_FOR_INITIATOR, nativeQuery = true)
    Integer countByStatesForInitiator(@Param(value = "states") String[] states, @Param(value = "initiator") long initiator);
    @Query(value = COUNT_TODAY, nativeQuery = true)
    Integer countByToday(String jaar, String maand, String weekday);
    @Query(value = COUNT_WEEK, nativeQuery = true)
    Integer countByThisWeek(String jaar, String week);
    @Query(value = COUNT_MAAND, nativeQuery = true)
    Integer countByThisMonth(String jaar, String maand);

    // special finds
    List<Game> findGamesByLeague(League league);
    @Query(value = FIND_ALL_BY_INITIATOR_ID, countQuery = COUNT_ALL_BY_INITIATOR_ID, nativeQuery = true)
    public List<Game> findAllGamesForInitiatorWithPage(
            @Param("initiator") long initiator,
            Pageable pageable);
    List<Game> findByInitiator(long initiator);

    // list with paging
    @Query(value = FIND_ALL, countQuery = COUNT_ALL, nativeQuery = true)
    List<Game> findAllGamesWithPage(Pageable pageable);
    @Query(value = FIND_STATES, countQuery = COUNT_STATES,nativeQuery = true)
    List<Game> findGameNodeStates(
            @Param(value = "states") List<GameState> states,
            Pageable pageable);
    @Query(value = FIND_NEWGAMES_BY_VISITOR_ID, countQuery = COUNT_NEWGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllNewGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);
    @Query(value = FIND_STARTEDGAMES_BY_VISITOR_ID, countQuery = COUNT_STARTEDGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllStartedGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);
    @Query(value = FIND_FINISHEDGAMES_BY_VISITOR_ID, countQuery = COUNT_FINISHEDGAMES_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllFinishedGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);
    @Query(value = FIND_ALL_INVITED_FOR_VISITOR_ID, countQuery = COUNT_ALL_INVITED_FOR_VISITOR_ID, nativeQuery = true)
    public List<Player> findAllInvitedPlayersForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);
    // TODO - FIX ME
    @Query(value = FIND_ALL_INITIATED_BY_VISITOR_ID, countQuery = COUNT_ALL_INITIATED_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllInitiatedGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);
    @Query(value = FIND_ALL_INVITED_BY_VISITOR_ID, countQuery = COUNT_ALL_INVITED_BY_VISITOR_ID, nativeQuery = true)
    public List<Game> findAllInvitedGamesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);

    // prepared queries
    String FIND_ALL =     "SELECT *        FROM GAME ORDER BY UPDATED DESC";
    String COUNT_ALL =    "SELECT count(*) FROM GAME";
    String FIND_STATES =  "SELECT *        FROM GAME as g WHERE g.STATE IN :states";
    String COUNT_STATES = "SELECT count(*) FROM GAME as g WHERE g.STATE IN (:states)";
    String COUNT_STATES_FOR_INITIATOR = "SELECT count(*) FROM GAME as g WHERE g.STATE IN (:states) AND g.INITIATOR = :initiator ";
    String COUNT_TODAY =  "SELECT count(*) FROM GAME as g WHERE g.JAAR = :jaar AND g.MAAND = :maand AND g.WEEKDAY = :weekday ";
    String COUNT_WEEK =   "SELECT count(*) FROM GAME as g WHERE g.JAAR = :jaar AND g.WEEK = :week ";
    String COUNT_MAAND =  "SELECT count(*) FROM GAME as g WHERE g.JAAR = :jaar AND g.MAAND = :maand ";

    String FIND_ALL_BY_INITIATOR_ID       = "SELECT *        FROM GAME as a WHERE a.INITIATOR = :initiator ORDER BY a.UPDATED DESC ";
    String COUNT_ALL_BY_INITIATOR_ID      = "SELECT count(*) FROM GAME as a WHERE a.INITIATOR = :initiator ";
    String FIND_ALL_INVITED_BY_VISITOR_ID =
            "SELECT a.*      FROM GAME as a " +
                    "JOIN PLAYER as b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.INITIATOR != :visitorId " +
                    "ORDER BY a.UPDATED DESC ";
    String COUNT_ALL_INVITED_BY_VISITOR_ID =
            "SELECT count(a.*) FROM GAME as a " +
                    "JOIN PLAYER as b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.INITIATOR != :visitorId ";

    String FIND_ALL_INITIATED_BY_VISITOR_ID =
            "SELECT * FROM GAME WHERE INITIATOR = :visitorId ORDER BY UPDATED desc ";
    String COUNT_ALL_INITIATED_BY_VISITOR_ID =
            "SELECT count(*) FROM GAME WHERE INITIATOR = :visitorId ";


    String FIND_ALL_INVITED_FOR_VISITOR_ID =
            "SELECT b.* FROM GAME as a JOIN PLAYER as b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND b.PLAYERTYPE IN ('INVITED','INVITEE','REJECTED') " +
                    "ORDER BY a.updated DESC ";
    String COUNT_ALL_INVITED_FOR_VISITOR_ID =
            "SELECT count(b.*) FROM GAME as a JOIN PLAYER as b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND b.PLAYERTYPE IN ('INVITED','INVITEE','REJECTED') ";


    String FIND_NEWGAMES_BY_VISITOR_ID =
            "SELECT a.* FROM GAME as a JOIN PLAYER b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('INITIALIZED','PENDING_INVITATIONS','PREPARED') " +
                    "ORDER BY a.UPDATED DESC ";
    String COUNT_NEWGAMES_BY_VISITOR_ID =
            "SELECT count(a.*) FROM GAME as a JOIN PLAYER b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('INITIALIZED','PENDING_INVITATIONS','PREPARED') ";

    String FIND_STARTEDGAMES_BY_VISITOR_ID =
            "SELECT a.* FROM GAME as a JOIN PLAYER as b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('STARTED','INITIATOR_MOVE','INVITEE_MOVE','BOT_MOVE') " +
                    "ORDER BY a.updated DESC ";
    String COUNT_STARTEDGAMES_BY_VISITOR_ID =
            "SELECT count(a.*) FROM GAME as a JOIN PLAYER as b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('STARTED','INITIATOR_MOVE','INVITEE_MOVE','BOT_MOVE') ";

    String FIND_FINISHEDGAMES_BY_VISITOR_ID =
            "SELECT a.* FROM GAME as a JOIN player as b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('FINISHED','STOPPED','CANCELLED') " +
                    "ORDER BY a.updated DESC ";
    String COUNT_FINISHEDGAMES_BY_VISITOR_ID =
            "SELECT count(a.*) FROM GAME as a JOIN PLAYER as b " +
                    "WHERE a.GAME_ID = b.GAME_ID " +
                    "AND b.VISITOR_ID = :visitorId " +
                    "AND a.STATE IN ('FINISHED','STOPPED','CANCELLED') ";

    
    default String getYear() {
        return String.valueOf(LocalDate.now().getYear());
    }
    default String getMaand() {
        return String.valueOf(LocalDate.now().getMonth());
    }
    default String getDay() {
        return String.valueOf(LocalDate.now().getDayOfMonth());
    }
    default String getWeek() {
        DateTimeFormatter week = DateTimeFormatter.ofPattern("w");
        return String.valueOf(LocalDate.now().format(week));
    }
}

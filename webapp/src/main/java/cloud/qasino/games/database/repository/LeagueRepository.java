package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.League;
import cloud.qasino.games.database.security.Visitor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LeagueRepository extends JpaRepository<League, Long> {

    // @formatter:off

    // counts
    Long countByName(String leagueName);

    // lifecycle of a league - aim to not used the dto's
    String COUNT_LEAGUES_FOR_INITIATOR = "SELECT count(*) FROM LEAGUE as l WHERE l.VISITOR_ID = :initiator";
    @Query(value = COUNT_LEAGUES_FOR_INITIATOR, nativeQuery = true)
    Integer countLeaguesForInitiator(@Param(value = "initiator") String initiator);

    // lifecycle of a visitor - aim to not used the dto's
    Optional<League> findLeagueByNameAndNameSequence(String leagueName, int leagueNameSequence);
    @Query(value = "SELECT * FROM LEAGUE where GAME_ID = :gameId ", nativeQuery = true)
    League findByGameId(Long gameId);

    public final static String FIND_LEAGUES_FOR_VISITOR_ID =
            "SELECT * FROM LEAGUE a WHERE a.VISITOR_ID = :visitorId " +
                    "AND a.IS_ACTIVE = CAST('true' AS BOOLEAN) ORDER BY CREATED desc ";
    public final static String COUNT_LEAGUES_FOR_VISITOR_ID =
            "SELECT count(*) FROM LEAGUE a WHERE a.VISITOR_ID = :visitorId " +
                    "AND a.IS_ACTIVE = CAST('true' AS BOOLEAN) ";
    @Query(value = FIND_LEAGUES_FOR_VISITOR_ID, countQuery = COUNT_LEAGUES_FOR_VISITOR_ID, nativeQuery = true)
    public List<League> findLeaguesForVisitorWithPage(
            @Param("visitorId") long visitorId,
            Pageable pageable);
    public List<League> findLeaguesByVisitor(
            Visitor visitor);
}

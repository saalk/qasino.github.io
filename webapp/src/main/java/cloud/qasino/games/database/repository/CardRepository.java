package cloud.qasino.games.database.repository;

import cloud.qasino.games.database.entity.Card;
import cloud.qasino.games.database.entity.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    public final static String FIND_CARDS_BY_GAME_ID =
            "SELECT * FROM CARD " +
                    "WHERE GAME_ID = :gameId ";
    public final static String COUNT_CARDS_BY_GAME_ID =
            "SELECT count(*) FROM CARD  " +
                    "WHERE GAME_ID = :gameId ";

    // counters
    int countByGame(Game game);

    String COUNT_CARDS_FOR_INITIATOR = "SELECT count(*) FROM CARD as c JOIN GAME as g WHERE c.GAME_ID = g.GAME_ID " +
            "AND g.INITIATOR = :initiator";
    @Query(value = COUNT_CARDS_FOR_INITIATOR, nativeQuery = true)
    Integer countCardsForInitiator(@Param(value = "initiator") String initiator);


    // lists
    List<Card> findByGame(Game game);
    List<Card> findByRankSuit(String rankSuit);
    @Query( value = FIND_CARDS_BY_GAME_ID,
            countQuery = COUNT_CARDS_BY_GAME_ID,
            nativeQuery = true)
    List<Card> findAllCardsByGameWithPage(@Param("gameId") long gameId, Pageable pageable);

    List<Card> findByGameOrderByLocationAscSequenceAsc(Game game);

    List<Card> findByGameOrderBySequenceAsc(Game game);
    @Query(value = "SELECT * FROM CARD where GAME_ID = :gameId ORDER BY SEQUENCE ASC ", nativeQuery = true)
    List<Card> findByGameIdOrderBySequenceAsc(long gameId);
}

package cloud.qasino.games.database.entity.enums;

import cloud.qasino.games.database.entity.enums.card.PlayingCard;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayingCardTest {

    @Test
    void callCreateDeckWithXJokersShouldBecomeCorrectNumberOfCards() {

        List<PlayingCard> testDeckNoJokers = PlayingCard.createDeckWithXJokers(0);
        List<PlayingCard> testDeckOneJokers = PlayingCard.createDeckWithXJokers(1);
        List<PlayingCard> testDeckTwoJokers = PlayingCard.createDeckWithXJokers(2);

        // assert statements
        assertEquals(52, testDeckNoJokers.size());
        assertEquals(53, testDeckOneJokers.size());
        assertEquals(54, testDeckTwoJokers.size());
        assertEquals(52, PlayingCard.normalCardDeckNoJoker.size());

    }

    @Test
    void callIsValidCardShouldGiveCorrectBoolean() {

        boolean valid = PlayingCard.isValid2LetterCardId("AS");
        boolean inValid = PlayingCard.isValid2LetterCardId("XX");

        // assert statements
        assertTrue(valid);
        assertFalse(inValid);

    }

    @Test
    void callSetCardFromCardIdShouldSetCorrectCard() {

//        PlayingCard valid = new PlayingCard();
//        boolean validCard = valid.setPlayingCardFromCardId("AS");
//
//        PlayingCard inValid = new PlayingCard();
//        boolean inValidCard = valid.setPlayingCardFromCardId("XX");
//
//        // assert statements
//        assertTrue(validCard);
//        assertEquals("AS",valid.getCardId());
//        assertEquals(Rank.ACE,valid.getRank());
//        assertEquals(Suit.SPADES,valid.getSuit());
//        assertEquals(1, valid.getValue());
//
//        assertTrue(!inValidCard);

    }

}
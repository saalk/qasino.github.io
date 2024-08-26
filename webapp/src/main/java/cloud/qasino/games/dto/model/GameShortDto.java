package cloud.qasino.games.dto.model;

import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.game.gamestate.GameStateGroup;
import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import cloud.qasino.games.database.entity.enums.game.style.BettingStrategy;
import cloud.qasino.games.database.entity.enums.game.style.DeckConfiguration;
import cloud.qasino.games.database.entity.enums.game.style.OneTimeInsurance;
import cloud.qasino.games.database.entity.enums.game.style.RoundsToWin;
import cloud.qasino.games.database.entity.enums.game.style.TurnsToWin;
import lombok.Data;

import java.time.Month;

@Data
/*
  The purpose of using this Dto is to separate the internal representation of game data
  (e.g., in the database or business logic) from the data exposed to
  external clients or systems.
  */
public class GameShortDto {

    // core
    private long gameId;
//    private String updated; // ignore

    // ref
//    private LeagueDto league;
    private long initiator;
//    private List<CardDto> cards; // works but not needed
//    private List<PlayerDto> players;

    // Normal fields
    private GameState state;
    private GameState previousState;
    private Type type;
    private String style;
    private int ante;

    private int jaar;
    private Month maand;
    private String week;
    private int weekday;

    // derived
//    private String cardsInStock;
    private GameStateGroup gameStateGroup;
    private boolean isActivePlayerInitiator;

    private AnteToWin anteToWin;
    private BettingStrategy bettingStrategy;
    private DeckConfiguration deckConfiguration;
    private OneTimeInsurance oneTimeInsurance;
    private RoundsToWin roundsToWin;
    private TurnsToWin turnsToWin;
}



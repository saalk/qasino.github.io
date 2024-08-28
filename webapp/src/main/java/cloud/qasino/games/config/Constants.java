package cloud.qasino.games.config;

import cloud.qasino.games.database.entity.enums.game.style.*;
import cloud.qasino.games.database.entity.enums.game.GameState;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.pattern.statemachine.event.GameEvent;
import cloud.qasino.games.database.entity.enums.move.Move;
import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import cloud.qasino.games.database.entity.enums.card.playingcard.Rank;
import cloud.qasino.games.database.entity.enums.card.playingcard.Suit;
//import org.apache.commons.lang3.BooleanUtils;
//import org.apache.commons.lang3.EnumUtils;
//import org.apache.commons.lang3.StringUtils;

import java.util.List;

public interface Constants {

    public static final String BASE_PATH = "/qasino/api";
    public static final String ENDPOINT_VISITOR = "/visitor";
    public static final String ENDPOINT_GAME = "/game";
    public static final String ENDPOINT_PLAYER = "/player";
    public static final String ENDPOINT_CARD = "/card";
    public static final String ENDPOINT_PLAY = "/play";

    public static final String NUMERIC_REGEX = "^([\\p{N}]+)$";
    public static final String ALPHANUMERIC_REGEX = "^([\\p{N}\\p{L}]+)$";
    public static final String UUID_REGEX = "^([\\p{N}\\p{L}\\-]+)$";

    // TODO move to properties
    int DEFAULT_PAWN_SHIP_HUMAN = 1000;
    int DEFAULT_PAWN_SHIP_BOT = 1000;

    // helper for checking header, path and request params
    static boolean isNullOrEmpty(int value) {
        // str.isEmpty will throw a nullpointer if e == null
        return value <= 0;
    }

    static boolean isNullOrEmpty(String str) {
        // str.isEmpty will throw a nullpointer if e == null
        return str == null || str.trim().isEmpty();
    }


    static boolean isNullOrEmpty(Integer integer) {
        // str.isEmpty will throw a nullpointer if e == null
        return integer == null || integer <= 0;
    }

    static boolean isNullOrEmpty(Boolean bool) {
        // str.isEmpty will throw a nullpointer if e == null
        return bool == null;
    }

    static boolean isNullOrEmpty(List<?> e) {
        // e.isEmpty will throw a nullpointer if e == null
        return e == null || e.isEmpty();
    }

    static boolean isNullOrEmpty(Object o) {
        // e.isEmpty will throw a nullpointer if e == null
        return o == null;
    }

}

package cloud.qasino.games.dto.model;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import lombok.Data;

import java.util.List;

@Data
public class SeatDto {

    // core
    private long playerId;
//    private String created; // ignore

    // ref

    // Normal fields
    private int seatId;
    private boolean human;
    private PlayerType playerType;
    private int fiches;
    private Avatar avatar;
    private String avatarName;
    private AiLevel aiLevel;

    private boolean winner;

    // ref
    private List<HandDto> hands;

    // derived
    private boolean isSeatPlaying;
    private int seatCurrentBet;

    private String cardsInHand;
    private String lastCardInHand;

    private boolean isSeatPlayerTheInitiator;
    private long visitorId;
    private String username;
    private int seatStartBalance;

}


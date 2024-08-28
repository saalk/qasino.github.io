package cloud.qasino.games.database.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// @Entity creates a direct link between class object(s) and table row(s)
@Entity
// @DynamicUpdate includes only columns which are actually being updated - not the cached insert
@DynamicUpdate
// @Data for JPA entities is an antipattern
// But we override equals, hash and toString and have noargs constructor.
@Data
//@JsonIdentityInfo(generator = JSOGGenerator.class, property = "turnId")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "PLAYING"
//        , indexes =
//        {@Index(name = "playing_game_index", columnList = "game_id", unique = false)
//          not needed : @Index(name = "turns_index", columnList = "playing_id", unique = true)
//        }
)
public class Playing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLAYING_ID")
    private long playingId;
    @JsonIgnore
    @Column(name = "CREATED", length = 25)
    private String updated;

    // Foreign keys
    // one [Playing] can be part of one [Game]
    @OneToOne
    @JoinColumn(name = "GAME_ID", referencedColumnName = "GAME_ID", foreignKey = @ForeignKey(name =
            "FK_GAME_ID"), nullable = false)
    private Game game;

    // one [Player] can be part of one [Playing]
    @OneToOne
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "PLAYER_ID", foreignKey = @ForeignKey(name =
            "FK_PLAYER_ID"), nullable = false)
    private Player player;

    // Functional fields
    @Column(name = "CURRENT_ROUND_NUMBER", nullable = true)
    private int currentRoundNumber;
    @Column(name = "CURRENT_SEAT_NUMBER", nullable = true)
    private int currentSeatNumber;
    @Column(name = "CURRENT_MOVE_NUMBER", nullable = true)
    @Setter(AccessLevel.NONE)
    private int currentMoveNumber;

    public void setCurrentMoveNumber(int currentMoveNumber) {
        this.currentMoveNumber = currentMoveNumber;
        setUpdated();
    }

    // Derived technical fields
    @Setter(AccessLevel.NONE)
    @Column(name = "JAAR", length = 4)
    private int jaar;
    @Setter(AccessLevel.NONE)
    @Column(name = "MAAND", length = 20)
    private Month maand;
    @Setter(AccessLevel.NONE)
    @Column(name = "WEEK", length = 3)
    private String week;
    @Setter(AccessLevel.NONE)
    @Column(name = "WEEKDAY", length = 2)
    private int weekday;

    // References - the actual FK are in other tables
    // one [Playing] is parent for many [CardMoves]
    @OneToMany(mappedBy = "playing")
    private List<CardMove> cardMoves = new ArrayList<>();

    public Playing() {
        setUpdated();
    }

    public Playing(Game game, Player player) {
        this();
        this.game = game;
        this.player = player;

        this.currentRoundNumber = 1;
        this.currentSeatNumber = 1;
        this.currentMoveNumber = 1;

    }

    public void setUpdated() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.updated = result.substring(0, 20);

        this.jaar = localDateAndTime.getYear();
        this.maand = localDateAndTime.getMonth();
        DateTimeFormatter week = DateTimeFormatter.ofPattern("w");
        this.week = localDateAndTime.format(week);
        this.weekday = localDateAndTime.getDayOfMonth();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playing playing = (Playing) o;
        return playingId == playing.playingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playingId);
    }

    @Override
    public String toString() {
        return "(" +
                "playingId=" + this.playingId +
                ", playerId=" + (this.player.getPlayerId()) +
                ", player avatarName=" + (this.player.getAvatarName()) +
                ", currentRoundNumber=" + this.currentRoundNumber +
                ", currentSeatNumber=" + this.currentSeatNumber +
                ", currentMoveNumber=" + this.currentMoveNumber +
                ", cardMoves count=" + this.cardMoves.size() +
                ", gameId=" + (this.game == null ? "" : this.game.getGameId()) +
                ", game state=" + (this.game == null ? "" : this.game.getState()) +
                ", weekday=" + this.weekday +
                ")";
    }
}

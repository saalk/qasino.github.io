package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.database.security.Visitor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
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
//@JsonIdentityInfo(generator = JSOGGenerator.class, property = "playerId")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "PLAYER", indexes = {
        // not needed : @Index(name = "players_index", columnList = "player_id", unique = true)
})
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLAYER_ID", nullable = false)
    private long playerId;

    @Column(name = "CREATED", length = 25)
    private String created;

    // Foreign keys
    // UsPl: a Visitor can play many Games as a Player
    // However AI players are no visitors!
    @ManyToOne
    @JoinColumn(name = "VISITOR_ID", referencedColumnName = "VISITOR_ID", foreignKey = @ForeignKey
            (name = "FK_VISITOR_ID"), nullable = true)
    private Visitor visitor;

    // PlGa: many Players can play the same Game
    @ManyToOne
    @JoinColumn(name = "GAME_ID", referencedColumnName = "GAME_ID", foreignKey = @ForeignKey
            (name = "FK_GAME_ID"), nullable = true)
    private Game game;

    // Normal fields

    // current sequence of the player in the game, zero is a DECLINED VISITOR
    @Column(name = "SEAT")
    private int seat;

    @Setter(AccessLevel.NONE)
    @Column(name = "IS_HUMAN")
    private boolean human;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROLE", nullable = false, length = 20)
    private PlayerType playerType;

    @Column(name = "startfiches")
    private int startFiches;

    @Column(name = "FICHES")
    private int fiches;

    @Enumerated(EnumType.STRING)
    @Column(name = "AVATAR", nullable = true, length = 50)
    private Avatar avatar;

    @Column(name = "AVATAR_NAME", nullable = true, length = 50)
    private String avatarName;

    @Enumerated(EnumType.STRING)
    @Column(name = "AI_LEVEL", nullable = true, length = 50)
    private AiLevel aiLevel;

    @Setter(AccessLevel.NONE)
    @Column(name = "IS_WINNER")
    private boolean winner;

    // REFERENCES

    // one [Player] can have one [Playing], holding the current player, round, seat and move
    @OneToOne(mappedBy = "player", cascade = CascadeType.DETACH)
    private Playing playing;

    // GaWi: one [Player] is the Winner of the GameSubTotals in the end
    @OneToOne(mappedBy = "player", cascade = CascadeType.DETACH)
    // just a reference the fk column is in "game" not here!
    private Result result;

    // HO: A [Player] holds one or more [Card]s after playing
    @OneToMany(mappedBy = "hand", cascade = CascadeType.DETACH)
    // just a reference, the actual fk column is in "game" not here !
    private List<Card> cards = new ArrayList<>();

    public Player() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);
        this.human = true;
        this.winner = false;
        this.seat = 1;
    }

    public Player(Visitor visitor, Game game, PlayerType playerType, int startFiches, int fiches, int seat, Avatar avatar, String avatarName, AiLevel aiLevel) {
        this();
        this.visitor = visitor;
        this.game = game;
        this.playerType = playerType;

        this.startFiches = startFiches;
        this.fiches = fiches;
        this.seat = seat;

        this.avatar = avatar;
        this.avatarName = avatarName;
        this.aiLevel = aiLevel;

        this.human = aiLevel == AiLevel.HUMAN;
    }

    public static Player buildDummyBot(Game game, Avatar avatar, AiLevel aiLevel) {
        if (avatar == null) avatar = Avatar.GOBLIN;
        if (aiLevel == null) aiLevel = AiLevel.AVERAGE;
        return new Player(null, game, PlayerType.BOT, 99, 99, 99, avatar, "avatarName", aiLevel);
    }

    public static Player buildDummyHuman(Visitor visitor, Game game, Avatar avatar) {
        if (avatar == null) avatar = Avatar.GOBLIN;
        return new Player(visitor, game, PlayerType.INITIATOR, 99, 99, 99, avatar, "avatarName", AiLevel.HUMAN);
    }

    public static Player buildDummyInvitee(Visitor visitor, Game game, Avatar avatar) {
        if (avatar == null) avatar = Avatar.GOBLIN;
        return new Player(visitor, game, PlayerType.INVITED, 99, 99, 99, avatar, "avatarName", AiLevel.HUMAN);
    }

    public void setAiLevel(AiLevel aiLevel) {
        this.aiLevel = aiLevel;
        this.human = aiLevel == AiLevel.HUMAN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return playerId == player.playerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId);
    }

    @Override
    public String toString() {
        return "(" +
                "playerId=" + this.playerId
                + ", visitorId=" + (this.visitor == null ? "" : this.visitor.getVisitorId())
                + ", gameId=" + (this.game == null ? "" : this.game.getGameId())
                + ", human=" + this.human
                + ", role=" + (this.playerType == null ? "" : this.playerType.getLabel())
                + ", fiches=" + this.fiches
                + ", seat=" + this.seat
                + ", avatar=" + this.avatar
                + ", aiLevel=" + this.aiLevel
                + ", winner=" + this.winner
                + ", resultId=" + (this.result == null ? "" : this.result.getResultId())
                + ")";
    }
}


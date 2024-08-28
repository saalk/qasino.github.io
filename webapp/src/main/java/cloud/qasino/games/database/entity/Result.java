package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.game.Type;
import cloud.qasino.games.database.security.Visitor;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// @Entity creates a direct link between class object(s) and table row(s)
@Entity
// @DynamicUpdate includes only columns which are actually being updated - not the cached insert
@DynamicUpdate
// @Data for JPA entities is an antipattern
// But we override equals, hash and toString and have noargs constructor.
@Data
@JsonIdentityInfo(generator = JSOGGenerator.class, property = "resultId")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "result", indexes = {
        // not needed : @Index(name = "results_index", columnList = "result_id", unique = true)
})
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private long resultId;
    @Column(name = "created", length = 25)
    private String created;

    // Foreign keys
    // one [Result] always belongs to one [Player]
    @OneToOne
    @JoinColumn(name = "player_id", referencedColumnName = "player_id", foreignKey = @ForeignKey
            (name = "fk_player_id"), nullable = false)
    private Player player;
    // many [Result] can belong to one [Visitor]
    // TODO the Initiator can win the Games as a Player - not sure if we want this relation
    @ManyToOne
    @JoinColumn(name = "visitor_id", referencedColumnName = "visitor_id", foreignKey = @ForeignKey
            (name = "fk_visitor_id"), nullable = true)
    private Visitor visitor;
    // many [Result] can belong to one [Game]
    // TODO the game already has a result for a Player - not sure if we want this relation
    @ManyToOne
    @JoinColumn(name = "game_id", referencedColumnName = "game_id", foreignKey = @ForeignKey(name =
            "fk_game_id"), nullable = false)
    private Game game;

    // Normal fields
    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private Type type;
    @Column(name = "JAAR", length = 4)
    private int jaar;
    @Column(name = "maand", length = 20)
    private Month maand;
    @Column(name = "week", length = 3)
    private String week;
    @Column(name = "weekday", length = 2)
    private int weekday;
    @Setter(AccessLevel.NONE)
    @Column(name = "fiches_won")
    private int fichesWon;
    @Column(name = "winner")
    private boolean winner;

    // References - the actual FK are in other tables
    // none

    public Result() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        DateTimeFormatter week = DateTimeFormatter.ofPattern("w");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);
        this.jaar = localDateAndTime.getYear();
        this.maand = localDateAndTime.getMonth();
        this.week = localDateAndTime.format(week);
        this.weekday = localDateAndTime.getDayOfMonth();
    }

    public Result(Player player, Visitor visitor, Game game, Type type, int fichesWon, boolean winner) {
        this();
        this.player = player;
        this.visitor = visitor;
        this.game = game;

        this.type = type;
        this.fichesWon = fichesWon;
        this.winner = winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return resultId == result.resultId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(resultId);
    }

    @Override
    public String toString() {
        return "(" +
                "resultId=" + this.resultId +
                ", visitorId=" + (this.visitor == null ? "" : this.visitor.getVisitorId()) +
                ", playerId=" + (this.player == null ? "" : this.player.getPlayerId()) +
                ", playerSeat=" + (this.player == null ? "" : this.player.getSeat()) +
                ", playerAvatarName=" + (this.player == null ? "" : this.player.getAvatarName()) +
                ", gameId=" + (this.game == null ? "" : this.game.getGameId()) +
                ", type=" + this.type +
                ", weekday=" + this.weekday +
                ", fichesWon=" + this.fichesWon +
                ", winner=" + this.winner +
                ")";
    }
}


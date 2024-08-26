package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.entity.enums.card.Face;
import cloud.qasino.games.database.entity.enums.card.Location;
import cloud.qasino.games.database.entity.enums.card.Position;
import com.fasterxml.jackson.annotation.*;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// @Entity creates a direct link between class object(s) and table row(s)
@Entity
// @DynamicUpdate includes only columns which are actually being updated - not the cached insert
@DynamicUpdate
// @Data for JPA entities is an antipattern
// But we override equals, hash and toString and have noargs constructor.
@Data
//@JsonIdentityInfo(generator = JSOGGenerator.class, property = "cardId")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "CARD", indexes =
        { @Index(name = "CARDS_GAME_INDEX", columnList = "GAME_ID", unique = false ),
          // not needed : @Index(name = "cards_index", columnList = "card_id", unique = true )
        })
public class Card {

    // @formatter:off
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CARD_ID")
    private long cardId;
    @JsonIgnore
    @Column(name = "CREATED", length = 25)
    private String created;
    @Column(name = "RANKSUIT", length = 3, nullable = false)
    private String rankSuit;

    // Foreign keys
    @JsonIgnore
    // many [Card] can be part of one [Game]
    @ManyToOne
    @JoinColumn(name = "GAME_ID", referencedColumnName = "GAME_ID", foreignKey =
    @ForeignKey(name =
            "FK_GAME_ID"), nullable=false)
    private Game game;
    @JsonIgnore
    // one [Card] can be part of one [Player] or none
    @OneToOne
    @JoinColumn(name = "PLAYER_ID", referencedColumnName = "PLAYER_ID", foreignKey =
    @ForeignKey(name ="FK_PLAYER_ID"))
    private Player hand;

    // Normal fields
    @Column(name = "SEQUENCE")
    private int sequence;
    @Enumerated(EnumType.STRING)
    @Column(name = "LOCATION", nullable = false)
    private Location location;
    @Enumerated(EnumType.STRING)
    @Column(name = "POSITION", nullable = false)
    private Position position;
    @Enumerated(EnumType.STRING)
    @Column(name = "FACE", nullable = false)
    private Face face;

    // References - actual FK are in other tables
    // none

    public Card() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);
    }

    public Card(String rankSuit, Game game, Player player, int sequence, Location location) {
        this();
        this.rankSuit = rankSuit;
        this.game = game;
        this.hand = player;
        this.sequence = sequence;
        this.location = location;
        this.position = Position.SHUFFLED;
        this.face = Face.DOWN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return cardId == card.cardId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(cardId);
    }

    @Override
    public String toString () {
        return String.valueOf(this.cardId);
    }

}

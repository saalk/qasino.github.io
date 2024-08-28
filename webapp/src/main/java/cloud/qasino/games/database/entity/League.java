package cloud.qasino.games.database.entity;

import cloud.qasino.games.database.security.Visitor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.List;
import java.util.Objects;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.next;

// @Entity creates a direct link between class object(s) and table row(s)
@Entity
// @DynamicUpdate includes only columns which are actually being updated - not the cached insert
// @DynamicUpdate
// @Data for JPA entities is an antipattern
// But we override equals, hash and toString and have noargs constructor.
@Data
//@JsonIdentityInfo(generator = JSOGGenerator.class, property = "leagueId")
//@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Table(name = "LEAGUE", indexes = {
        // not needed : @Index(name = "leagues_index", columnList = "league_id", unique = true)
})
public class League {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LEAGUE_ID", nullable = false)
    private long leagueId;
    @JsonIgnore
    @Column(name = "CREATED", length = 25)
    private String created;

    // Foreign keys
    // many [League] can be part of one [Visitor]
    @ManyToOne // no cascade otherwise league.visitor is set to null !!
    @JoinColumn(name = "VISITOR_ID", referencedColumnName = "VISITOR_ID", foreignKey = @ForeignKey
            (name = "FK_VISITOR_ID"), nullable = false)
    private Visitor visitor;

    // Normal fields
    @Column(name = "NAME", nullable = false, length = 50)
    private String name;
    @Column(name = "NAME_SEQ")
    private int nameSequence;
    @Getter(AccessLevel.NONE)
    @Column(name = "IS_ACTIVE")
    private boolean active;
    @Column(name = "ENDED", length = 25)
    private String ended;

    // References - the actual FK are in other tables
    @JsonIgnore // otherwise the game does not show up - only as a ref in the response
    // one [League] can have many [Game]s
    @OneToMany(mappedBy = "league") // no cascade otherwise league.visitor is set to null !!
    // just a reference, the actual fk column is in game not here !
    private List<Game> games;

    public League() {
        LocalDateTime localDateAndTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.created = result.substring(0, 20);
        this.active = true;
    }

    public League(Visitor visitor, String name, int nameSequence) {
        this();
        this.visitor = visitor;
        this.name = name;
        this.nameSequence = nameSequence;

        endLeagueThisMonth(); //default
    }

    public static League buildDummy(Visitor visitor, String leagueName) {
        if (leagueName.isEmpty()) leagueName = "leagueName";
        return new League(visitor, leagueName, 1);
    }

    public boolean endLeagueDaysFromNow(int days) {
        if (!this.isActive()) return false;
        LocalDateTime localDateAndTime = LocalDateTime.now().plus(Period.ofDays(days));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.ended = result.substring(0, 20);
        return true;
    }

    public boolean endLeagueNextMonday() {
        if (!this.isActive()) return false;
        LocalDateTime localDateAndTime = LocalDateTime.now().with(next(DayOfWeek.MONDAY));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.ended = result.substring(0, 20);
        return true;
    }

    public boolean endLeagueThisMonth() {
        if (!this.isActive()) return false;
        LocalDateTime localDateAndTime = LocalDateTime.now().with(lastDayOfMonth());
        ;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.ended = result.substring(0, 20);
        return true;
    }

    public boolean isActive() {
        if (!this.active) return false; // \"visitor\"can set to inactive before enddate
        if (this.ended == null || this.ended.isEmpty()) return true;

        // check if ended has passed
        LocalDate yesterday = LocalDate.now().plusDays(-1);
        DateTimeFormatterBuilder dateTimeFormatterBuilder = new DateTimeFormatterBuilder()
                .append(DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS"));
        DateTimeFormatter dateTimeFormatter = dateTimeFormatterBuilder.toFormatter();
        LocalDate ended = LocalDate.parse(this.ended, dateTimeFormatter);
        int days = Period.between(yesterday, ended).getDays();
        return days > 0;
    }

    public void closeLeaguePerYesterday() {
        LocalDateTime localDateAndTime = LocalDateTime.now().plusDays(-1); // yesterday;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        this.ended = result.substring(0, 20);
        this.active = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        League league = (League) o;
        return leagueId == league.leagueId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(leagueId);
    }

    @Override
    public String toString() {
        return "(" +
                "leagueId=" + this.leagueId +
                ", visitorId=" + (this.visitor == null ? "" : this.visitor.getVisitorId()) +
                ", name=" + this.name +
                ", nameSequence=" + this.nameSequence +
                ", active=" + this.active +
                ")";
    }
}


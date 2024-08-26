package cloud.qasino.games.database.entity.enums.game.style;

import lombok.Getter;

import jakarta.validation.constraints.Pattern;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
public enum AnteToWin {

// todo determine what to do here : bet vs ante ration?
    NA("n", "No amount wil win directly"),
    TIMES_3_WINS("3", "Triple your ante and win direct"),
    TIMES_4_WINS("4", "4 Times your ante wins direct"),
    TIMES_5_WINS("5", "5 Times your ante wins direct");

    /**
     * A static HashMap lookup with key + value is created to use in a getter
     * to fromLabel the Enum based on the name eg. key "Low" -> value AiLevel.DUMB
     */
    public static final Map<String, AnteToWin> lookup
            = new HashMap<>();
    static {
        for(AnteToWin anteToWin : EnumSet.allOf(AnteToWin.class))
            lookup.put(anteToWin.getLabel(), anteToWin);
    }

    @Pattern(regexp = "[a-z,0-9]")
    String label;
    String description;

    // Constructor, each argument to the constructor shadows one of the object's
    // fields
    AnteToWin(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public static AnteToWin fromName(String name) {
        return AnteToWin.valueOf(name.toUpperCase());
    }

    public static AnteToWin fromLabel(String inputLabel) {
        return lookup.get(inputLabel.toLowerCase());
    }

    public static AnteToWin fromLabel(char character) {
        return fromLabel(Character.toString(character));
    }

    public static AnteToWin fromLabelWithDefault(String label) {
        AnteToWin anteToWin = fromLabel(label);
        if (anteToWin == null) return AnteToWin.NA;
        return anteToWin;
    }

    public static AnteToWin fromLabelWithDefault(char character) {
        return fromLabelWithDefault(Character.toString(character));
    }
    
}

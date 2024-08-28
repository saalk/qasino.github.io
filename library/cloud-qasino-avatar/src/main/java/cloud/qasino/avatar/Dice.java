package cloud.qasino.avatar;

/**
 * Dice objects.
 * <p>
 * Jaison Eccleston
 * 14APR2022
 */

import lombok.Data;

import java.util.Random;

@Data
public class Dice {

    Random gen = new Random();
    // instance variables
    static int faces = 0;
    static int sum;

    public static int rollDice(int rolls) {
        sum = 0;
        faces = 6;
        Random gen = new Random();
        for (int i = 0; i < rolls; i++) {
            sum += (1 + gen.nextInt(1 + faces));
        }
        return sum;
    }

    public static int rollDice(int sides, int rolls) {
        sum = 0;
        faces = sides;
        Random gen = new Random();
        for (int i = 0; i < rolls; i++)
            sum += (1 + gen.nextInt(1 + faces));
        return sum;
    }


}
package org.gtlcore.gtlcore.utils;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import com.google.common.math.LongMath;

import java.text.DecimalFormat;
import java.util.Random;

public class NumberUtils {

    public static final String[] UNITS = { "", "K", "M", "G", "T", "P", "E", "Y", "Z", "R", "Q", "S", "O", "N" };

    public static String formatLong(long number) {
        DecimalFormat df = new DecimalFormat("#.##");
        double temp = number;
        int unitIndex = 0;
        while (temp >= 1000) {
            temp /= 1000;
            unitIndex++;
        }
        return df.format(temp) + UNITS[unitIndex];
    }

    public static MutableComponent numberText(long number) {
        return Component.literal(formatLong(number));
    }

    public static int getFakeVoltageTier(long voltage) {
        long a = voltage;
        int b = 0;
        while (a / 4L >= 8L) {
            b++;
            a /= 4L;
        }
        return b;
    }

    public static long getVoltageFromFakeTier(int tier) {
        return LongMath.pow(4L, tier + 1) * 2;
    }

    public static int chanceOccurrences(int count, int chance) {
        Random random = new Random();
        int occurrences = 0;
        for (int i = 0; i < count; i++) {
            if (random.nextInt(chance) == 0) {
                occurrences++;
            }
        }
        return occurrences;
    }
}

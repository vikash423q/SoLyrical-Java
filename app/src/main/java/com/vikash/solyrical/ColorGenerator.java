package com.vikash.solyrical;

import android.graphics.Color;

import java.util.Random;

public class ColorGenerator {

    final static Random mRandom = new Random(System.currentTimeMillis());

    public static int generateRandomColor() {
        // This is the base color which will be mixed with the generated one
        final int baseColor = Color.WHITE;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (baseRed + mRandom.nextInt(128)) / 2;
        final int green = (baseGreen + mRandom.nextInt(128)) / 2;
        final int blue = (baseBlue + mRandom.nextInt(128)) / 2;

        return Color.rgb(red, green, blue);
    }
}

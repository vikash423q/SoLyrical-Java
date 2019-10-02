package com.vikash.solyrical;

import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.util.Random;

public class ColorGenerator {

    final static Random mRandom = new Random(System.currentTimeMillis());

    public static int generateRandomColor() {
        // This is the base color which will be mixed with the generated one
        final int baseColor = Color.WHITE;

        final int baseRed = Color.red(baseColor);
        final int baseGreen = Color.green(baseColor);
        final int baseBlue = Color.blue(baseColor);

        final int red = (baseRed + mRandom.nextInt(128))/2;
        final int green = (baseGreen + mRandom.nextInt(128))/2;
        final int blue = (baseBlue + mRandom.nextInt(128))/2;

        return Color.rgb(red, green, blue);
    }

    public static int colorFader(int color){

        final int baseRed=Color.red(color);
        final int baseGreen=Color.green(color);
        final int baseBlue=Color.blue(color);

        final float t=0.3f+mRandom.nextFloat()*0.5f;
        final int red=(int)(baseRed+(255-baseRed)*t);
        final int blue=(int)(baseBlue+(255-baseBlue)*t);
        final int green=(int)(baseGreen+(255-baseGreen)*t);

        return Color.rgb(red,green,blue);

    }

}

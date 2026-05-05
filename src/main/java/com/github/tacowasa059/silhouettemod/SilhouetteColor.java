package com.github.tacowasa059.silhouettemod;

public record SilhouetteColor(int red, int green, int blue) {
    public static final SilhouetteColor BLACK = new SilhouetteColor(0, 0, 0);

    public SilhouetteColor {
        red = clamp(red);
        green = clamp(green);
        blue = clamp(blue);
    }

    private static int clamp(int value) {
        return Math.max(0, Math.min(255, value));
    }

    public float redFloat() {
        return red / 255.0F;
    }

    public float greenFloat() {
        return green / 255.0F;
    }

    public float blueFloat() {
        return blue / 255.0F;
    }
}

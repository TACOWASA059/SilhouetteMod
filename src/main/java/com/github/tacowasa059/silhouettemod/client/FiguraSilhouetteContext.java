package com.github.tacowasa059.silhouettemod.client;

import com.github.tacowasa059.silhouettemod.SilhouetteColor;

public final class FiguraSilhouetteContext {
    private static final ThreadLocal<SilhouetteColor> CURRENT_COLOR = new ThreadLocal<>();

    private FiguraSilhouetteContext() {
    }

    public static void set(SilhouetteColor color) {
        if (color == null) {
            CURRENT_COLOR.remove();
        } else {
            CURRENT_COLOR.set(color);
        }
    }

    public static SilhouetteColor get() {
        return CURRENT_COLOR.get();
    }
}

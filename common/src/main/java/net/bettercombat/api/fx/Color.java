package net.bettercombat.api.fx;

public record Color(float red, float green, float blue, float alpha) {
    public Color(float red, float green, float blue) {
        this(red, green, blue, 1);
    }
    public Color alpha(float alpha) {
        return new Color(red, green, blue, alpha);
    }

    public static Color from(int rgb) {
        float red = ((float) ((rgb >> 16) & 0xFF)) / 255F;
        float green = ((float) ((rgb >> 8) & 0xFF)) / 255F;
        float blue = ((float) (rgb & 0xFF)) / 255F;
        return new Color(red, green, blue);
    }

    public static Color fromRGBA(long rgba) {
        float red = ((float) ((rgba >> 24) & 0xFF)) / 255F;
        float green = ((float) ((rgba >> 16) & 0xFF)) / 255F;
        float blue = ((float) ((rgba >> 8) & 0xFF)) / 255F;
        float alpha = ((float) (rgba & 0xFF)) / 255F;
        return new Color(red, green, blue, alpha);
    }
    public static Color fromStringRGB(String rgbString) {
        // Expecting format #RRGGBB
        if (rgbString.startsWith("#")) {
            rgbString = rgbString.substring(1);
        }
        if (rgbString.length() != 6) {
            throw new IllegalArgumentException("Invalid RGB string format");
        }
        int rgb = Integer.parseInt(rgbString, 16);
        return from(rgb);
    }

    public Color blend(Color other, float ratio) {
        return blend(this, other, ratio);
    }
    public static Color blend(Color color1, Color color2, float ratio) {
        float red = color1.red * (1 - ratio) + color2.red * ratio;
        float green = color1.green * (1 - ratio) + color2.green * ratio;
        float blue = color1.blue * (1 - ratio) + color2.blue * ratio;
        float alpha = color1.alpha * (1 - ratio) + color2.alpha * ratio;
        return new Color(red, green, blue, alpha);
    }

    public record IntFormat(int red, int green, int blue, int alpha) {
        public static IntFormat fromLongRGBA(long rgba) {
            var red = (rgba >> 24) & 255;
            var green = (rgba >> 16) & 255;
            var blue = (rgba >> 8) & 255;
            var alpha = rgba & 255;
            return new IntFormat((int)red, (int)green, (int)blue, (int)alpha);
        }
    }
    public IntFormat toIntFormat() {
        return new IntFormat((int) (red * 255), (int) (green * 255), (int) (blue * 255), (int) (alpha * 255));
    }
    public record ByteFormat(byte red, byte green, byte blue, byte alpha) { }
    public ByteFormat toByteFormat() {
        return new ByteFormat((byte) (red * 255), (byte) (green * 255), (byte) (blue * 255), (byte) (alpha * 255));
    }
    public long toRGBA() {
        return ((long) (red * 255) << 24) | ((long) (green * 255) << 16) | ((long) (blue * 255) << 8) | ((long) (alpha * 255));
    }
    public long toARGB() {
        return ((long) (alpha * 255) << 24) | ((long) (red * 255) << 16) | ((long) (green * 255) << 8) | ((long) (blue * 255));
    }
    public String toStringRGB() {
        // Hexadecimal format #RRGGBBAA
        return String.format("#%02X%02X%02X",
                (int) (red * 255),
                (int) (green * 255),
                (int) (blue * 255));
    }

    public static final Color RED = new Color(1, 0, 0);
    public static final Color GREEN = new Color(0, 1, 0);
    public static final Color BLUE = new Color(0, 0, 1);
    public static final Color WHITE = new Color(1, 1, 1);
}

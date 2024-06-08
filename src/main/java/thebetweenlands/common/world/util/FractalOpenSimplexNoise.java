package thebetweenlands.common.world.util;

public class FractalOpenSimplexNoise {
    private OpenSimplexNoise[] octaves;
    private double scale;

    public FractalOpenSimplexNoise(int octaves, double scale) {
        this(Double.doubleToRawLongBits(Math.random()), octaves, scale);
    }

    public FractalOpenSimplexNoise(long seed, int octaves, double scale) {
        this.octaves = new OpenSimplexNoise[octaves];
        this.scale = scale;
        for (int i = 0; i < octaves; i++)
            this.octaves[i] = new OpenSimplexNoise(seed = ((seed * 25214903917L + 11L) % (1L << 48)));
    }

    public double eval(double x, double y) {
        double eval = 0;
        for (int o = 0; o < octaves.length; o++)
            eval += octaves[o].eval(x * (1 << o) * scale, y * (1 << o) * scale) * Math.pow(0.5, o);
        return Math.max(-1, Math.min(eval, 1));
    }

    public double eval(double x, double y, double z) {
        double eval = 0; 
        for (int o = 0; o < octaves.length; o++)
            eval += octaves[o].eval(x * (1 << o) * scale, y * (1 << o) * scale, z * (1 << o) * scale) * Math.pow(0.5, o);
        return Math.max(-1, Math.min(eval, 1));
    }

    public double eval(double x, double y, double z, double w) {
        double eval = 0;
        for (int o = 0; o < octaves.length; o++)
            eval += octaves[o].eval(x * (1 << o) * scale, y * (1 << o) * scale, z * (1 << o) * scale, w * (1 << o) * scale) * Math.pow(0.5, o);
        return Math.max(-1, Math.min(eval, 1));
    }
}

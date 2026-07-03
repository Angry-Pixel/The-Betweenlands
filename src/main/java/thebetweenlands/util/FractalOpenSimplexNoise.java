package thebetweenlands.util;

public final class FractalOpenSimplexNoise {
	private final OpenSimplexNoise[] octaves;

	public FractalOpenSimplexNoise(int octaves) {
		this(Double.doubleToRawLongBits(Math.random()), octaves);
	}

	public FractalOpenSimplexNoise(long seed, int octaves) {
		this.octaves = new OpenSimplexNoise[octaves];
		for (int i = 0; i < octaves; i++)
			this.octaves[i] = new OpenSimplexNoise(seed = ((seed * 25214903917L + 11L) % (1L << 48)));
	}
	
	private FractalOpenSimplexNoise(OpenSimplexNoise[] octaves) {
		this.octaves = octaves;
	}
	
	public static FractalOpenSimplexNoise create(long seed, int octaves, boolean additiveSeed) {
		if(!additiveSeed) {
			return new FractalOpenSimplexNoise(seed, octaves);
		} else {
			OpenSimplexNoise[] octaveArray = new OpenSimplexNoise[octaves];
			for (int i = 0; i < octaves; i++) {
				octaveArray[i] = new OpenSimplexNoise(seed + i);
			}
			return new FractalOpenSimplexNoise(octaveArray);
		}
	}
	
	public double eval(double x, double y) {
		final OpenSimplexNoise[] octaves = this.octaves;
		double eval = 0;
		for (int o = 0; o < octaves.length; o++)
			eval += octaves[o].eval(x * (1 << o), y * (1 << o)) * Math.pow(0.5, o);
		return Math.max(-1, Math.min(eval, 1));
	}

	public double eval(double x, double y, double z) {
		final OpenSimplexNoise[] octaves = this.octaves;
		double eval = 0;
		for (int o = 0; o < octaves.length; o++)
			eval += octaves[o].eval(x * (1 << o), y * (1 << o), z * (1 << o)) * Math.pow(0.5, o);
		return Math.max(-1, Math.min(eval, 1));
	}

	public double eval(double x, double y, double z, double w) {
		final OpenSimplexNoise[] octaves = this.octaves;
		double eval = 0;
		for (int o = 0; o < octaves.length; o++)
			eval += octaves[o].eval(x * (1 << o), y * (1 << o), z * (1 << o), w * (1 << o)) * Math.pow(0.5, o);
		return Math.max(-1, Math.min(eval, 1));
	}
}

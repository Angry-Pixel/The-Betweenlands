package thebetweenlands.common.colors;

import java.awt.Color;

import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;

public class BetweenlandsColorModifiers {
	
	// Use Simplex noise map, if negitive use color 1 else use color 2
	public static int Betweenlands2ColorMod(int x, int y, int color1, int color2, double noisescale) {
		Color Color1 = new Color(color1);
		Color Color2 = new Color(color2);
		int diffr = Color1.getRed() - Color2.getRed();
		int diffg = Color1.getGreen() - Color2.getGreen();
		int diffb = Color1.getBlue() - Color2.getBlue();
		
		XoroshiroRandomSource source = new XoroshiroRandomSource(69); // Nice
		SimplexNoise noise = new SimplexNoise(source);
		
		double factor = 1 - (noise.getValue(x * noisescale, y * noisescale));
		
		// clamping
		if (factor > 1) {
			factor = 1;
		}
		else if (factor < -1) {
			factor = 0;
		}
		
		return new Color((int) (Color1.getRed() - (diffr * factor)),(int) (Color1.getGreen() - (diffg * factor)), (int) (Color1.getBlue() - (diffb * factor))).getRGB();
	}
}

package thebetweenlands.util;

// Just a MathHelper replacement
public class MathUtil {

	public static long getCoordinateRandom(int x, int y, int z) {
		long i = (long) (x * 3129871) ^ (long) z * 116129781L ^ (long) y;
		i = i * i * 42317861L + i * 11L;
		return i;
	}
}

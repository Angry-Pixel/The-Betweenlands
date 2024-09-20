package thebetweenlands.util;

public class AnimationMathHelper {
	public float b;
	public float c;
	public float d;
	public float e;
	public float f;
	public float h;

	public AnimationMathHelper() {
		b = 0.0F;
		c = 0.0F;
		h = 1.0F;
	}

	public float swing(float speed, float max, boolean reset) {
		if (reset) {
			b = 0.0F;
			c = 0.0F;
			h = 1.0F;
		}
		e = b;
		d = c;
		c = (float) ((double) c + 4 * 0.8F);

		if (c < 0.0F)
			c = 0.0F;

		if (c > speed)
			c = speed;

		if (h < speed)
			h = speed;

		h = (float) ((double) h * 0.8F);
		b += h * 0.5F;

		float f1 = e + (b - e);
		float f2 = d + (c - d);
		return (float) ((Math.sin(f1) + 0.5F) * f2 * max);
	}
}
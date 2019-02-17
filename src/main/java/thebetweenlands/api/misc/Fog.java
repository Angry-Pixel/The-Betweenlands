package thebetweenlands.api.misc;

import org.lwjgl.opengl.GL11;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Fog {
	public static enum FogType {
		LINEAR, EXP, EXP2;
	}

	protected FogType type;
	protected float density, red, green, blue, colorMultiplier, start, end, colorIncrement, distanceIncrementMultiplier, densityIncrement;

	public Fog(FogType type, float density, float red, float green, float blue, float colorMultiplier, float start, float end, float colorIncrement, float distanceIncrementMultiplier, float densityIncrement) {
		this.type = type;
		this.density = density;
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.start = start;
		this.end = end;
		this.colorIncrement = colorIncrement;
		this.distanceIncrementMultiplier = distanceIncrementMultiplier;
		this.densityIncrement = densityIncrement;
		this.colorMultiplier = colorMultiplier;
	}

	public Fog(FogType type, float density, float red, float green, float blue, float colorMultiplier, float start, float end)  {
		this(type, density, red, green, blue, colorMultiplier, start, end, 0.001F, 1.0F, 1.0F);
	}

	public Fog(Fog fog) {
		this(fog.type, fog.density, fog.red, fog.green, fog.blue, fog.colorMultiplier, fog.start, fog.end, fog.colorIncrement, fog.distanceIncrementMultiplier, fog.densityIncrement);
	}

	/**
	 * Returns the color increment
	 * @return
	 */
	public float getColorIncrement() {
		return this.colorIncrement;
	}

	/**
	 * Returns the distance increment multiplier
	 * @return
	 */
	public float getDistanceIncrementMultiplier() {
		return this.distanceIncrementMultiplier;
	}

	/**
	 * Returns the density increment
	 * @return
	 */
	public float getDensityIncrement() {
		return this.densityIncrement;
	}

	/**
	 * Returns the fog type
	 * @return
	 */
	public FogType getFogType() {
		return this.type;
	}

	/**
	 * Returns the fog type GL constant
	 * @return
	 */
	@OnlyIn(Dist.CLIENT)
	public int getGlFogType() {
		switch(this.type) {
		default:
		case LINEAR:
			return GL11.GL_LINEAR;
		case EXP:
			return GL11.GL_EXP;
		case EXP2:
			return GL11.GL_EXP2;
		}
	}

	/**
	 * Returns the fog density (only for {@link FogType#EXP} and {@link FogType#EXP2})
	 * @return
	 */
	public float getDensity() {
		return this.density;
	}

	/**
	 * Returns the red component
	 * @return
	 */
	public float getRed() {
		return this.red;
	}

	/**
	 * Returns the green component
	 * @return
	 */
	public float getGreen() {
		return this.green;
	}

	/**
	 * Returns the blue component
	 * @return
	 */
	public float getBlue() {
		return this.blue;
	}

	/**
	 * Returns the color multiplier
	 * @return
	 */
	public float getColorMultiplier() {
		return this.colorMultiplier;
	}

	/**
	 * Returns the fog start (only for {@link FogType#LINEAR})
	 * @return
	 */
	public float getStart() {
		return this.start;
	}

	/**
	 * Returns the fog end (only for {@link FogType#LINEAR})
	 * @return
	 */
	public float getEnd() {
		return this.end;
	}

	public static class MutableFog extends Fog {
		public MutableFog(FogType type, float density, float red, float green, float blue, float colorMultiplier, float start, float end) {
			super(type, density, red, green, blue, colorMultiplier, start, end);
		}

		public MutableFog(FogType type, float density, float red, float green, float blue, float colorMultiplier, float start, float end, float colorIncrement, float distanceIncrementMultiplier, float densityIncrement) {
			super(type, density, red, green, blue, colorMultiplier, start, end, colorIncrement, distanceIncrementMultiplier, densityIncrement);
		}

		public MutableFog(Fog fog) {
			super(fog);
		}

		public MutableFog() {
			super(FogType.LINEAR, 0, 0, 0, 0, 1.0F, 0, 0, 0.001F, 1.0F, 1.0F);
		}

		public MutableFog setColorIncrement(float increment) {
			this.colorIncrement = increment;
			return this;
		}

		public MutableFog setDistanceIncrementMultiplier(float multiplier) {
			this.distanceIncrementMultiplier = multiplier;
			return this;
		}

		public MutableFog setDensityIncrement(float increment) {
			this.densityIncrement = increment;
			return this;
		}

		public MutableFog setType(FogType type) {
			this.type = type;
			return this;
		}

		public MutableFog setDensity(float density) {
			this.density = density;
			return this;
		}

		public MutableFog setRed(float red) {
			this.red = red;
			return this;
		}

		public MutableFog setGreen(float green) {
			this.green = green;
			return this;
		}

		public MutableFog setBlue(float blue) {
			this.blue = blue;
			return this;
		}

		public MutableFog setColor(float red, float green, float blue) {
			this.setRed(red);
			this.setGreen(green);
			this.setBlue(blue);
			return this;
		}
		
		public MutableFog setStart(float start) {
			this.start = start;
			return this;
		}

		public MutableFog setEnd(float end) {
			this.end = end;
			return this;
		}

		public MutableFog setColorMultiplier(float multiplier) {
			this.colorMultiplier = multiplier;
			return this;
		}

		public MutableFog set(Fog fog) {
			this.setType(fog.type).setDensity(fog.density).setRed(fog.getRed()).setGreen(fog.getGreen()).setBlue(fog.getBlue()).setStart(fog.start).setEnd(fog.end)
			.setColorIncrement(fog.getColorIncrement()).setDistanceIncrementMultiplier(fog.getDistanceIncrementMultiplier()).setDensityIncrement(fog.getDensityIncrement());
			return this;
		}

		public Fog toImmutable() {
			return new Fog(this);
		}
	}
}

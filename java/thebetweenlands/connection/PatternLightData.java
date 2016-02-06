package thebetweenlands.connection;

import thebetweenlands.items.lanterns.LightVariant;

public class PatternLightData {
	private LightVariant lightVariant;

	private byte color;

	public PatternLightData(LightVariant lightVariant, byte color) {
		this.lightVariant = lightVariant;
		this.color = color;
	}

	public LightVariant getLightVariant() {
		return lightVariant;
	}

	public byte getColor() {
		return color;
	}
}

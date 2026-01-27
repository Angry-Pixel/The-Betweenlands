package thebetweenlands.client.renderer.util;

import net.minecraft.client.model.geom.builders.UVPair;

public record UVSquare(UVPair start, UVPair end) {
	public static final UVSquare IDENTITY = UVSquare.of(0.0f, 0.0f, 1.0f, 1.0f);
	
	public static UVSquare of(float minU, float minV, float maxU, float maxV) {
		return new UVSquare(new UVPair(minU, minV), new UVPair(maxU, maxV));
	}
	
	public static UVSquare of(float minU, float minV, float maxU, float maxV, float uvScale) {
		return new UVSquare(new UVPair(minU / uvScale, minV / uvScale), new UVPair(maxU / uvScale, maxV / uvScale));
	}
	
	public static UVSquare of(float minU, float minV, float maxU, float maxV, float uScale, float vScale) {
		return new UVSquare(new UVPair(minU / uScale, minV / vScale), new UVPair(maxU / uScale, maxV / vScale));
	}
}

package thebetweenlands.client.renderer.util;

import net.minecraft.client.model.geom.builders.UVPair;

public record UVSquare(UVPair start, UVPair end) {
	public static UVSquare of(float minU, float minV, float maxU, float maxV) {
		return new UVSquare(new UVPair(minU, minV), new UVPair(maxU, maxV));
	}
	
	public static UVSquare of(float minU, float minV, float maxU, float maxV, float uvScale) {
		return new UVSquare(new UVPair(minU / uvScale, minV / uvScale), new UVPair(maxU / uvScale, maxV / uvScale));
	}
}

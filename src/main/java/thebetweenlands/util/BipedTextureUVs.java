package thebetweenlands.util;

public record BipedTextureUVs(
	int textureWidth, int textureHeight,
	UVPair leftArm, UVPair leftSleeve,
	UVPair rightArm, UVPair rightSleeve,
	UVPair leftLeg, UVPair leftPant,
	UVPair rightLeg, UVPair rightPant,
	UVPair body, UVPair jacket) {

	public static BipedTextureUVs forPlayer() {
		return new BipedTextureUVs(
			64, 64,
			new UVPair(32, 48),
			new UVPair(48, 48),
			new UVPair(40, 16),
			new UVPair(40, 32),
			new UVPair(16, 48),
			new UVPair(0, 48),
			new UVPair(0, 16),
			new UVPair(0, 32),
			new UVPair(16, 16),
			new UVPair(16, 32)
		);
	}

	public static BipedTextureUVs forBasicHumanoid(int width, int height) {
		return new BipedTextureUVs(
			width, height,
			new UVPair(40, 16),
			new UVPair(40, 16),
			new UVPair(40, 16),
			new UVPair(40, 16),
			new UVPair(0, 16),
			new UVPair(0, 16),
			new UVPair(0, 16),
			new UVPair(0, 16),
			new UVPair(16, 16),
			new UVPair(16, 16)
		);
	}

	public static BipedTextureUVs forUniqueLimbHumanoid(int width, int height) {
		return new BipedTextureUVs(
			width, height,
			new UVPair(32, 48),
			new UVPair(48, 48),
			new UVPair(40, 16),
			new UVPair(40, 32),
			new UVPair(16, 48),
			new UVPair(0, 48),
			new UVPair(0, 16),
			new UVPair(0, 32),
			new UVPair(16, 16),
			new UVPair(16, 32)
		);
	}

	public record UVPair(int u, int v) {
	}
}

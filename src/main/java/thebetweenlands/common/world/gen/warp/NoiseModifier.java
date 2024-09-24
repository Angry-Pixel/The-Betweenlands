package thebetweenlands.common.world.gen.warp;

public interface NoiseModifier {
    NoiseModifier PASS = ((density, height, zWidth, xWidth) -> density);

    double modifyNoise(double density, int height, int zWidth, int xWidth);
}

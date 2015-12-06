package thebetweenlands.utils;

import net.minecraft.util.IIcon;

public class AtlasIcon implements IIcon {

	private final IIcon baseIcon;
	private final int index, horizontalTiles;

	public AtlasIcon(IIcon baseIcon, int index, int horizontalTiles) {
		this.baseIcon = baseIcon;
		this.index = index;
		this.horizontalTiles = horizontalTiles;
	}

	public double getTileSize() {
		return (double)this.baseIcon.getIconWidth() / (double)this.horizontalTiles;
	}

	public double getRelativeTileSize() {
		return (double)this.getTileSize() / (double)this.baseIcon.getIconWidth();
	}

	@Override
	public int getIconWidth() {
		return (int)this.getRelativeTileSize();
	}

	@Override
	public int getIconHeight() {
		return (int)this.getRelativeTileSize();
	}

	@Override
	public float getMinU() {
		return this.baseIcon.getInterpolatedU((index % horizontalTiles) / (double)horizontalTiles * 16.0D);
	}

	@Override
	public float getMaxU() {
		return this.baseIcon.getInterpolatedU((index % horizontalTiles + 1) / (double)horizontalTiles * 16.0D);
	}

	@Override
	public float getInterpolatedU(double lerp) {
		return (float)(this.getMinU() + (this.getMaxU() - this.getMinU()) / 16.0D * lerp);
	}

	@Override
	public float getMinV() {
		return this.baseIcon.getInterpolatedV((index / horizontalTiles) / (double)horizontalTiles * 16.0D);
	}

	@Override
	public float getMaxV() {
		return this.baseIcon.getInterpolatedV((index / horizontalTiles + 1) / (double)horizontalTiles * 16.0D);
	}

	@Override
	public float getInterpolatedV(double lerp) {
		return (float)(this.getMinV() + (this.getMaxV() - this.getMinV()) / 16.0D * lerp);
	}

	@Override
	public String getIconName() {
		return this.baseIcon.getIconName() + ":" + this.index;
	}
}

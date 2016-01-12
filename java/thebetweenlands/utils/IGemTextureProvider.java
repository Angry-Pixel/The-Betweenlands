package thebetweenlands.utils;

import thebetweenlands.gemcircle.CircleGem;

public interface IGemTextureProvider {
	public IGemTextureProvider setGemTexture(CircleGem gem, String texture);
	public String getGemTexture(CircleGem gem);
}

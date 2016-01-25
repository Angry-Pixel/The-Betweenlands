package thebetweenlands.utils;

import thebetweenlands.gemcircle.CircleGem;

public interface IGemTextureProvider {
	public IGemTextureProvider setGemTextures(CircleGem gem, String... texture);
	public String[] getGemTextures(CircleGem gem);
}

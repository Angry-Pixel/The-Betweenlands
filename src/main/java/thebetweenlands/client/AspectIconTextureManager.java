package thebetweenlands.client;

import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.TextureAtlasHolder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.common.TheBetweenlands;

public class AspectIconTextureManager extends TextureAtlasHolder {

	public AspectIconTextureManager(TextureManager textureManager) {
		super(textureManager, TheBetweenlands.prefix("textures/atlas/aspect_icons.png"), TheBetweenlands.prefix("aspect_icons"));
	}

	public TextureAtlasSprite get(Holder<AspectType> aspect) {
		return this.getSprite(aspect.unwrapKey().map(ResourceKey::location).orElseGet(MissingTextureAtlasSprite::getLocation));
	}
}

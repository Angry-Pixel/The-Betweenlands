package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface SpriteSet {
   TextureAtlasSprite get(int p_107966_, int p_107967_);

   TextureAtlasSprite get(Random p_107968_);
}
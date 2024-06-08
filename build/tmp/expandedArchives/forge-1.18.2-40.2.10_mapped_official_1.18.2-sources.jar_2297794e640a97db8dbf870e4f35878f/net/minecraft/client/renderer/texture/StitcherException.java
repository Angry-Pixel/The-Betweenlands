package net.minecraft.client.renderer.texture;

import java.util.Collection;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StitcherException extends RuntimeException {
   private final Collection<TextureAtlasSprite.Info> allSprites;

   public StitcherException(TextureAtlasSprite.Info p_118256_, Collection<TextureAtlasSprite.Info> p_118257_) {
      super(String.format("Unable to fit: %s - size: %dx%d - Maybe try a lower resolution resourcepack?", p_118256_.name(), p_118256_.width(), p_118256_.height()));
      this.allSprites = p_118257_;
   }

   public Collection<TextureAtlasSprite.Info> getAllSprites() {
      return this.allSprites;
   }
}
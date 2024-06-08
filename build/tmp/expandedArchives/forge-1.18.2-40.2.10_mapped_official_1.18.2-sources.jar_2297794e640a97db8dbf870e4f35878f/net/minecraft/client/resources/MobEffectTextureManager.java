package net.minecraft.client.resources;

import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MobEffectTextureManager extends TextureAtlasHolder {
   public MobEffectTextureManager(TextureManager p_118730_) {
      super(p_118730_, new ResourceLocation("textures/atlas/mob_effects.png"), "mob_effect");
   }

   protected Stream<ResourceLocation> getResourcesToLoad() {
      return Registry.MOB_EFFECT.keySet().stream();
   }

   public TextureAtlasSprite get(MobEffect p_118733_) {
      return this.getSprite(Registry.MOB_EFFECT.getKey(p_118733_));
   }
}
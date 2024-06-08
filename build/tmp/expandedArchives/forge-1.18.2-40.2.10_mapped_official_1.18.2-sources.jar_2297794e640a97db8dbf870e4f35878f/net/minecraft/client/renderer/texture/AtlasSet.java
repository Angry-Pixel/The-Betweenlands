package net.minecraft.client.renderer.texture;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AtlasSet implements AutoCloseable {
   private final Map<ResourceLocation, TextureAtlas> atlases;

   public AtlasSet(Collection<TextureAtlas> p_117970_) {
      this.atlases = p_117970_.stream().collect(Collectors.toMap(TextureAtlas::location, Function.identity()));
   }

   public TextureAtlas getAtlas(ResourceLocation p_117974_) {
      return this.atlases.get(p_117974_);
   }

   public TextureAtlasSprite getSprite(Material p_117972_) {
      return this.atlases.get(p_117972_.atlasLocation()).getSprite(p_117972_.texture());
   }

   public void close() {
      this.atlases.values().forEach(TextureAtlas::clearTextureData);
      this.atlases.clear();
   }
}
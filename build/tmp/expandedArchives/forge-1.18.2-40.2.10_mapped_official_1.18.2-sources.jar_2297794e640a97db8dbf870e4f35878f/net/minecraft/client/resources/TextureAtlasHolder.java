package net.minecraft.client.resources;

import java.util.stream.Stream;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TextureAtlasHolder extends SimplePreparableReloadListener<TextureAtlas.Preparations> implements AutoCloseable {
   private final TextureAtlas textureAtlas;
   private final String prefix;

   public TextureAtlasHolder(TextureManager p_118887_, ResourceLocation p_118888_, String p_118889_) {
      this.prefix = p_118889_;
      this.textureAtlas = new TextureAtlas(p_118888_);
      p_118887_.register(this.textureAtlas.location(), this.textureAtlas);
   }

   protected abstract Stream<ResourceLocation> getResourcesToLoad();

   protected TextureAtlasSprite getSprite(ResourceLocation p_118902_) {
      return this.textureAtlas.getSprite(this.resolveLocation(p_118902_));
   }

   private ResourceLocation resolveLocation(ResourceLocation p_118907_) {
      return new ResourceLocation(p_118907_.getNamespace(), this.prefix + "/" + p_118907_.getPath());
   }

   protected TextureAtlas.Preparations prepare(ResourceManager p_118891_, ProfilerFiller p_118892_) {
      p_118892_.startTick();
      p_118892_.push("stitching");
      TextureAtlas.Preparations textureatlas$preparations = this.textureAtlas.prepareToStitch(p_118891_, this.getResourcesToLoad().map(this::resolveLocation), p_118892_, 0);
      p_118892_.pop();
      p_118892_.endTick();
      return textureatlas$preparations;
   }

   protected void apply(TextureAtlas.Preparations p_118894_, ResourceManager p_118895_, ProfilerFiller p_118896_) {
      p_118896_.startTick();
      p_118896_.push("upload");
      this.textureAtlas.reload(p_118894_);
      p_118896_.pop();
      p_118896_.endTick();
   }

   public void close() {
      this.textureAtlas.clearTextureData();
   }
}
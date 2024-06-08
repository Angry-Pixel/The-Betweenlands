package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.platform.PngInfo;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Mth;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureAtlas extends AbstractTexture implements Tickable {
   private static final Logger LOGGER = LogUtils.getLogger();
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation LOCATION_BLOCKS = InventoryMenu.BLOCK_ATLAS;
   /** @deprecated */
   @Deprecated
   public static final ResourceLocation LOCATION_PARTICLES = new ResourceLocation("textures/atlas/particles.png");
   private static final String FILE_EXTENSION = ".png";
   private final List<Tickable> animatedTextures = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> texturesByName = Maps.newHashMap();
   private final ResourceLocation location;
   private final int maxSupportedTextureSize;

   public TextureAtlas(ResourceLocation p_118269_) {
      this.location = p_118269_;
      this.maxSupportedTextureSize = RenderSystem.maxSupportedTextureSize();
   }

   public void load(ResourceManager p_118282_) {
   }

   public void reload(TextureAtlas.Preparations p_118313_) {
      this.sprites.clear();
      this.sprites.addAll(p_118313_.sprites);
      LOGGER.info("Created: {}x{}x{} {}-atlas", p_118313_.width, p_118313_.height, p_118313_.mipLevel, this.location);
      TextureUtil.prepareImage(this.getId(), p_118313_.mipLevel, p_118313_.width, p_118313_.height);
      this.clearTextureData();

      for(TextureAtlasSprite textureatlassprite : p_118313_.regions) {
         this.texturesByName.put(textureatlassprite.getName(), textureatlassprite);

         try {
            textureatlassprite.uploadFirstFrame();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Stitching texture atlas");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Texture being stitched together");
            crashreportcategory.setDetail("Atlas path", this.location);
            crashreportcategory.setDetail("Sprite", textureatlassprite);
            throw new ReportedException(crashreport);
         }

         Tickable tickable = textureatlassprite.getAnimationTicker();
         if (tickable != null) {
            this.animatedTextures.add(tickable);
         }
      }

      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPost(this);
   }

   public TextureAtlas.Preparations prepareToStitch(ResourceManager p_118308_, Stream<ResourceLocation> p_118309_, ProfilerFiller p_118310_, int p_118311_) {
      p_118310_.push("preparing");
      Set<ResourceLocation> set = p_118309_.peek((p_118327_) -> {
         if (p_118327_ == null) {
            throw new IllegalArgumentException("Location cannot be null!");
         }
      }).collect(Collectors.toSet());
      int i = this.maxSupportedTextureSize;
      Stitcher stitcher = new Stitcher(i, i, p_118311_);
      int j = Integer.MAX_VALUE;
      int k = 1 << p_118311_;
      p_118310_.popPush("extracting_frames");
      net.minecraftforge.client.ForgeHooksClient.onTextureStitchedPre(this, set);

      for(TextureAtlasSprite.Info textureatlassprite$info : this.getBasicSpriteInfos(p_118308_, set)) {
         j = Math.min(j, Math.min(textureatlassprite$info.width(), textureatlassprite$info.height()));
         int l = Math.min(Integer.lowestOneBit(textureatlassprite$info.width()), Integer.lowestOneBit(textureatlassprite$info.height()));
         if (l < k) {
            LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", textureatlassprite$info.name(), textureatlassprite$info.width(), textureatlassprite$info.height(), Mth.log2(k), Mth.log2(l));
            k = l;
         }

         stitcher.registerSprite(textureatlassprite$info);
      }

      int i1 = Math.min(j, k);
      int j1 = Mth.log2(i1);
      int k1 = p_118311_;
      if (false) // FORGE: do not lower the mipmap level
      if (j1 < p_118311_) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.location, p_118311_, j1, i1);
         k1 = j1;
      } else {
         k1 = p_118311_;
      }

      p_118310_.popPush("register");
      stitcher.registerSprite(MissingTextureAtlasSprite.info());
      p_118310_.popPush("stitching");

      try {
         stitcher.stitch();
      } catch (StitcherException stitcherexception) {
         CrashReport crashreport = CrashReport.forThrowable(stitcherexception, "Stitching");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Stitcher");
         crashreportcategory.setDetail("Sprites", stitcherexception.getAllSprites().stream().map((p_118315_) -> {
            return String.format("%s[%dx%d]", p_118315_.name(), p_118315_.width(), p_118315_.height());
         }).collect(Collectors.joining(",")));
         crashreportcategory.setDetail("Max Texture Size", i);
         throw new ReportedException(crashreport);
      }

      p_118310_.popPush("loading");
      List<TextureAtlasSprite> list = this.getLoadedSprites(p_118308_, stitcher, k1);
      p_118310_.pop();
      return new TextureAtlas.Preparations(set, stitcher.getWidth(), stitcher.getHeight(), k1, list);
   }

   private Collection<TextureAtlasSprite.Info> getBasicSpriteInfos(ResourceManager p_118305_, Set<ResourceLocation> p_118306_) {
      List<CompletableFuture<?>> list = Lists.newArrayList();
      Queue<TextureAtlasSprite.Info> queue = new ConcurrentLinkedQueue<>();

      for(ResourceLocation resourcelocation : p_118306_) {
         if (!MissingTextureAtlasSprite.getLocation().equals(resourcelocation)) {
            list.add(CompletableFuture.runAsync(() -> {
               ResourceLocation resourcelocation1 = this.getResourceLocation(resourcelocation);

               TextureAtlasSprite.Info textureatlassprite$info;
               try {
                  Resource resource = p_118305_.getResource(resourcelocation1);

                  try {
                     PngInfo pnginfo = new PngInfo(resource.toString(), resource.getInputStream());
                     AnimationMetadataSection animationmetadatasection = resource.getMetadata(AnimationMetadataSection.SERIALIZER);
                     if (animationmetadatasection == null) {
                        animationmetadatasection = AnimationMetadataSection.EMPTY;
                     }

                     Pair<Integer, Integer> pair = animationmetadatasection.getFrameSize(pnginfo.width, pnginfo.height);
                     textureatlassprite$info = new TextureAtlasSprite.Info(resourcelocation, pair.getFirst(), pair.getSecond(), animationmetadatasection);
                  } catch (Throwable throwable1) {
                     if (resource != null) {
                        try {
                           resource.close();
                        } catch (Throwable throwable) {
                           throwable1.addSuppressed(throwable);
                        }
                     }

                     throw throwable1;
                  }

                  if (resource != null) {
                     resource.close();
                  }
               } catch (RuntimeException runtimeexception) {
                  LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
                  return;
               } catch (IOException ioexception) {
                  LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception);
                  return;
               }

               queue.add(textureatlassprite$info);
            }, Util.backgroundExecutor()));
         }
      }

      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      return queue;
   }

   private List<TextureAtlasSprite> getLoadedSprites(ResourceManager p_118284_, Stitcher p_118285_, int p_118286_) {
      Queue<TextureAtlasSprite> queue = new ConcurrentLinkedQueue<>();
      List<CompletableFuture<?>> list = Lists.newArrayList();
      p_118285_.gatherSprites((p_174703_, p_174704_, p_174705_, p_174706_, p_174707_) -> {
         if (p_174703_ == MissingTextureAtlasSprite.info()) {
            MissingTextureAtlasSprite missingtextureatlassprite = MissingTextureAtlasSprite.newInstance(this, p_118286_, p_174704_, p_174705_, p_174706_, p_174707_);
            queue.add(missingtextureatlassprite);
         } else {
            list.add(CompletableFuture.runAsync(() -> {
               TextureAtlasSprite textureatlassprite = this.load(p_118284_, p_174703_, p_174704_, p_174705_, p_118286_, p_174706_, p_174707_);
               if (textureatlassprite != null) {
                  queue.add(textureatlassprite);
               }

            }, Util.backgroundExecutor()));
         }

      });
      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      return Lists.newArrayList(queue);
   }

   @Nullable
   private TextureAtlasSprite load(ResourceManager p_118288_, TextureAtlasSprite.Info p_118289_, int p_118290_, int p_118291_, int p_118292_, int p_118293_, int p_118294_) {
      ResourceLocation resourcelocation = this.getResourceLocation(p_118289_.name());

      try {
         Resource resource = p_118288_.getResource(resourcelocation);

         TextureAtlasSprite textureatlassprite;
         try {
            NativeImage nativeimage = NativeImage.read(resource.getInputStream());
            textureatlassprite = net.minecraftforge.client.ForgeHooksClient.loadTextureAtlasSprite(this, p_118288_, p_118289_, resource, p_118290_, p_118291_, p_118293_, p_118294_, p_118292_, nativeimage);
            if (textureatlassprite == null)
            textureatlassprite = new TextureAtlasSprite(this, p_118289_, p_118292_, p_118290_, p_118291_, p_118293_, p_118294_, nativeimage);
         } catch (Throwable throwable1) {
            if (resource != null) {
               try {
                  resource.close();
               } catch (Throwable throwable) {
                  throwable1.addSuppressed(throwable);
               }
            }

            throw throwable1;
         }

         if (resource != null) {
            resource.close();
         }

         return textureatlassprite;
      } catch (RuntimeException runtimeexception) {
         LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
         return null;
      } catch (IOException ioexception) {
         LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
         return null;
      }
   }

   private ResourceLocation getResourceLocation(ResourceLocation p_118325_) {
      return new ResourceLocation(p_118325_.getNamespace(), String.format("textures/%s%s", p_118325_.getPath(), ".png"));
   }

   public void cycleAnimationFrames() {
      this.bind();

      for(Tickable tickable : this.animatedTextures) {
         tickable.tick();
      }

   }

   public void tick() {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(this::cycleAnimationFrames);
      } else {
         this.cycleAnimationFrames();
      }

   }

   public TextureAtlasSprite getSprite(ResourceLocation p_118317_) {
      TextureAtlasSprite textureatlassprite = this.texturesByName.get(p_118317_);
      return textureatlassprite == null ? this.texturesByName.get(MissingTextureAtlasSprite.getLocation()) : textureatlassprite;
   }

   public void clearTextureData() {
      for(TextureAtlasSprite textureatlassprite : this.texturesByName.values()) {
         textureatlassprite.close();
      }

      this.texturesByName.clear();
      this.animatedTextures.clear();
   }

   public ResourceLocation location() {
      return this.location;
   }

   public void updateFilter(TextureAtlas.Preparations p_118323_) {
      this.setFilter(false, p_118323_.mipLevel > 0);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Preparations {
      final Set<ResourceLocation> sprites;
      final int width;
      final int height;
      final int mipLevel;
      final List<TextureAtlasSprite> regions;

      public Preparations(Set<ResourceLocation> p_118337_, int p_118338_, int p_118339_, int p_118340_, List<TextureAtlasSprite> p_118341_) {
         this.sprites = p_118337_;
         this.width = p_118338_;
         this.height = p_118339_;
         this.mipLevel = p_118340_;
         this.regions = p_118341_;
      }
   }
}

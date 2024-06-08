package net.minecraft.client.renderer.texture;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.logging.LogUtils;
import com.mojang.realmsclient.RealmsMainScreen;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureManager implements PreparableReloadListener, Tickable, AutoCloseable {
   private static final Logger LOGGER = LogUtils.getLogger();
   public static final ResourceLocation INTENTIONAL_MISSING_TEXTURE = new ResourceLocation("");
   private final Map<ResourceLocation, AbstractTexture> byPath = Maps.newHashMap();
   private final Set<Tickable> tickableTextures = Sets.newHashSet();
   private final Map<String, Integer> prefixRegister = Maps.newHashMap();
   private final ResourceManager resourceManager;

   public TextureManager(ResourceManager p_118474_) {
      this.resourceManager = p_118474_;
   }

   public void bindForSetup(ResourceLocation p_174785_) {
      if (!RenderSystem.isOnRenderThread()) {
         RenderSystem.recordRenderCall(() -> {
            this._bind(p_174785_);
         });
      } else {
         this._bind(p_174785_);
      }

   }

   private void _bind(ResourceLocation p_118520_) {
      AbstractTexture abstracttexture = this.byPath.get(p_118520_);
      if (abstracttexture == null) {
         abstracttexture = new SimpleTexture(p_118520_);
         this.register(p_118520_, abstracttexture);
      }

      abstracttexture.bind();
   }

   public void register(ResourceLocation p_118496_, AbstractTexture p_118497_) {
      p_118497_ = this.loadTexture(p_118496_, p_118497_);
      AbstractTexture abstracttexture = this.byPath.put(p_118496_, p_118497_);
      if (abstracttexture != p_118497_) {
         if (abstracttexture != null && abstracttexture != MissingTextureAtlasSprite.getTexture()) {
            this.tickableTextures.remove(abstracttexture);
            this.safeClose(p_118496_, abstracttexture);
         }

         if (p_118497_ instanceof Tickable) {
            this.tickableTextures.add((Tickable)p_118497_);
         }
      }

   }

   private void safeClose(ResourceLocation p_118509_, AbstractTexture p_118510_) {
      if (p_118510_ != MissingTextureAtlasSprite.getTexture()) {
         try {
            p_118510_.close();
         } catch (Exception exception) {
            LOGGER.warn("Failed to close texture {}", p_118509_, exception);
         }
      }

      p_118510_.releaseId();
   }

   private AbstractTexture loadTexture(ResourceLocation p_118516_, AbstractTexture p_118517_) {
      try {
         p_118517_.load(this.resourceManager);
         return p_118517_;
      } catch (IOException ioexception) {
         if (p_118516_ != INTENTIONAL_MISSING_TEXTURE) {
            LOGGER.warn("Failed to load texture: {}", p_118516_, ioexception);
         }

         return MissingTextureAtlasSprite.getTexture();
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Registering texture");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Resource location being registered");
         crashreportcategory.setDetail("Resource location", p_118516_);
         crashreportcategory.setDetail("Texture object class", () -> {
            return p_118517_.getClass().getName();
         });
         throw new ReportedException(crashreport);
      }
   }

   public AbstractTexture getTexture(ResourceLocation p_118507_) {
      AbstractTexture abstracttexture = this.byPath.get(p_118507_);
      if (abstracttexture == null) {
         abstracttexture = new SimpleTexture(p_118507_);
         this.register(p_118507_, abstracttexture);
      }

      return abstracttexture;
   }

   public AbstractTexture getTexture(ResourceLocation p_174787_, AbstractTexture p_174788_) {
      return this.byPath.getOrDefault(p_174787_, p_174788_);
   }

   public ResourceLocation register(String p_118491_, DynamicTexture p_118492_) {
      Integer integer = this.prefixRegister.get(p_118491_);
      if (integer == null) {
         integer = 1;
      } else {
         integer = integer + 1;
      }

      this.prefixRegister.put(p_118491_, integer);
      ResourceLocation resourcelocation = new ResourceLocation(String.format(Locale.ROOT, "dynamic/%s_%d", p_118491_, integer));
      this.register(resourcelocation, p_118492_);
      return resourcelocation;
   }

   public CompletableFuture<Void> preload(ResourceLocation p_118502_, Executor p_118503_) {
      if (!this.byPath.containsKey(p_118502_)) {
         PreloadedTexture preloadedtexture = new PreloadedTexture(this.resourceManager, p_118502_, p_118503_);
         this.byPath.put(p_118502_, preloadedtexture);
         return preloadedtexture.getFuture().thenRunAsync(() -> {
            this.register(p_118502_, preloadedtexture);
         }, TextureManager::execute);
      } else {
         return CompletableFuture.completedFuture((Void)null);
      }
   }

   private static void execute(Runnable p_118489_) {
      Minecraft.getInstance().execute(() -> {
         RenderSystem.recordRenderCall(p_118489_::run);
      });
   }

   public void tick() {
      for(Tickable tickable : this.tickableTextures) {
         tickable.tick();
      }

   }

   public void release(ResourceLocation p_118514_) {
      AbstractTexture abstracttexture = this.getTexture(p_118514_, MissingTextureAtlasSprite.getTexture());
      if (abstracttexture != MissingTextureAtlasSprite.getTexture()) {
         this.byPath.remove(p_118514_); // Forge: fix MC-98707
         TextureUtil.releaseTextureId(abstracttexture.getId());
      }

   }

   public void close() {
      this.byPath.forEach(this::safeClose);
      this.byPath.clear();
      this.tickableTextures.clear();
      this.prefixRegister.clear();
   }

   public CompletableFuture<Void> reload(PreparableReloadListener.PreparationBarrier p_118476_, ResourceManager p_118477_, ProfilerFiller p_118478_, ProfilerFiller p_118479_, Executor p_118480_, Executor p_118481_) {
      return CompletableFuture.allOf(TitleScreen.preloadResources(this, p_118480_), this.preload(AbstractWidget.WIDGETS_LOCATION, p_118480_)).thenCompose(p_118476_::wait).thenAcceptAsync((p_118485_) -> {
         MissingTextureAtlasSprite.getTexture();
         RealmsMainScreen.updateTeaserImages(this.resourceManager);
         Iterator<Entry<ResourceLocation, AbstractTexture>> iterator = this.byPath.entrySet().iterator();

         while(iterator.hasNext()) {
            Entry<ResourceLocation, AbstractTexture> entry = iterator.next();
            ResourceLocation resourcelocation = entry.getKey();
            AbstractTexture abstracttexture = entry.getValue();
            if (abstracttexture == MissingTextureAtlasSprite.getTexture() && !resourcelocation.equals(MissingTextureAtlasSprite.getLocation())) {
               iterator.remove();
            } else {
               abstracttexture.reset(this, p_118477_, resourcelocation, p_118481_);
            }
         }

      }, (p_118505_) -> {
         RenderSystem.recordRenderCall(p_118505_::run);
      });
   }
}

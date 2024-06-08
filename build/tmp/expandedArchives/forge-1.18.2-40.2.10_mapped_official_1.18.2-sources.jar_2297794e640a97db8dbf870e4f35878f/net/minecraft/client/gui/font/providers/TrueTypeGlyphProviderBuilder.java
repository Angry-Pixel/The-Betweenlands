package net.minecraft.client.gui.font.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.TrueTypeGlyphProvider;
import com.mojang.blaze3d.platform.TextureUtil;
import com.mojang.logging.LogUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryUtil;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TrueTypeGlyphProviderBuilder implements GlyphProviderBuilder {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final ResourceLocation location;
   private final float size;
   private final float oversample;
   private final float shiftX;
   private final float shiftY;
   private final String skip;

   public TrueTypeGlyphProviderBuilder(ResourceLocation p_95491_, float p_95492_, float p_95493_, float p_95494_, float p_95495_, String p_95496_) {
      this.location = p_95491_;
      this.size = p_95492_;
      this.oversample = p_95493_;
      this.shiftX = p_95494_;
      this.shiftY = p_95495_;
      this.skip = p_95496_;
   }

   public static GlyphProviderBuilder fromJson(JsonObject p_95500_) {
      float f = 0.0F;
      float f1 = 0.0F;
      if (p_95500_.has("shift")) {
         JsonArray jsonarray = p_95500_.getAsJsonArray("shift");
         if (jsonarray.size() != 2) {
            throw new JsonParseException("Expected 2 elements in 'shift', found " + jsonarray.size());
         }

         f = GsonHelper.convertToFloat(jsonarray.get(0), "shift[0]");
         f1 = GsonHelper.convertToFloat(jsonarray.get(1), "shift[1]");
      }

      StringBuilder stringbuilder = new StringBuilder();
      if (p_95500_.has("skip")) {
         JsonElement jsonelement = p_95500_.get("skip");
         if (jsonelement.isJsonArray()) {
            JsonArray jsonarray1 = GsonHelper.convertToJsonArray(jsonelement, "skip");

            for(int i = 0; i < jsonarray1.size(); ++i) {
               stringbuilder.append(GsonHelper.convertToString(jsonarray1.get(i), "skip[" + i + "]"));
            }
         } else {
            stringbuilder.append(GsonHelper.convertToString(jsonelement, "skip"));
         }
      }

      return new TrueTypeGlyphProviderBuilder(new ResourceLocation(GsonHelper.getAsString(p_95500_, "file")), GsonHelper.getAsFloat(p_95500_, "size", 11.0F), GsonHelper.getAsFloat(p_95500_, "oversample", 1.0F), f, f1, stringbuilder.toString());
   }

   @Nullable
   public GlyphProvider create(ResourceManager p_95498_) {
      STBTTFontinfo stbttfontinfo = null;
      ByteBuffer bytebuffer = null;

      try {
         Resource resource = p_95498_.getResource(new ResourceLocation(this.location.getNamespace(), "font/" + this.location.getPath()));

         TrueTypeGlyphProvider truetypeglyphprovider;
         try {
            LOGGER.debug("Loading font {}", (Object)this.location);
            stbttfontinfo = STBTTFontinfo.malloc();
            bytebuffer = TextureUtil.readResource(resource.getInputStream());
            bytebuffer.flip();
            LOGGER.debug("Reading font {}", (Object)this.location);
            if (!STBTruetype.stbtt_InitFont(stbttfontinfo, bytebuffer)) {
               throw new IOException("Invalid ttf");
            }

            truetypeglyphprovider = new TrueTypeGlyphProvider(bytebuffer, stbttfontinfo, this.size, this.oversample, this.shiftX, this.shiftY, this.skip);
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

         return truetypeglyphprovider;
      } catch (Exception exception) {
         LOGGER.error("Couldn't load truetype font {}", this.location, exception);
         if (stbttfontinfo != null) {
            stbttfontinfo.free();
         }

         MemoryUtil.memFree(bytebuffer);
         return null;
      }
   }
}
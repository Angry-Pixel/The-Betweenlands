package net.minecraft.client.gui.font;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.font.GlyphInfo;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.blaze3d.font.RawGlyph;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.gui.font.glyphs.BakedGlyph;
import net.minecraft.client.gui.font.glyphs.EmptyGlyph;
import net.minecraft.client.gui.font.glyphs.MissingGlyph;
import net.minecraft.client.gui.font.glyphs.WhiteGlyph;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontSet implements AutoCloseable {
   private static final EmptyGlyph SPACE_GLYPH = new EmptyGlyph();
   private static final GlyphInfo SPACE_INFO = () -> {
      return 4.0F;
   };
   private static final GlyphInfo ZERO_WIDTH_NO_JOIN_INFO = () -> {
      return 0.0F;
   };
   private static final int ZERO_WIDTH_NO_JOIN_CODEPOINT = 8204;
   private static final Random RANDOM = new Random();
   private final TextureManager textureManager;
   private final ResourceLocation name;
   private BakedGlyph missingGlyph;
   private BakedGlyph whiteGlyph;
   private final List<GlyphProvider> providers = Lists.newArrayList();
   private final Int2ObjectMap<BakedGlyph> glyphs = new Int2ObjectOpenHashMap<>();
   private final Int2ObjectMap<GlyphInfo> glyphInfos = new Int2ObjectOpenHashMap<>();
   private final Int2ObjectMap<IntList> glyphsByWidth = new Int2ObjectOpenHashMap<>();
   private final List<FontTexture> textures = Lists.newArrayList();

   public FontSet(TextureManager p_95062_, ResourceLocation p_95063_) {
      this.textureManager = p_95062_;
      this.name = p_95063_;
   }

   public void reload(List<GlyphProvider> p_95072_) {
      this.closeProviders();
      this.closeTextures();
      this.glyphs.clear();
      this.glyphInfos.clear();
      this.glyphsByWidth.clear();
      this.missingGlyph = this.stitch(MissingGlyph.INSTANCE);
      this.whiteGlyph = this.stitch(WhiteGlyph.INSTANCE);
      IntSet intset = new IntOpenHashSet();

      for(GlyphProvider glyphprovider : p_95072_) {
         intset.addAll(glyphprovider.getSupportedGlyphs());
      }

      Set<GlyphProvider> set = Sets.newHashSet();
      intset.forEach((int p_95076_) -> {
         for(GlyphProvider glyphprovider1 : p_95072_) {
            GlyphInfo glyphinfo = this.getGlyphInfoForSpace(p_95076_);
            if (glyphinfo == null) {
               glyphinfo = glyphprovider1.getGlyph(p_95076_);
            }

            if (glyphinfo != null) {
               set.add(glyphprovider1);
               if (glyphinfo != MissingGlyph.INSTANCE) {
                  this.glyphsByWidth.computeIfAbsent(Mth.ceil(glyphinfo.getAdvance(false)), (p_169091_) -> {
                     return new IntArrayList();
                  }).add(p_95076_);
               }
               break;
            }
         }

      });
      p_95072_.stream().filter(set::contains).forEach(this.providers::add);
   }

   public void close() {
      this.closeProviders();
      this.closeTextures();
   }

   private void closeProviders() {
      for(GlyphProvider glyphprovider : this.providers) {
         glyphprovider.close();
      }

      this.providers.clear();
   }

   private void closeTextures() {
      for(FontTexture fonttexture : this.textures) {
         fonttexture.close();
      }

      this.textures.clear();
   }

   @Nullable
   private GlyphInfo getGlyphInfoForSpace(int p_202362_) {
      GlyphInfo glyphinfo;
      switch(p_202362_) {
      case 32:
         glyphinfo = SPACE_INFO;
         break;
      case 8204:
         glyphinfo = ZERO_WIDTH_NO_JOIN_INFO;
         break;
      default:
         glyphinfo = null;
      }

      return glyphinfo;
   }

   public GlyphInfo getGlyphInfo(int p_95066_) {
      return this.glyphInfos.computeIfAbsent(p_95066_, (p_95088_) -> {
         GlyphInfo glyphinfo = this.getGlyphInfoForSpace(p_95088_);
         return (GlyphInfo)(glyphinfo == null ? this.getRaw(p_95088_) : glyphinfo);
      });
   }

   private RawGlyph getRaw(int p_95082_) {
      for(GlyphProvider glyphprovider : this.providers) {
         RawGlyph rawglyph = glyphprovider.getGlyph(p_95082_);
         if (rawglyph != null) {
            return rawglyph;
         }
      }

      return MissingGlyph.INSTANCE;
   }

   public BakedGlyph getGlyph(int p_95079_) {
      return this.glyphs.computeIfAbsent(p_95079_, (p_95086_) -> {
         Object object;
         switch(p_95086_) {
         case 32:
         case 8204:
            object = SPACE_GLYPH;
            break;
         default:
            object = this.stitch(this.getRaw(p_95086_));
         }

         return (BakedGlyph)object;
      });
   }

   private BakedGlyph stitch(RawGlyph p_95070_) {
      for(FontTexture fonttexture : this.textures) {
         BakedGlyph bakedglyph = fonttexture.add(p_95070_);
         if (bakedglyph != null) {
            return bakedglyph;
         }
      }

      FontTexture fonttexture1 = new FontTexture(new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "/" + this.textures.size()), p_95070_.isColored());
      this.textures.add(fonttexture1);
      this.textureManager.register(fonttexture1.getName(), fonttexture1);
      BakedGlyph bakedglyph1 = fonttexture1.add(p_95070_);
      return bakedglyph1 == null ? this.missingGlyph : bakedglyph1;
   }

   public BakedGlyph getRandomGlyph(GlyphInfo p_95068_) {
      IntList intlist = this.glyphsByWidth.get(Mth.ceil(p_95068_.getAdvance(false)));
      return intlist != null && !intlist.isEmpty() ? this.getGlyph(intlist.getInt(RANDOM.nextInt(intlist.size()))) : this.missingGlyph;
   }

   public BakedGlyph whiteGlyph() {
      return this.whiteGlyph;
   }
}
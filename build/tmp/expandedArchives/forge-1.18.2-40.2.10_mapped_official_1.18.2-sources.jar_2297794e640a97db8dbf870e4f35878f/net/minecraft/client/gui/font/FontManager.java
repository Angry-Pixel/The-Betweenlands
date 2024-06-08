package net.minecraft.client.gui.font;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.font.GlyphProvider;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.providers.GlyphProviderBuilderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontManager implements AutoCloseable {
   static final Logger LOGGER = LogUtils.getLogger();
   private static final String FONTS_PATH = "fonts.json";
   public static final ResourceLocation MISSING_FONT = new ResourceLocation("minecraft", "missing");
   private final FontSet missingFontSet;
   final Map<ResourceLocation, FontSet> fontSets = Maps.newHashMap();
   final TextureManager textureManager;
   private Map<ResourceLocation, ResourceLocation> renames = ImmutableMap.of();
   private final PreparableReloadListener reloadListener = new SimplePreparableReloadListener<Map<ResourceLocation, List<GlyphProvider>>>() {
      protected Map<ResourceLocation, List<GlyphProvider>> prepare(ResourceManager p_95024_, ProfilerFiller p_95025_) {
         p_95025_.startTick();
         Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         Map<ResourceLocation, List<GlyphProvider>> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : p_95024_.listResources("font", (p_95031_) -> {
            return p_95031_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring("font/".length(), s.length() - ".json".length()));
            List<GlyphProvider> list = map.computeIfAbsent(resourcelocation1, (p_95040_) -> {
               return Lists.newArrayList(new AllMissingGlyphProvider());
            });
            p_95025_.push(resourcelocation1::toString);

            try {
               for(Resource resource : p_95024_.getResources(resourcelocation)) {
                  p_95025_.push(resource::getSourceName);

                  try {
                     InputStream inputstream = resource.getInputStream();

                     try {
                        Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));

                        try {
                           p_95025_.push("reading");
                           JsonArray jsonarray = GsonHelper.getAsJsonArray(GsonHelper.fromJson(gson, reader, JsonObject.class), "providers");
                           p_95025_.popPush("parsing");

                           for(int i = jsonarray.size() - 1; i >= 0; --i) {
                              JsonObject jsonobject = GsonHelper.convertToJsonObject(jsonarray.get(i), "providers[" + i + "]");

                              try {
                                 String s1 = GsonHelper.getAsString(jsonobject, "type");
                                 GlyphProviderBuilderType glyphproviderbuildertype = GlyphProviderBuilderType.byName(s1);
                                 p_95025_.push(s1);
                                 GlyphProvider glyphprovider = glyphproviderbuildertype.create(jsonobject).create(p_95024_);
                                 if (glyphprovider != null) {
                                    list.add(glyphprovider);
                                 }

                                 p_95025_.pop();
                              } catch (RuntimeException runtimeexception) {
                                 FontManager.LOGGER.warn("Unable to read definition '{}' in {} in resourcepack: '{}': {}", resourcelocation1, "fonts.json", resource.getSourceName(), runtimeexception.getMessage());
                              }
                           }

                           p_95025_.pop();
                        } catch (Throwable throwable2) {
                           try {
                              reader.close();
                           } catch (Throwable throwable1) {
                              throwable2.addSuppressed(throwable1);
                           }

                           throw throwable2;
                        }

                        reader.close();
                     } catch (Throwable throwable3) {
                        if (inputstream != null) {
                           try {
                              inputstream.close();
                           } catch (Throwable throwable) {
                              throwable3.addSuppressed(throwable);
                           }
                        }

                        throw throwable3;
                     }

                     if (inputstream != null) {
                        inputstream.close();
                     }
                  } catch (RuntimeException runtimeexception1) {
                     FontManager.LOGGER.warn("Unable to load font '{}' in {} in resourcepack: '{}': {}", resourcelocation1, "fonts.json", resource.getSourceName(), runtimeexception1.getMessage());
                  }

                  p_95025_.pop();
               }
            } catch (IOException ioexception) {
               FontManager.LOGGER.warn("Unable to load font '{}' in {}: {}", resourcelocation1, "fonts.json", ioexception.getMessage());
            }

            p_95025_.push("caching");
            IntSet intset = new IntOpenHashSet();

            for(GlyphProvider glyphprovider1 : list) {
               intset.addAll(glyphprovider1.getSupportedGlyphs());
            }

            intset.forEach((int p_95034_) -> {
               if (p_95034_ != 32) {
                  for(GlyphProvider glyphprovider2 : Lists.reverse(list)) {
                     if (glyphprovider2.getGlyph(p_95034_) != null) {
                        break;
                     }
                  }

               }
            });
            p_95025_.pop();
            p_95025_.pop();
         }

         p_95025_.endTick();
         return map;
      }

      protected void apply(Map<ResourceLocation, List<GlyphProvider>> p_95036_, ResourceManager p_95037_, ProfilerFiller p_95038_) {
         p_95038_.startTick();
         p_95038_.push("closing");
         FontManager.this.fontSets.values().forEach(FontSet::close);
         FontManager.this.fontSets.clear();
         p_95038_.popPush("reloading");
         p_95036_.forEach((p_95042_, p_95043_) -> {
            FontSet fontset = new FontSet(FontManager.this.textureManager, p_95042_);
            fontset.reload(Lists.reverse(p_95043_));
            FontManager.this.fontSets.put(p_95042_, fontset);
         });
         p_95038_.pop();
         p_95038_.endTick();
      }

      public String getName() {
         return "FontManager";
      }
   };

   public FontManager(TextureManager p_95005_) {
      this.textureManager = p_95005_;
      this.missingFontSet = Util.make(new FontSet(p_95005_, MISSING_FONT), (p_95010_) -> {
         p_95010_.reload(Lists.newArrayList(new AllMissingGlyphProvider()));
      });
   }

   public void setRenames(Map<ResourceLocation, ResourceLocation> p_95012_) {
      this.renames = p_95012_;
   }

   public Font createFont() {
      return new Font((p_95014_) -> {
         return this.fontSets.getOrDefault(this.renames.getOrDefault(p_95014_, p_95014_), this.missingFontSet);
      });
   }

   public PreparableReloadListener getReloadListener() {
      return this.reloadListener;
   }

   public void close() {
      this.fontSets.values().forEach(FontSet::close);
      this.missingFontSet.close();
   }
}
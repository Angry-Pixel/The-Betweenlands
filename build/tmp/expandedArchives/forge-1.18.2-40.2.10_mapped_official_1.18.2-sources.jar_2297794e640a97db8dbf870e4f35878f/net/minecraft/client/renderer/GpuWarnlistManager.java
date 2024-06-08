package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlUtil;
import com.mojang.logging.LogUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GpuWarnlistManager extends SimplePreparableReloadListener<GpuWarnlistManager.Preparations> {
   private static final Logger LOGGER = LogUtils.getLogger();
   private static final ResourceLocation GPU_WARNLIST_LOCATION = new ResourceLocation("gpu_warnlist.json");
   private ImmutableMap<String, String> warnings = ImmutableMap.of();
   private boolean showWarning;
   private boolean warningDismissed;
   private boolean skipFabulous;

   public boolean hasWarnings() {
      return !this.warnings.isEmpty();
   }

   public boolean willShowWarning() {
      return this.hasWarnings() && !this.warningDismissed;
   }

   public void showWarning() {
      this.showWarning = true;
   }

   public void dismissWarning() {
      this.warningDismissed = true;
   }

   public void dismissWarningAndSkipFabulous() {
      this.warningDismissed = true;
      this.skipFabulous = true;
   }

   public boolean isShowingWarning() {
      return this.showWarning && !this.warningDismissed;
   }

   public boolean isSkippingFabulous() {
      return this.skipFabulous;
   }

   public void resetWarnings() {
      this.showWarning = false;
      this.warningDismissed = false;
      this.skipFabulous = false;
   }

   @Nullable
   public String getRendererWarnings() {
      return this.warnings.get("renderer");
   }

   @Nullable
   public String getVersionWarnings() {
      return this.warnings.get("version");
   }

   @Nullable
   public String getVendorWarnings() {
      return this.warnings.get("vendor");
   }

   @Nullable
   public String getAllWarnings() {
      StringBuilder stringbuilder = new StringBuilder();
      this.warnings.forEach((p_109235_, p_109236_) -> {
         stringbuilder.append(p_109235_).append(": ").append(p_109236_);
      });
      return stringbuilder.length() == 0 ? null : stringbuilder.toString();
   }

   protected GpuWarnlistManager.Preparations prepare(ResourceManager p_109220_, ProfilerFiller p_109221_) {
      List<Pattern> list = Lists.newArrayList();
      List<Pattern> list1 = Lists.newArrayList();
      List<Pattern> list2 = Lists.newArrayList();
      p_109221_.startTick();
      JsonObject jsonobject = parseJson(p_109220_, p_109221_);
      if (jsonobject != null) {
         p_109221_.push("compile_regex");
         compilePatterns(jsonobject.getAsJsonArray("renderer"), list);
         compilePatterns(jsonobject.getAsJsonArray("version"), list1);
         compilePatterns(jsonobject.getAsJsonArray("vendor"), list2);
         p_109221_.pop();
      }

      p_109221_.endTick();
      return new GpuWarnlistManager.Preparations(list, list1, list2);
   }

   protected void apply(GpuWarnlistManager.Preparations p_109226_, ResourceManager p_109227_, ProfilerFiller p_109228_) {
      this.warnings = p_109226_.apply();
   }

   private static void compilePatterns(JsonArray p_109223_, List<Pattern> p_109224_) {
      p_109223_.forEach((p_109239_) -> {
         p_109224_.add(Pattern.compile(p_109239_.getAsString(), 2));
      });
   }

   @Nullable
   private static JsonObject parseJson(ResourceManager p_109245_, ProfilerFiller p_109246_) {
      p_109246_.push("parse_json");
      JsonObject jsonobject = null;

      try {
         Resource resource = p_109245_.getResource(GPU_WARNLIST_LOCATION);

         try {
            BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));

            try {
               jsonobject = (new JsonParser()).parse(bufferedreader).getAsJsonObject();
            } catch (Throwable throwable2) {
               try {
                  bufferedreader.close();
               } catch (Throwable throwable1) {
                  throwable2.addSuppressed(throwable1);
               }

               throw throwable2;
            }

            bufferedreader.close();
         } catch (Throwable throwable3) {
            if (resource != null) {
               try {
                  resource.close();
               } catch (Throwable throwable) {
                  throwable3.addSuppressed(throwable);
               }
            }

            throw throwable3;
         }

         if (resource != null) {
            resource.close();
         }
      } catch (JsonSyntaxException | IOException ioexception) {
         LOGGER.warn("Failed to load GPU warnlist");
      }

      p_109246_.pop();
      return jsonobject;
   }

   @OnlyIn(Dist.CLIENT)
   protected static final class Preparations {
      private final List<Pattern> rendererPatterns;
      private final List<Pattern> versionPatterns;
      private final List<Pattern> vendorPatterns;

      Preparations(List<Pattern> p_109261_, List<Pattern> p_109262_, List<Pattern> p_109263_) {
         this.rendererPatterns = p_109261_;
         this.versionPatterns = p_109262_;
         this.vendorPatterns = p_109263_;
      }

      private static String matchAny(List<Pattern> p_109273_, String p_109274_) {
         List<String> list = Lists.newArrayList();

         for(Pattern pattern : p_109273_) {
            Matcher matcher = pattern.matcher(p_109274_);

            while(matcher.find()) {
               list.add(matcher.group());
            }
         }

         return String.join(", ", list);
      }

      ImmutableMap<String, String> apply() {
         Builder<String, String> builder = new Builder<>();
         String s = matchAny(this.rendererPatterns, GlUtil.getRenderer());
         if (!s.isEmpty()) {
            builder.put("renderer", s);
         }

         String s1 = matchAny(this.versionPatterns, GlUtil.getOpenGLVersion());
         if (!s1.isEmpty()) {
            builder.put("version", s1);
         }

         String s2 = matchAny(this.vendorPatterns, GlUtil.getVendor());
         if (!s2.isEmpty()) {
            builder.put("vendor", s2);
         }

         return builder.build();
      }
   }
}
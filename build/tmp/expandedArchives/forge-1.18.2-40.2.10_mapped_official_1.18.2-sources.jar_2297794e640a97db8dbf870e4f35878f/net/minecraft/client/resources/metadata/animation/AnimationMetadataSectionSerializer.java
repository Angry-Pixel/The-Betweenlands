package net.minecraft.client.resources.metadata.animation;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import javax.annotation.Nullable;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;

@OnlyIn(Dist.CLIENT)
public class AnimationMetadataSectionSerializer implements MetadataSectionSerializer<AnimationMetadataSection> {
   public AnimationMetadataSection fromJson(JsonObject p_119064_) {
      Builder<AnimationFrame> builder = ImmutableList.builder();
      int i = GsonHelper.getAsInt(p_119064_, "frametime", 1);
      if (i != 1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid default frame time");
      }

      if (p_119064_.has("frames")) {
         try {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(p_119064_, "frames");

            for(int j = 0; j < jsonarray.size(); ++j) {
               JsonElement jsonelement = jsonarray.get(j);
               AnimationFrame animationframe = this.getFrame(j, jsonelement);
               if (animationframe != null) {
                  builder.add(animationframe);
               }
            }
         } catch (ClassCastException classcastexception) {
            throw new JsonParseException("Invalid animation->frames: expected array, was " + p_119064_.get("frames"), classcastexception);
         }
      }

      int k = GsonHelper.getAsInt(p_119064_, "width", -1);
      int l = GsonHelper.getAsInt(p_119064_, "height", -1);
      if (k != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)k, "Invalid width");
      }

      if (l != -1) {
         Validate.inclusiveBetween(1L, 2147483647L, (long)l, "Invalid height");
      }

      boolean flag = GsonHelper.getAsBoolean(p_119064_, "interpolate", false);
      return new AnimationMetadataSection(builder.build(), k, l, i, flag);
   }

   @Nullable
   private AnimationFrame getFrame(int p_119059_, JsonElement p_119060_) {
      if (p_119060_.isJsonPrimitive()) {
         return new AnimationFrame(GsonHelper.convertToInt(p_119060_, "frames[" + p_119059_ + "]"));
      } else if (p_119060_.isJsonObject()) {
         JsonObject jsonobject = GsonHelper.convertToJsonObject(p_119060_, "frames[" + p_119059_ + "]");
         int i = GsonHelper.getAsInt(jsonobject, "time", -1);
         if (jsonobject.has("time")) {
            Validate.inclusiveBetween(1L, 2147483647L, (long)i, "Invalid frame time");
         }

         int j = GsonHelper.getAsInt(jsonobject, "index");
         Validate.inclusiveBetween(0L, 2147483647L, (long)j, "Invalid frame index");
         return new AnimationFrame(j, i);
      } else {
         return null;
      }
   }

   public String getMetadataSectionName() {
      return "animation";
   }
}
package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockFaceUV {
   public float[] uvs;
   public final int rotation;

   public BlockFaceUV(@Nullable float[] p_111390_, int p_111391_) {
      this.uvs = p_111390_;
      this.rotation = p_111391_;
   }

   public float getU(int p_111393_) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int i = this.getShiftedIndex(p_111393_);
         return this.uvs[i != 0 && i != 1 ? 2 : 0];
      }
   }

   public float getV(int p_111397_) {
      if (this.uvs == null) {
         throw new NullPointerException("uvs");
      } else {
         int i = this.getShiftedIndex(p_111397_);
         return this.uvs[i != 0 && i != 3 ? 3 : 1];
      }
   }

   private int getShiftedIndex(int p_111401_) {
      return (p_111401_ + this.rotation / 90) % 4;
   }

   public int getReverseIndex(int p_111399_) {
      return (p_111399_ + 4 - this.rotation / 90) % 4;
   }

   public void setMissingUv(float[] p_111395_) {
      if (this.uvs == null) {
         this.uvs = p_111395_;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<BlockFaceUV> {
      private static final int DEFAULT_ROTATION = 0;

      public BlockFaceUV deserialize(JsonElement p_111404_, Type p_111405_, JsonDeserializationContext p_111406_) throws JsonParseException {
         JsonObject jsonobject = p_111404_.getAsJsonObject();
         float[] afloat = this.getUVs(jsonobject);
         int i = this.getRotation(jsonobject);
         return new BlockFaceUV(afloat, i);
      }

      protected int getRotation(JsonObject p_111408_) {
         int i = GsonHelper.getAsInt(p_111408_, "rotation", 0);
         if (i >= 0 && i % 90 == 0 && i / 90 <= 3) {
            return i;
         } else {
            throw new JsonParseException("Invalid rotation " + i + " found, only 0/90/180/270 allowed");
         }
      }

      @Nullable
      private float[] getUVs(JsonObject p_111410_) {
         if (!p_111410_.has("uv")) {
            return null;
         } else {
            JsonArray jsonarray = GsonHelper.getAsJsonArray(p_111410_, "uv");
            if (jsonarray.size() != 4) {
               throw new JsonParseException("Expected 4 uv values, found: " + jsonarray.size());
            } else {
               float[] afloat = new float[4];

               for(int i = 0; i < afloat.length; ++i) {
                  afloat[i] = GsonHelper.convertToFloat(jsonarray.get(i), "uv[" + i + "]");
               }

               return afloat;
            }
         }
      }
   }
}
package net.minecraft.client.renderer.block.model;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.lang.reflect.Type;
import java.util.Objects;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Variant implements ModelState {
   private final ResourceLocation modelLocation;
   private final Transformation rotation;
   private final boolean uvLock;
   private final int weight;

   public Variant(ResourceLocation p_111879_, Transformation p_111880_, boolean p_111881_, int p_111882_) {
      this.modelLocation = p_111879_;
      this.rotation = p_111880_;
      this.uvLock = p_111881_;
      this.weight = p_111882_;
   }

   public ResourceLocation getModelLocation() {
      return this.modelLocation;
   }

   public Transformation getRotation() {
      return this.rotation;
   }

   public boolean isUvLocked() {
      return this.uvLock;
   }

   public int getWeight() {
      return this.weight;
   }

   public String toString() {
      return "Variant{modelLocation=" + this.modelLocation + ", rotation=" + this.rotation + ", uvLock=" + this.uvLock + ", weight=" + this.weight + "}";
   }

   public boolean equals(Object p_111888_) {
      if (this == p_111888_) {
         return true;
      } else if (!(p_111888_ instanceof Variant)) {
         return false;
      } else {
         Variant variant = (Variant)p_111888_;
         return this.modelLocation.equals(variant.modelLocation) && Objects.equals(this.rotation, variant.rotation) && this.uvLock == variant.uvLock && this.weight == variant.weight;
      }
   }

   public int hashCode() {
      int i = this.modelLocation.hashCode();
      i = 31 * i + this.rotation.hashCode();
      i = 31 * i + Boolean.valueOf(this.uvLock).hashCode();
      return 31 * i + this.weight;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<Variant> {
      @VisibleForTesting
      static final boolean DEFAULT_UVLOCK = false;
      @VisibleForTesting
      static final int DEFAULT_WEIGHT = 1;
      @VisibleForTesting
      static final int DEFAULT_X_ROTATION = 0;
      @VisibleForTesting
      static final int DEFAULT_Y_ROTATION = 0;

      public Variant deserialize(JsonElement p_111893_, Type p_111894_, JsonDeserializationContext p_111895_) throws JsonParseException {
         JsonObject jsonobject = p_111893_.getAsJsonObject();
         ResourceLocation resourcelocation = this.getModel(jsonobject);
         BlockModelRotation blockmodelrotation = this.getBlockRotation(jsonobject);
         boolean flag = this.getUvLock(jsonobject);
         int i = this.getWeight(jsonobject);
         return new Variant(resourcelocation, blockmodelrotation.getRotation(), flag, i);
      }

      private boolean getUvLock(JsonObject p_111903_) {
         return GsonHelper.getAsBoolean(p_111903_, "uvlock", false);
      }

      protected BlockModelRotation getBlockRotation(JsonObject p_111897_) {
         int i = GsonHelper.getAsInt(p_111897_, "x", 0);
         int j = GsonHelper.getAsInt(p_111897_, "y", 0);
         BlockModelRotation blockmodelrotation = BlockModelRotation.by(i, j);
         if (blockmodelrotation == null) {
            throw new JsonParseException("Invalid BlockModelRotation x: " + i + ", y: " + j);
         } else {
            return blockmodelrotation;
         }
      }

      protected ResourceLocation getModel(JsonObject p_111899_) {
         return new ResourceLocation(GsonHelper.getAsString(p_111899_, "model"));
      }

      protected int getWeight(JsonObject p_111901_) {
         int i = GsonHelper.getAsInt(p_111901_, "weight", 1);
         if (i < 1) {
            throw new JsonParseException("Invalid weight " + i + " found, expected integer >= 1");
         } else {
            return i;
         }
      }
   }
}
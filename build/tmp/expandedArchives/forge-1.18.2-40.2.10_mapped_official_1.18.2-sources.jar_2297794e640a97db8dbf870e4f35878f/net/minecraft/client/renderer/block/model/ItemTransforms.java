package net.minecraft.client.renderer.block.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemTransforms {
   public static final ItemTransforms NO_TRANSFORMS = new ItemTransforms();
   public final ItemTransform thirdPersonLeftHand;
   public final ItemTransform thirdPersonRightHand;
   public final ItemTransform firstPersonLeftHand;
   public final ItemTransform firstPersonRightHand;
   public final ItemTransform head;
   public final ItemTransform gui;
   public final ItemTransform ground;
   public final ItemTransform fixed;
   public final com.google.common.collect.ImmutableMap<TransformType, ItemTransform> moddedTransforms;

   private ItemTransforms() {
      this(ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM, ItemTransform.NO_TRANSFORM);
   }

   public ItemTransforms(ItemTransforms p_111807_) {
      this.thirdPersonLeftHand = p_111807_.thirdPersonLeftHand;
      this.thirdPersonRightHand = p_111807_.thirdPersonRightHand;
      this.firstPersonLeftHand = p_111807_.firstPersonLeftHand;
      this.firstPersonRightHand = p_111807_.firstPersonRightHand;
      this.head = p_111807_.head;
      this.gui = p_111807_.gui;
      this.ground = p_111807_.ground;
      this.fixed = p_111807_.fixed;
      this.moddedTransforms = p_111807_.moddedTransforms;
   }

   @Deprecated
   public ItemTransforms(ItemTransform p_111798_, ItemTransform p_111799_, ItemTransform p_111800_, ItemTransform p_111801_, ItemTransform p_111802_, ItemTransform p_111803_, ItemTransform p_111804_, ItemTransform p_111805_) {
      this(p_111798_, p_111799_, p_111800_, p_111801_, p_111802_, p_111803_, p_111804_, p_111805_, com.google.common.collect.ImmutableMap.of());
   }

   public ItemTransforms(ItemTransform p_111798_, ItemTransform p_111799_, ItemTransform p_111800_, ItemTransform p_111801_, ItemTransform p_111802_, ItemTransform p_111803_, ItemTransform p_111804_, ItemTransform p_111805_,
           com.google.common.collect.ImmutableMap<TransformType, ItemTransform> moddedTransforms) {
      this.thirdPersonLeftHand = p_111798_;
      this.thirdPersonRightHand = p_111799_;
      this.firstPersonLeftHand = p_111800_;
      this.firstPersonRightHand = p_111801_;
      this.head = p_111802_;
      this.gui = p_111803_;
      this.ground = p_111804_;
      this.fixed = p_111805_;
      this.moddedTransforms = moddedTransforms;
   }

   public ItemTransform getTransform(ItemTransforms.TransformType p_111809_) {
      switch(p_111809_) {
      case THIRD_PERSON_LEFT_HAND:
         return this.thirdPersonLeftHand;
      case THIRD_PERSON_RIGHT_HAND:
         return this.thirdPersonRightHand;
      case FIRST_PERSON_LEFT_HAND:
         return this.firstPersonLeftHand;
      case FIRST_PERSON_RIGHT_HAND:
         return this.firstPersonRightHand;
      case HEAD:
         return this.head;
      case GUI:
         return this.gui;
      case GROUND:
         return this.ground;
      case FIXED:
         return this.fixed;
      default:
         return moddedTransforms.getOrDefault(p_111809_, ItemTransform.NO_TRANSFORM);
      }
   }

   public boolean hasTransform(ItemTransforms.TransformType p_111811_) {
      return this.getTransform(p_111811_) != ItemTransform.NO_TRANSFORM;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Deserializer implements JsonDeserializer<ItemTransforms> {
      public ItemTransforms deserialize(JsonElement p_111820_, Type p_111821_, JsonDeserializationContext p_111822_) throws JsonParseException {
         JsonObject jsonobject = p_111820_.getAsJsonObject();
         ItemTransform itemtransform = this.getTransform(p_111822_, jsonobject, "thirdperson_righthand");
         ItemTransform itemtransform1 = this.getTransform(p_111822_, jsonobject, "thirdperson_lefthand");
         if (itemtransform1 == ItemTransform.NO_TRANSFORM) {
            itemtransform1 = itemtransform;
         }

         ItemTransform itemtransform2 = this.getTransform(p_111822_, jsonobject, "firstperson_righthand");
         ItemTransform itemtransform3 = this.getTransform(p_111822_, jsonobject, "firstperson_lefthand");
         if (itemtransform3 == ItemTransform.NO_TRANSFORM) {
            itemtransform3 = itemtransform2;
         }

         ItemTransform itemtransform4 = this.getTransform(p_111822_, jsonobject, "head");
         ItemTransform itemtransform5 = this.getTransform(p_111822_, jsonobject, "gui");
         ItemTransform itemtransform6 = this.getTransform(p_111822_, jsonobject, "ground");
         ItemTransform itemtransform7 = this.getTransform(p_111822_, jsonobject, "fixed");

         var builder = com.google.common.collect.ImmutableMap.<TransformType, ItemTransform>builder();
         for (TransformType type : TransformType.values()) {
            if (type.isModded()) {
               var transform = this.getTransform(p_111822_, jsonobject, type.getSerializeName());
               var fallbackType = type;
               while (transform == ItemTransform.NO_TRANSFORM && fallbackType.fallback != null) {
                  fallbackType = fallbackType.fallback;
                  transform = this.getTransform(p_111822_, jsonobject, fallbackType.getSerializeName());
               }
               if (transform != ItemTransform.NO_TRANSFORM){
                  builder.put(type, transform);
               }
            }
         }

         return new ItemTransforms(itemtransform1, itemtransform, itemtransform3, itemtransform2, itemtransform4, itemtransform5, itemtransform6, itemtransform7, builder.build());
      }

      private ItemTransform getTransform(JsonDeserializationContext p_111816_, JsonObject p_111817_, String p_111818_) {
         return p_111817_.has(p_111818_) ? p_111816_.deserialize(p_111817_.get(p_111818_), ItemTransform.class) : ItemTransform.NO_TRANSFORM;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum TransformType implements net.minecraftforge.common.IExtensibleEnum {
      NONE("none"),
      THIRD_PERSON_LEFT_HAND("thirdperson_lefthand"),
      THIRD_PERSON_RIGHT_HAND("thirdperson_righthand"),
      FIRST_PERSON_LEFT_HAND("firstperson_lefthand"),
      FIRST_PERSON_RIGHT_HAND("firstperson_righthand"),
      HEAD("head"),
      GUI("gui"),
      GROUND("ground"),
      FIXED("fixed");

      public boolean firstPerson() {
         return this == FIRST_PERSON_LEFT_HAND || this == FIRST_PERSON_RIGHT_HAND;
      }

      // Forge start
      private final String serializeName;
      private final boolean isModded;
      @javax.annotation.Nullable
      private final TransformType fallback;

      private TransformType(String name) {
         serializeName = name;
         isModded = false;
         fallback = null;
      }

      private TransformType(net.minecraft.resources.ResourceLocation serializeName) {
         this.serializeName = java.util.Objects.requireNonNull(serializeName, "Modded TransformTypes must have a non-null serializeName").toString();
         isModded = true;
         fallback = null;
      }

      private TransformType(net.minecraft.resources.ResourceLocation serializeName, TransformType fallback) {
         this.serializeName = java.util.Objects.requireNonNull(serializeName, "Modded TransformTypes must have a non-null serializeName").toString();
         isModded = true;
         this.fallback = fallback;
      }

      public boolean isModded() {
         return isModded;
      }

      @javax.annotation.Nullable
      public TransformType fallback() {
         return fallback;
      }

      public String getSerializeName() {
         return serializeName;
      }

      public static TransformType create(String keyName, net.minecraft.resources.ResourceLocation serializeName) {
         throw new IllegalStateException("Enum not extended!");
      }
      public static TransformType create(String keyName, net.minecraft.resources.ResourceLocation serializeName, TransformType fallback) {
         throw new IllegalStateException("Enum not extended!");
      }
   }
}

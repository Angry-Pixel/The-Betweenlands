package net.minecraft.advancements.critereon;

import com.google.common.base.Joiner;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.EntityType;

public abstract class EntityTypePredicate {
   public static final EntityTypePredicate ANY = new EntityTypePredicate() {
      public boolean matches(EntityType<?> p_37652_) {
         return true;
      }

      public JsonElement serializeToJson() {
         return JsonNull.INSTANCE;
      }
   };
   private static final Joiner COMMA_JOINER = Joiner.on(", ");

   public abstract boolean matches(EntityType<?> p_37642_);

   public abstract JsonElement serializeToJson();

   public static EntityTypePredicate fromJson(@Nullable JsonElement p_37644_) {
      if (p_37644_ != null && !p_37644_.isJsonNull()) {
         String s = GsonHelper.convertToString(p_37644_, "type");
         if (s.startsWith("#")) {
            ResourceLocation resourcelocation1 = new ResourceLocation(s.substring(1));
            return new EntityTypePredicate.TagPredicate(TagKey.create(Registry.ENTITY_TYPE_REGISTRY, resourcelocation1));
         } else {
            ResourceLocation resourcelocation = new ResourceLocation(s);
            EntityType<?> entitytype = Registry.ENTITY_TYPE.getOptional(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown entity type '" + resourcelocation + "', valid types are: " + COMMA_JOINER.join(Registry.ENTITY_TYPE.keySet()));
            });
            return new EntityTypePredicate.TypePredicate(entitytype);
         }
      } else {
         return ANY;
      }
   }

   public static EntityTypePredicate of(EntityType<?> p_37648_) {
      return new EntityTypePredicate.TypePredicate(p_37648_);
   }

   public static EntityTypePredicate of(TagKey<EntityType<?>> p_204082_) {
      return new EntityTypePredicate.TagPredicate(p_204082_);
   }

   static class TagPredicate extends EntityTypePredicate {
      private final TagKey<EntityType<?>> tag;

      public TagPredicate(TagKey<EntityType<?>> p_204084_) {
         this.tag = p_204084_;
      }

      public boolean matches(EntityType<?> p_37658_) {
         return p_37658_.is(this.tag);
      }

      public JsonElement serializeToJson() {
         return new JsonPrimitive("#" + this.tag.location());
      }
   }

   static class TypePredicate extends EntityTypePredicate {
      private final EntityType<?> type;

      public TypePredicate(EntityType<?> p_37661_) {
         this.type = p_37661_;
      }

      public boolean matches(EntityType<?> p_37664_) {
         return this.type == p_37664_;
      }

      public JsonElement serializeToJson() {
         return new JsonPrimitive(Registry.ENTITY_TYPE.getKey(this.type).toString());
      }
   }
}
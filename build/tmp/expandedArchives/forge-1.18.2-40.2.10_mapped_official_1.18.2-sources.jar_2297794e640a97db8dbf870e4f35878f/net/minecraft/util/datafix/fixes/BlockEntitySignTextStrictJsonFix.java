package net.minecraft.util.datafix.fixes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.serialization.Dynamic;
import java.lang.reflect.Type;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.GsonHelper;
import org.apache.commons.lang3.StringUtils;

public class BlockEntitySignTextStrictJsonFix extends NamedEntityFix {
   public static final Gson GSON = (new GsonBuilder()).registerTypeAdapter(Component.class, new JsonDeserializer<Component>() {
      public MutableComponent deserialize(JsonElement p_14875_, Type p_14876_, JsonDeserializationContext p_14877_) throws JsonParseException {
         if (p_14875_.isJsonPrimitive()) {
            return new TextComponent(p_14875_.getAsString());
         } else if (p_14875_.isJsonArray()) {
            JsonArray jsonarray = p_14875_.getAsJsonArray();
            MutableComponent mutablecomponent = null;

            for(JsonElement jsonelement : jsonarray) {
               MutableComponent mutablecomponent1 = this.deserialize(jsonelement, jsonelement.getClass(), p_14877_);
               if (mutablecomponent == null) {
                  mutablecomponent = mutablecomponent1;
               } else {
                  mutablecomponent.append(mutablecomponent1);
               }
            }

            return mutablecomponent;
         } else {
            throw new JsonParseException("Don't know how to turn " + p_14875_ + " into a Component");
         }
      }
   }).create();

   public BlockEntitySignTextStrictJsonFix(Schema p_14864_, boolean p_14865_) {
      super(p_14864_, p_14865_, "BlockEntitySignTextStrictJsonFix", References.BLOCK_ENTITY, "Sign");
   }

   private Dynamic<?> updateLine(Dynamic<?> p_14871_, String p_14872_) {
      String s = p_14871_.get(p_14872_).asString("");
      Component component = null;
      if (!"null".equals(s) && !StringUtils.isEmpty(s)) {
         if (s.charAt(0) == '"' && s.charAt(s.length() - 1) == '"' || s.charAt(0) == '{' && s.charAt(s.length() - 1) == '}') {
            try {
               component = GsonHelper.fromJson(GSON, s, Component.class, true);
               if (component == null) {
                  component = TextComponent.EMPTY;
               }
            } catch (Exception exception2) {
            }

            if (component == null) {
               try {
                  component = Component.Serializer.fromJson(s);
               } catch (Exception exception1) {
               }
            }

            if (component == null) {
               try {
                  component = Component.Serializer.fromJsonLenient(s);
               } catch (Exception exception) {
               }
            }

            if (component == null) {
               component = new TextComponent(s);
            }
         } else {
            component = new TextComponent(s);
         }
      } else {
         component = TextComponent.EMPTY;
      }

      return p_14871_.set(p_14872_, p_14871_.createString(Component.Serializer.toJson(component)));
   }

   protected Typed<?> fix(Typed<?> p_14867_) {
      return p_14867_.update(DSL.remainderFinder(), (p_14869_) -> {
         p_14869_ = this.updateLine(p_14869_, "Text1");
         p_14869_ = this.updateLine(p_14869_, "Text2");
         p_14869_ = this.updateLine(p_14869_, "Text3");
         return this.updateLine(p_14869_, "Text4");
      });
   }
}
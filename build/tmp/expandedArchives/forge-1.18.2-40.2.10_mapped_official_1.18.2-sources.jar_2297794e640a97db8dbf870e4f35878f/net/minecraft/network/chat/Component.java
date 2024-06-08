package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.stream.JsonReader;
import com.mojang.brigadier.Message;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.LowerCaseEnumTypeAdapterFactory;

public interface Component extends Message, FormattedText {
   Style getStyle();

   String getContents();

   default String getString() {
      return FormattedText.super.getString();
   }

   default String getString(int p_130669_) {
      StringBuilder stringbuilder = new StringBuilder();
      this.visit((p_130673_) -> {
         int i = p_130669_ - stringbuilder.length();
         if (i <= 0) {
            return STOP_ITERATION;
         } else {
            stringbuilder.append(p_130673_.length() <= i ? p_130673_ : p_130673_.substring(0, i));
            return Optional.empty();
         }
      });
      return stringbuilder.toString();
   }

   List<Component> getSiblings();

   MutableComponent plainCopy();

   MutableComponent copy();

   FormattedCharSequence getVisualOrderText();

   default <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> p_130679_, Style p_130680_) {
      Style style = this.getStyle().applyTo(p_130680_);
      Optional<T> optional = this.visitSelf(p_130679_, style);
      if (optional.isPresent()) {
         return optional;
      } else {
         for(Component component : this.getSiblings()) {
            Optional<T> optional1 = component.visit(p_130679_, style);
            if (optional1.isPresent()) {
               return optional1;
            }
         }

         return Optional.empty();
      }
   }

   default <T> Optional<T> visit(FormattedText.ContentConsumer<T> p_130677_) {
      Optional<T> optional = this.visitSelf(p_130677_);
      if (optional.isPresent()) {
         return optional;
      } else {
         for(Component component : this.getSiblings()) {
            Optional<T> optional1 = component.visit(p_130677_);
            if (optional1.isPresent()) {
               return optional1;
            }
         }

         return Optional.empty();
      }
   }

   default <T> Optional<T> visitSelf(FormattedText.StyledContentConsumer<T> p_130682_, Style p_130683_) {
      return p_130682_.accept(p_130683_, this.getContents());
   }

   default <T> Optional<T> visitSelf(FormattedText.ContentConsumer<T> p_130681_) {
      return p_130681_.accept(this.getContents());
   }

   default List<Component> toFlatList(Style p_178406_) {
      List<Component> list = Lists.newArrayList();
      this.visit((p_178403_, p_178404_) -> {
         if (!p_178404_.isEmpty()) {
            list.add((new TextComponent(p_178404_)).withStyle(p_178403_));
         }

         return Optional.empty();
      }, p_178406_);
      return list;
   }

   static Component nullToEmpty(@Nullable String p_130675_) {
      return (Component)(p_130675_ != null ? new TextComponent(p_130675_) : TextComponent.EMPTY);
   }

   public static class Serializer implements JsonDeserializer<MutableComponent>, JsonSerializer<Component> {
      private static final Gson GSON = Util.make(() -> {
         GsonBuilder gsonbuilder = new GsonBuilder();
         gsonbuilder.disableHtmlEscaping();
         gsonbuilder.registerTypeHierarchyAdapter(Component.class, new Component.Serializer());
         gsonbuilder.registerTypeHierarchyAdapter(Style.class, new Style.Serializer());
         gsonbuilder.registerTypeAdapterFactory(new LowerCaseEnumTypeAdapterFactory());
         return gsonbuilder.create();
      });
      private static final Field JSON_READER_POS = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("pos");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'pos' for JsonReader", nosuchfieldexception);
         }
      });
      private static final Field JSON_READER_LINESTART = Util.make(() -> {
         try {
            new JsonReader(new StringReader(""));
            Field field = JsonReader.class.getDeclaredField("lineStart");
            field.setAccessible(true);
            return field;
         } catch (NoSuchFieldException nosuchfieldexception) {
            throw new IllegalStateException("Couldn't get field 'lineStart' for JsonReader", nosuchfieldexception);
         }
      });

      public MutableComponent deserialize(JsonElement p_130694_, Type p_130695_, JsonDeserializationContext p_130696_) throws JsonParseException {
         if (p_130694_.isJsonPrimitive()) {
            return new TextComponent(p_130694_.getAsString());
         } else if (!p_130694_.isJsonObject()) {
            if (p_130694_.isJsonArray()) {
               JsonArray jsonarray1 = p_130694_.getAsJsonArray();
               MutableComponent mutablecomponent1 = null;

               for(JsonElement jsonelement : jsonarray1) {
                  MutableComponent mutablecomponent2 = this.deserialize(jsonelement, jsonelement.getClass(), p_130696_);
                  if (mutablecomponent1 == null) {
                     mutablecomponent1 = mutablecomponent2;
                  } else {
                     mutablecomponent1.append(mutablecomponent2);
                  }
               }

               return mutablecomponent1;
            } else {
               throw new JsonParseException("Don't know how to turn " + p_130694_ + " into a Component");
            }
         } else {
            JsonObject jsonobject = p_130694_.getAsJsonObject();
            MutableComponent mutablecomponent;
            if (jsonobject.has("text")) {
               mutablecomponent = new TextComponent(GsonHelper.getAsString(jsonobject, "text"));
            } else if (jsonobject.has("translate")) {
               String s = GsonHelper.getAsString(jsonobject, "translate");
               if (jsonobject.has("with")) {
                  JsonArray jsonarray = GsonHelper.getAsJsonArray(jsonobject, "with");
                  Object[] aobject = new Object[jsonarray.size()];

                  for(int i = 0; i < aobject.length; ++i) {
                     aobject[i] = this.deserialize(jsonarray.get(i), p_130695_, p_130696_);
                     if (aobject[i] instanceof TextComponent) {
                        TextComponent textcomponent = (TextComponent)aobject[i];
                        if (textcomponent.getStyle().isEmpty() && textcomponent.getSiblings().isEmpty()) {
                           aobject[i] = textcomponent.getText();
                        }
                     }
                  }

                  mutablecomponent = new TranslatableComponent(s, aobject);
               } else {
                  mutablecomponent = new TranslatableComponent(s);
               }
            } else if (jsonobject.has("score")) {
               JsonObject jsonobject1 = GsonHelper.getAsJsonObject(jsonobject, "score");
               if (!jsonobject1.has("name") || !jsonobject1.has("objective")) {
                  throw new JsonParseException("A score component needs a least a name and an objective");
               }

               mutablecomponent = new ScoreComponent(GsonHelper.getAsString(jsonobject1, "name"), GsonHelper.getAsString(jsonobject1, "objective"));
            } else if (jsonobject.has("selector")) {
               Optional<Component> optional = this.parseSeparator(p_130695_, p_130696_, jsonobject);
               mutablecomponent = new SelectorComponent(GsonHelper.getAsString(jsonobject, "selector"), optional);
            } else if (jsonobject.has("keybind")) {
               mutablecomponent = new KeybindComponent(GsonHelper.getAsString(jsonobject, "keybind"));
            } else {
               if (!jsonobject.has("nbt")) {
                  throw new JsonParseException("Don't know how to turn " + p_130694_ + " into a Component");
               }

               String s1 = GsonHelper.getAsString(jsonobject, "nbt");
               Optional<Component> optional1 = this.parseSeparator(p_130695_, p_130696_, jsonobject);
               boolean flag = GsonHelper.getAsBoolean(jsonobject, "interpret", false);
               if (jsonobject.has("block")) {
                  mutablecomponent = new NbtComponent.BlockNbtComponent(s1, flag, GsonHelper.getAsString(jsonobject, "block"), optional1);
               } else if (jsonobject.has("entity")) {
                  mutablecomponent = new NbtComponent.EntityNbtComponent(s1, flag, GsonHelper.getAsString(jsonobject, "entity"), optional1);
               } else {
                  if (!jsonobject.has("storage")) {
                     throw new JsonParseException("Don't know how to turn " + p_130694_ + " into a Component");
                  }

                  mutablecomponent = new NbtComponent.StorageNbtComponent(s1, flag, new ResourceLocation(GsonHelper.getAsString(jsonobject, "storage")), optional1);
               }
            }

            if (jsonobject.has("extra")) {
               JsonArray jsonarray2 = GsonHelper.getAsJsonArray(jsonobject, "extra");
               if (jsonarray2.size() <= 0) {
                  throw new JsonParseException("Unexpected empty array of components");
               }

               for(int j = 0; j < jsonarray2.size(); ++j) {
                  mutablecomponent.append(this.deserialize(jsonarray2.get(j), p_130695_, p_130696_));
               }
            }

            mutablecomponent.setStyle(p_130696_.deserialize(p_130694_, Style.class));
            return mutablecomponent;
         }
      }

      private Optional<Component> parseSeparator(Type p_178416_, JsonDeserializationContext p_178417_, JsonObject p_178418_) {
         return p_178418_.has("separator") ? Optional.of(this.deserialize(p_178418_.get("separator"), p_178416_, p_178417_)) : Optional.empty();
      }

      private void serializeStyle(Style p_130710_, JsonObject p_130711_, JsonSerializationContext p_130712_) {
         JsonElement jsonelement = p_130712_.serialize(p_130710_);
         if (jsonelement.isJsonObject()) {
            JsonObject jsonobject = (JsonObject)jsonelement;

            for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
               p_130711_.add(entry.getKey(), entry.getValue());
            }
         }

      }

      public JsonElement serialize(Component p_130706_, Type p_130707_, JsonSerializationContext p_130708_) {
         JsonObject jsonobject = new JsonObject();
         if (!p_130706_.getStyle().isEmpty()) {
            this.serializeStyle(p_130706_.getStyle(), jsonobject, p_130708_);
         }

         if (!p_130706_.getSiblings().isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(Component component : p_130706_.getSiblings()) {
               jsonarray.add(this.serialize(component, component.getClass(), p_130708_));
            }

            jsonobject.add("extra", jsonarray);
         }

         if (p_130706_ instanceof TextComponent) {
            jsonobject.addProperty("text", ((TextComponent)p_130706_).getText());
         } else if (p_130706_ instanceof TranslatableComponent) {
            TranslatableComponent translatablecomponent = (TranslatableComponent)p_130706_;
            jsonobject.addProperty("translate", translatablecomponent.getKey());
            if (translatablecomponent.getArgs() != null && translatablecomponent.getArgs().length > 0) {
               JsonArray jsonarray1 = new JsonArray();

               for(Object object : translatablecomponent.getArgs()) {
                  if (object instanceof Component) {
                     jsonarray1.add(this.serialize((Component)object, object.getClass(), p_130708_));
                  } else {
                     jsonarray1.add(new JsonPrimitive(String.valueOf(object)));
                  }
               }

               jsonobject.add("with", jsonarray1);
            }
         } else if (p_130706_ instanceof ScoreComponent) {
            ScoreComponent scorecomponent = (ScoreComponent)p_130706_;
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.addProperty("name", scorecomponent.getName());
            jsonobject1.addProperty("objective", scorecomponent.getObjective());
            jsonobject.add("score", jsonobject1);
         } else if (p_130706_ instanceof SelectorComponent) {
            SelectorComponent selectorcomponent = (SelectorComponent)p_130706_;
            jsonobject.addProperty("selector", selectorcomponent.getPattern());
            this.serializeSeparator(p_130708_, jsonobject, selectorcomponent.getSeparator());
         } else if (p_130706_ instanceof KeybindComponent) {
            KeybindComponent keybindcomponent = (KeybindComponent)p_130706_;
            jsonobject.addProperty("keybind", keybindcomponent.getName());
         } else {
            if (!(p_130706_ instanceof NbtComponent)) {
               throw new IllegalArgumentException("Don't know how to serialize " + p_130706_ + " as a Component");
            }

            NbtComponent nbtcomponent = (NbtComponent)p_130706_;
            jsonobject.addProperty("nbt", nbtcomponent.getNbtPath());
            jsonobject.addProperty("interpret", nbtcomponent.isInterpreting());
            this.serializeSeparator(p_130708_, jsonobject, nbtcomponent.separator);
            if (p_130706_ instanceof NbtComponent.BlockNbtComponent) {
               NbtComponent.BlockNbtComponent nbtcomponent$blocknbtcomponent = (NbtComponent.BlockNbtComponent)p_130706_;
               jsonobject.addProperty("block", nbtcomponent$blocknbtcomponent.getPos());
            } else if (p_130706_ instanceof NbtComponent.EntityNbtComponent) {
               NbtComponent.EntityNbtComponent nbtcomponent$entitynbtcomponent = (NbtComponent.EntityNbtComponent)p_130706_;
               jsonobject.addProperty("entity", nbtcomponent$entitynbtcomponent.getSelector());
            } else {
               if (!(p_130706_ instanceof NbtComponent.StorageNbtComponent)) {
                  throw new IllegalArgumentException("Don't know how to serialize " + p_130706_ + " as a Component");
               }

               NbtComponent.StorageNbtComponent nbtcomponent$storagenbtcomponent = (NbtComponent.StorageNbtComponent)p_130706_;
               jsonobject.addProperty("storage", nbtcomponent$storagenbtcomponent.getId().toString());
            }
         }

         return jsonobject;
      }

      private void serializeSeparator(JsonSerializationContext p_178412_, JsonObject p_178413_, Optional<Component> p_178414_) {
         p_178414_.ifPresent((p_178410_) -> {
            p_178413_.add("separator", this.serialize(p_178410_, p_178410_.getClass(), p_178412_));
         });
      }

      public static String toJson(Component p_130704_) {
         return GSON.toJson(p_130704_);
      }

      public static JsonElement toJsonTree(Component p_130717_) {
         return GSON.toJsonTree(p_130717_);
      }

      @Nullable
      public static MutableComponent fromJson(String p_130702_) {
         return GsonHelper.fromJson(GSON, p_130702_, MutableComponent.class, false);
      }

      @Nullable
      public static MutableComponent fromJson(JsonElement p_130692_) {
         return GSON.fromJson(p_130692_, MutableComponent.class);
      }

      @Nullable
      public static MutableComponent fromJsonLenient(String p_130715_) {
         return GsonHelper.fromJson(GSON, p_130715_, MutableComponent.class, true);
      }

      public static MutableComponent fromJson(com.mojang.brigadier.StringReader p_130700_) {
         try {
            JsonReader jsonreader = new JsonReader(new StringReader(p_130700_.getRemaining()));
            jsonreader.setLenient(false);
            MutableComponent mutablecomponent = GSON.getAdapter(MutableComponent.class).read(jsonreader);
            p_130700_.setCursor(p_130700_.getCursor() + getPos(jsonreader));
            return mutablecomponent;
         } catch (StackOverflowError | IOException ioexception) {
            throw new JsonParseException(ioexception);
         }
      }

      private static int getPos(JsonReader p_130698_) {
         try {
            return JSON_READER_POS.getInt(p_130698_) - JSON_READER_LINESTART.getInt(p_130698_) + 1;
         } catch (IllegalAccessException illegalaccessexception) {
            throw new IllegalStateException("Couldn't read position of JsonReader", illegalaccessexception);
         }
      }
   }
}
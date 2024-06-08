package net.minecraft.network.chat;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;

public class Style {
   public static final Style EMPTY = new Style((TextColor)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (Boolean)null, (ClickEvent)null, (HoverEvent)null, (String)null, (ResourceLocation)null);
   public static final ResourceLocation DEFAULT_FONT = new ResourceLocation("minecraft", "default");
   @Nullable
   final TextColor color;
   @Nullable
   final Boolean bold;
   @Nullable
   final Boolean italic;
   @Nullable
   final Boolean underlined;
   @Nullable
   final Boolean strikethrough;
   @Nullable
   final Boolean obfuscated;
   @Nullable
   final ClickEvent clickEvent;
   @Nullable
   final HoverEvent hoverEvent;
   @Nullable
   final String insertion;
   @Nullable
   final ResourceLocation font;

   Style(@Nullable TextColor p_131113_, @Nullable Boolean p_131114_, @Nullable Boolean p_131115_, @Nullable Boolean p_131116_, @Nullable Boolean p_131117_, @Nullable Boolean p_131118_, @Nullable ClickEvent p_131119_, @Nullable HoverEvent p_131120_, @Nullable String p_131121_, @Nullable ResourceLocation p_131122_) {
      this.color = p_131113_;
      this.bold = p_131114_;
      this.italic = p_131115_;
      this.underlined = p_131116_;
      this.strikethrough = p_131117_;
      this.obfuscated = p_131118_;
      this.clickEvent = p_131119_;
      this.hoverEvent = p_131120_;
      this.insertion = p_131121_;
      this.font = p_131122_;
   }

   @Nullable
   public TextColor getColor() {
      return this.color;
   }

   public boolean isBold() {
      return this.bold == Boolean.TRUE;
   }

   public boolean isItalic() {
      return this.italic == Boolean.TRUE;
   }

   public boolean isStrikethrough() {
      return this.strikethrough == Boolean.TRUE;
   }

   public boolean isUnderlined() {
      return this.underlined == Boolean.TRUE;
   }

   public boolean isObfuscated() {
      return this.obfuscated == Boolean.TRUE;
   }

   public boolean isEmpty() {
      return this == EMPTY;
   }

   @Nullable
   public ClickEvent getClickEvent() {
      return this.clickEvent;
   }

   @Nullable
   public HoverEvent getHoverEvent() {
      return this.hoverEvent;
   }

   @Nullable
   public String getInsertion() {
      return this.insertion;
   }

   public ResourceLocation getFont() {
      return this.font != null ? this.font : DEFAULT_FONT;
   }

   public Style withColor(@Nullable TextColor p_131149_) {
      return new Style(p_131149_, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withColor(@Nullable ChatFormatting p_131141_) {
      return this.withColor(p_131141_ != null ? TextColor.fromLegacyFormat(p_131141_) : null);
   }

   public Style withColor(int p_178521_) {
      return this.withColor(TextColor.fromRgb(p_178521_));
   }

   public Style withBold(@Nullable Boolean p_131137_) {
      return new Style(this.color, p_131137_, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withItalic(@Nullable Boolean p_131156_) {
      return new Style(this.color, this.bold, p_131156_, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withUnderlined(@Nullable Boolean p_131163_) {
      return new Style(this.color, this.bold, this.italic, p_131163_, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withStrikethrough(@Nullable Boolean p_178523_) {
      return new Style(this.color, this.bold, this.italic, this.underlined, p_178523_, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withObfuscated(@Nullable Boolean p_178525_) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, p_178525_, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   /**
    * @deprecated Use {@link #withUnderlined(Boolean)} instead.
    */
   @Deprecated(forRemoval = true, since = "1.18.2")
   public Style setUnderlined(@Nullable Boolean underlined) {
      return new Style(this.color, this.bold, this.italic, underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   /**
    * @deprecated Use {@link #withStrikethrough(Boolean)} instead.
    */
   @Deprecated(forRemoval = true, since = "1.18.2")
   public Style setStrikethrough(@Nullable Boolean strikethrough) {
      return new Style(this.color, this.bold, this.italic, this.underlined, strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   /**
    * @deprecated Use {@link #withObfuscated(Boolean)} instead.
    */
   @Deprecated(forRemoval = true, since = "1.18.2")
   public Style setObfuscated(@Nullable Boolean obfuscated) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, obfuscated, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style withClickEvent(@Nullable ClickEvent p_131143_) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, p_131143_, this.hoverEvent, this.insertion, this.font);
   }

   public Style withHoverEvent(@Nullable HoverEvent p_131145_) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, p_131145_, this.insertion, this.font);
   }

   public Style withInsertion(@Nullable String p_131139_) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, p_131139_, this.font);
   }

   public Style withFont(@Nullable ResourceLocation p_131151_) {
      return new Style(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion, p_131151_);
   }

   public Style applyFormat(ChatFormatting p_131158_) {
      TextColor textcolor = this.color;
      Boolean obool = this.bold;
      Boolean obool1 = this.italic;
      Boolean obool2 = this.strikethrough;
      Boolean obool3 = this.underlined;
      Boolean obool4 = this.obfuscated;
      switch(p_131158_) {
      case OBFUSCATED:
         obool4 = true;
         break;
      case BOLD:
         obool = true;
         break;
      case STRIKETHROUGH:
         obool2 = true;
         break;
      case UNDERLINE:
         obool3 = true;
         break;
      case ITALIC:
         obool1 = true;
         break;
      case RESET:
         return EMPTY;
      default:
         textcolor = TextColor.fromLegacyFormat(p_131158_);
      }

      return new Style(textcolor, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyLegacyFormat(ChatFormatting p_131165_) {
      TextColor textcolor = this.color;
      Boolean obool = this.bold;
      Boolean obool1 = this.italic;
      Boolean obool2 = this.strikethrough;
      Boolean obool3 = this.underlined;
      Boolean obool4 = this.obfuscated;
      switch(p_131165_) {
      case OBFUSCATED:
         obool4 = true;
         break;
      case BOLD:
         obool = true;
         break;
      case STRIKETHROUGH:
         obool2 = true;
         break;
      case UNDERLINE:
         obool3 = true;
         break;
      case ITALIC:
         obool1 = true;
         break;
      case RESET:
         return EMPTY;
      default:
         obool4 = false;
         obool = false;
         obool2 = false;
         obool3 = false;
         obool1 = false;
         textcolor = TextColor.fromLegacyFormat(p_131165_);
      }

      return new Style(textcolor, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyFormats(ChatFormatting... p_131153_) {
      TextColor textcolor = this.color;
      Boolean obool = this.bold;
      Boolean obool1 = this.italic;
      Boolean obool2 = this.strikethrough;
      Boolean obool3 = this.underlined;
      Boolean obool4 = this.obfuscated;

      for(ChatFormatting chatformatting : p_131153_) {
         switch(chatformatting) {
         case OBFUSCATED:
            obool4 = true;
            break;
         case BOLD:
            obool = true;
            break;
         case STRIKETHROUGH:
            obool2 = true;
            break;
         case UNDERLINE:
            obool3 = true;
            break;
         case ITALIC:
            obool1 = true;
            break;
         case RESET:
            return EMPTY;
         default:
            textcolor = TextColor.fromLegacyFormat(chatformatting);
         }
      }

      return new Style(textcolor, obool, obool1, obool3, obool2, obool4, this.clickEvent, this.hoverEvent, this.insertion, this.font);
   }

   public Style applyTo(Style p_131147_) {
      if (this == EMPTY) {
         return p_131147_;
      } else {
         return p_131147_ == EMPTY ? this : new Style(this.color != null ? this.color : p_131147_.color, this.bold != null ? this.bold : p_131147_.bold, this.italic != null ? this.italic : p_131147_.italic, this.underlined != null ? this.underlined : p_131147_.underlined, this.strikethrough != null ? this.strikethrough : p_131147_.strikethrough, this.obfuscated != null ? this.obfuscated : p_131147_.obfuscated, this.clickEvent != null ? this.clickEvent : p_131147_.clickEvent, this.hoverEvent != null ? this.hoverEvent : p_131147_.hoverEvent, this.insertion != null ? this.insertion : p_131147_.insertion, this.font != null ? this.font : p_131147_.font);
      }
   }

   public String toString() {
      return "Style{ color=" + this.color + ", bold=" + this.bold + ", italic=" + this.italic + ", underlined=" + this.underlined + ", strikethrough=" + this.strikethrough + ", obfuscated=" + this.obfuscated + ", clickEvent=" + this.getClickEvent() + ", hoverEvent=" + this.getHoverEvent() + ", insertion=" + this.getInsertion() + ", font=" + this.getFont() + "}";
   }

   public boolean equals(Object p_131175_) {
      if (this == p_131175_) {
         return true;
      } else if (!(p_131175_ instanceof Style)) {
         return false;
      } else {
         Style style = (Style)p_131175_;
         return this.isBold() == style.isBold() && Objects.equals(this.getColor(), style.getColor()) && this.isItalic() == style.isItalic() && this.isObfuscated() == style.isObfuscated() && this.isStrikethrough() == style.isStrikethrough() && this.isUnderlined() == style.isUnderlined() && Objects.equals(this.getClickEvent(), style.getClickEvent()) && Objects.equals(this.getHoverEvent(), style.getHoverEvent()) && Objects.equals(this.getInsertion(), style.getInsertion()) && Objects.equals(this.getFont(), style.getFont());
      }
   }

   public int hashCode() {
      return Objects.hash(this.color, this.bold, this.italic, this.underlined, this.strikethrough, this.obfuscated, this.clickEvent, this.hoverEvent, this.insertion);
   }

   public static class Serializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
      @Nullable
      public Style deserialize(JsonElement p_131200_, Type p_131201_, JsonDeserializationContext p_131202_) throws JsonParseException {
         if (p_131200_.isJsonObject()) {
            JsonObject jsonobject = p_131200_.getAsJsonObject();
            if (jsonobject == null) {
               return null;
            } else {
               Boolean obool = getOptionalFlag(jsonobject, "bold");
               Boolean obool1 = getOptionalFlag(jsonobject, "italic");
               Boolean obool2 = getOptionalFlag(jsonobject, "underlined");
               Boolean obool3 = getOptionalFlag(jsonobject, "strikethrough");
               Boolean obool4 = getOptionalFlag(jsonobject, "obfuscated");
               TextColor textcolor = getTextColor(jsonobject);
               String s = getInsertion(jsonobject);
               ClickEvent clickevent = getClickEvent(jsonobject);
               HoverEvent hoverevent = getHoverEvent(jsonobject);
               ResourceLocation resourcelocation = getFont(jsonobject);
               return new Style(textcolor, obool, obool1, obool2, obool3, obool4, clickevent, hoverevent, s, resourcelocation);
            }
         } else {
            return null;
         }
      }

      @Nullable
      private static ResourceLocation getFont(JsonObject p_131204_) {
         if (p_131204_.has("font")) {
            String s = GsonHelper.getAsString(p_131204_, "font");

            try {
               return new ResourceLocation(s);
            } catch (ResourceLocationException resourcelocationexception) {
               throw new JsonSyntaxException("Invalid font name: " + s);
            }
         } else {
            return null;
         }
      }

      @Nullable
      private static HoverEvent getHoverEvent(JsonObject p_131213_) {
         if (p_131213_.has("hoverEvent")) {
            JsonObject jsonobject = GsonHelper.getAsJsonObject(p_131213_, "hoverEvent");
            HoverEvent hoverevent = HoverEvent.deserialize(jsonobject);
            if (hoverevent != null && hoverevent.getAction().isAllowedFromServer()) {
               return hoverevent;
            }
         }

         return null;
      }

      @Nullable
      private static ClickEvent getClickEvent(JsonObject p_131215_) {
         if (p_131215_.has("clickEvent")) {
            JsonObject jsonobject = GsonHelper.getAsJsonObject(p_131215_, "clickEvent");
            String s = GsonHelper.getAsString(jsonobject, "action", (String)null);
            ClickEvent.Action clickevent$action = s == null ? null : ClickEvent.Action.getByName(s);
            String s1 = GsonHelper.getAsString(jsonobject, "value", (String)null);
            if (clickevent$action != null && s1 != null && clickevent$action.isAllowedFromServer()) {
               return new ClickEvent(clickevent$action, s1);
            }
         }

         return null;
      }

      @Nullable
      private static String getInsertion(JsonObject p_131217_) {
         return GsonHelper.getAsString(p_131217_, "insertion", (String)null);
      }

      @Nullable
      private static TextColor getTextColor(JsonObject p_131223_) {
         if (p_131223_.has("color")) {
            String s = GsonHelper.getAsString(p_131223_, "color");
            return TextColor.parseColor(s);
         } else {
            return null;
         }
      }

      @Nullable
      private static Boolean getOptionalFlag(JsonObject p_131206_, String p_131207_) {
         return p_131206_.has(p_131207_) ? p_131206_.get(p_131207_).getAsBoolean() : null;
      }

      @Nullable
      public JsonElement serialize(Style p_131209_, Type p_131210_, JsonSerializationContext p_131211_) {
         if (p_131209_.isEmpty()) {
            return null;
         } else {
            JsonObject jsonobject = new JsonObject();
            if (p_131209_.bold != null) {
               jsonobject.addProperty("bold", p_131209_.bold);
            }

            if (p_131209_.italic != null) {
               jsonobject.addProperty("italic", p_131209_.italic);
            }

            if (p_131209_.underlined != null) {
               jsonobject.addProperty("underlined", p_131209_.underlined);
            }

            if (p_131209_.strikethrough != null) {
               jsonobject.addProperty("strikethrough", p_131209_.strikethrough);
            }

            if (p_131209_.obfuscated != null) {
               jsonobject.addProperty("obfuscated", p_131209_.obfuscated);
            }

            if (p_131209_.color != null) {
               jsonobject.addProperty("color", p_131209_.color.serialize());
            }

            if (p_131209_.insertion != null) {
               jsonobject.add("insertion", p_131211_.serialize(p_131209_.insertion));
            }

            if (p_131209_.clickEvent != null) {
               JsonObject jsonobject1 = new JsonObject();
               jsonobject1.addProperty("action", p_131209_.clickEvent.getAction().getName());
               jsonobject1.addProperty("value", p_131209_.clickEvent.getValue());
               jsonobject.add("clickEvent", jsonobject1);
            }

            if (p_131209_.hoverEvent != null) {
               jsonobject.add("hoverEvent", p_131209_.hoverEvent.serialize());
            }

            if (p_131209_.font != null) {
               jsonobject.addProperty("font", p_131209_.font.toString());
            }

            return jsonobject;
         }
      }
   }
}

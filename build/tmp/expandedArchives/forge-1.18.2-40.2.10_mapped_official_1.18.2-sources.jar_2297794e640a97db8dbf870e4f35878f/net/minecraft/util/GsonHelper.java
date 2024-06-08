package net.minecraft.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;

public class GsonHelper {
   private static final Gson GSON = (new GsonBuilder()).create();

   public static boolean isStringValue(JsonObject p_13814_, String p_13815_) {
      return !isValidPrimitive(p_13814_, p_13815_) ? false : p_13814_.getAsJsonPrimitive(p_13815_).isString();
   }

   public static boolean isStringValue(JsonElement p_13804_) {
      return !p_13804_.isJsonPrimitive() ? false : p_13804_.getAsJsonPrimitive().isString();
   }

   public static boolean isNumberValue(JsonObject p_144763_, String p_144764_) {
      return !isValidPrimitive(p_144763_, p_144764_) ? false : p_144763_.getAsJsonPrimitive(p_144764_).isNumber();
   }

   public static boolean isNumberValue(JsonElement p_13873_) {
      return !p_13873_.isJsonPrimitive() ? false : p_13873_.getAsJsonPrimitive().isNumber();
   }

   public static boolean isBooleanValue(JsonObject p_13881_, String p_13882_) {
      return !isValidPrimitive(p_13881_, p_13882_) ? false : p_13881_.getAsJsonPrimitive(p_13882_).isBoolean();
   }

   public static boolean isBooleanValue(JsonElement p_144768_) {
      return !p_144768_.isJsonPrimitive() ? false : p_144768_.getAsJsonPrimitive().isBoolean();
   }

   public static boolean isArrayNode(JsonObject p_13886_, String p_13887_) {
      return !isValidNode(p_13886_, p_13887_) ? false : p_13886_.get(p_13887_).isJsonArray();
   }

   public static boolean isObjectNode(JsonObject p_144773_, String p_144774_) {
      return !isValidNode(p_144773_, p_144774_) ? false : p_144773_.get(p_144774_).isJsonObject();
   }

   public static boolean isValidPrimitive(JsonObject p_13895_, String p_13896_) {
      return !isValidNode(p_13895_, p_13896_) ? false : p_13895_.get(p_13896_).isJsonPrimitive();
   }

   public static boolean isValidNode(JsonObject p_13901_, String p_13902_) {
      if (p_13901_ == null) {
         return false;
      } else {
         return p_13901_.get(p_13902_) != null;
      }
   }

   public static String convertToString(JsonElement p_13806_, String p_13807_) {
      if (p_13806_.isJsonPrimitive()) {
         return p_13806_.getAsString();
      } else {
         throw new JsonSyntaxException("Expected " + p_13807_ + " to be a string, was " + getType(p_13806_));
      }
   }

   public static String getAsString(JsonObject p_13907_, String p_13908_) {
      if (p_13907_.has(p_13908_)) {
         return convertToString(p_13907_.get(p_13908_), p_13908_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13908_ + ", expected to find a string");
      }
   }

   @Nullable
   @Contract("_,_,!null->!null;_,_,null->_")
   public static String getAsString(JsonObject p_13852_, String p_13853_, @Nullable String p_13854_) {
      return p_13852_.has(p_13853_) ? convertToString(p_13852_.get(p_13853_), p_13853_) : p_13854_;
   }

   public static Item convertToItem(JsonElement p_13875_, String p_13876_) {
      if (p_13875_.isJsonPrimitive()) {
         String s = p_13875_.getAsString();
         return Registry.ITEM.getOptional(new ResourceLocation(s)).orElseThrow(() -> {
            return new JsonSyntaxException("Expected " + p_13876_ + " to be an item, was unknown string '" + s + "'");
         });
      } else {
         throw new JsonSyntaxException("Expected " + p_13876_ + " to be an item, was " + getType(p_13875_));
      }
   }

   public static Item getAsItem(JsonObject p_13910_, String p_13911_) {
      if (p_13910_.has(p_13911_)) {
         return convertToItem(p_13910_.get(p_13911_), p_13911_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13911_ + ", expected to find an item");
      }
   }

   @Nullable
   @Contract("_,_,!null->!null;_,_,null->_")
   public static Item getAsItem(JsonObject p_144747_, String p_144748_, @Nullable Item p_144749_) {
      return p_144747_.has(p_144748_) ? convertToItem(p_144747_.get(p_144748_), p_144748_) : p_144749_;
   }

   public static boolean convertToBoolean(JsonElement p_13878_, String p_13879_) {
      if (p_13878_.isJsonPrimitive()) {
         return p_13878_.getAsBoolean();
      } else {
         throw new JsonSyntaxException("Expected " + p_13879_ + " to be a Boolean, was " + getType(p_13878_));
      }
   }

   public static boolean getAsBoolean(JsonObject p_13913_, String p_13914_) {
      if (p_13913_.has(p_13914_)) {
         return convertToBoolean(p_13913_.get(p_13914_), p_13914_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13914_ + ", expected to find a Boolean");
      }
   }

   public static boolean getAsBoolean(JsonObject p_13856_, String p_13857_, boolean p_13858_) {
      return p_13856_.has(p_13857_) ? convertToBoolean(p_13856_.get(p_13857_), p_13857_) : p_13858_;
   }

   public static double convertToDouble(JsonElement p_144770_, String p_144771_) {
      if (p_144770_.isJsonPrimitive() && p_144770_.getAsJsonPrimitive().isNumber()) {
         return p_144770_.getAsDouble();
      } else {
         throw new JsonSyntaxException("Expected " + p_144771_ + " to be a Double, was " + getType(p_144770_));
      }
   }

   public static double getAsDouble(JsonObject p_144785_, String p_144786_) {
      if (p_144785_.has(p_144786_)) {
         return convertToDouble(p_144785_.get(p_144786_), p_144786_);
      } else {
         throw new JsonSyntaxException("Missing " + p_144786_ + ", expected to find a Double");
      }
   }

   public static double getAsDouble(JsonObject p_144743_, String p_144744_, double p_144745_) {
      return p_144743_.has(p_144744_) ? convertToDouble(p_144743_.get(p_144744_), p_144744_) : p_144745_;
   }

   public static float convertToFloat(JsonElement p_13889_, String p_13890_) {
      if (p_13889_.isJsonPrimitive() && p_13889_.getAsJsonPrimitive().isNumber()) {
         return p_13889_.getAsFloat();
      } else {
         throw new JsonSyntaxException("Expected " + p_13890_ + " to be a Float, was " + getType(p_13889_));
      }
   }

   public static float getAsFloat(JsonObject p_13916_, String p_13917_) {
      if (p_13916_.has(p_13917_)) {
         return convertToFloat(p_13916_.get(p_13917_), p_13917_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13917_ + ", expected to find a Float");
      }
   }

   public static float getAsFloat(JsonObject p_13821_, String p_13822_, float p_13823_) {
      return p_13821_.has(p_13822_) ? convertToFloat(p_13821_.get(p_13822_), p_13822_) : p_13823_;
   }

   public static long convertToLong(JsonElement p_13892_, String p_13893_) {
      if (p_13892_.isJsonPrimitive() && p_13892_.getAsJsonPrimitive().isNumber()) {
         return p_13892_.getAsLong();
      } else {
         throw new JsonSyntaxException("Expected " + p_13893_ + " to be a Long, was " + getType(p_13892_));
      }
   }

   public static long getAsLong(JsonObject p_13922_, String p_13923_) {
      if (p_13922_.has(p_13923_)) {
         return convertToLong(p_13922_.get(p_13923_), p_13923_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13923_ + ", expected to find a Long");
      }
   }

   public static long getAsLong(JsonObject p_13829_, String p_13830_, long p_13831_) {
      return p_13829_.has(p_13830_) ? convertToLong(p_13829_.get(p_13830_), p_13830_) : p_13831_;
   }

   public static int convertToInt(JsonElement p_13898_, String p_13899_) {
      if (p_13898_.isJsonPrimitive() && p_13898_.getAsJsonPrimitive().isNumber()) {
         return p_13898_.getAsInt();
      } else {
         throw new JsonSyntaxException("Expected " + p_13899_ + " to be a Int, was " + getType(p_13898_));
      }
   }

   public static int getAsInt(JsonObject p_13928_, String p_13929_) {
      if (p_13928_.has(p_13929_)) {
         return convertToInt(p_13928_.get(p_13929_), p_13929_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13929_ + ", expected to find a Int");
      }
   }

   public static int getAsInt(JsonObject p_13825_, String p_13826_, int p_13827_) {
      return p_13825_.has(p_13826_) ? convertToInt(p_13825_.get(p_13826_), p_13826_) : p_13827_;
   }

   public static byte convertToByte(JsonElement p_13904_, String p_13905_) {
      if (p_13904_.isJsonPrimitive() && p_13904_.getAsJsonPrimitive().isNumber()) {
         return p_13904_.getAsByte();
      } else {
         throw new JsonSyntaxException("Expected " + p_13905_ + " to be a Byte, was " + getType(p_13904_));
      }
   }

   public static byte getAsByte(JsonObject p_144791_, String p_144792_) {
      if (p_144791_.has(p_144792_)) {
         return convertToByte(p_144791_.get(p_144792_), p_144792_);
      } else {
         throw new JsonSyntaxException("Missing " + p_144792_ + ", expected to find a Byte");
      }
   }

   public static byte getAsByte(JsonObject p_13817_, String p_13818_, byte p_13819_) {
      return p_13817_.has(p_13818_) ? convertToByte(p_13817_.get(p_13818_), p_13818_) : p_13819_;
   }

   public static char convertToCharacter(JsonElement p_144776_, String p_144777_) {
      if (p_144776_.isJsonPrimitive() && p_144776_.getAsJsonPrimitive().isNumber()) {
         return p_144776_.getAsCharacter();
      } else {
         throw new JsonSyntaxException("Expected " + p_144777_ + " to be a Character, was " + getType(p_144776_));
      }
   }

   public static char getAsCharacter(JsonObject p_144794_, String p_144795_) {
      if (p_144794_.has(p_144795_)) {
         return convertToCharacter(p_144794_.get(p_144795_), p_144795_);
      } else {
         throw new JsonSyntaxException("Missing " + p_144795_ + ", expected to find a Character");
      }
   }

   public static char getAsCharacter(JsonObject p_144739_, String p_144740_, char p_144741_) {
      return p_144739_.has(p_144740_) ? convertToCharacter(p_144739_.get(p_144740_), p_144740_) : p_144741_;
   }

   public static BigDecimal convertToBigDecimal(JsonElement p_144779_, String p_144780_) {
      if (p_144779_.isJsonPrimitive() && p_144779_.getAsJsonPrimitive().isNumber()) {
         return p_144779_.getAsBigDecimal();
      } else {
         throw new JsonSyntaxException("Expected " + p_144780_ + " to be a BigDecimal, was " + getType(p_144779_));
      }
   }

   public static BigDecimal getAsBigDecimal(JsonObject p_144797_, String p_144798_) {
      if (p_144797_.has(p_144798_)) {
         return convertToBigDecimal(p_144797_.get(p_144798_), p_144798_);
      } else {
         throw new JsonSyntaxException("Missing " + p_144798_ + ", expected to find a BigDecimal");
      }
   }

   public static BigDecimal getAsBigDecimal(JsonObject p_144751_, String p_144752_, BigDecimal p_144753_) {
      return p_144751_.has(p_144752_) ? convertToBigDecimal(p_144751_.get(p_144752_), p_144752_) : p_144753_;
   }

   public static BigInteger convertToBigInteger(JsonElement p_144782_, String p_144783_) {
      if (p_144782_.isJsonPrimitive() && p_144782_.getAsJsonPrimitive().isNumber()) {
         return p_144782_.getAsBigInteger();
      } else {
         throw new JsonSyntaxException("Expected " + p_144783_ + " to be a BigInteger, was " + getType(p_144782_));
      }
   }

   public static BigInteger getAsBigInteger(JsonObject p_144800_, String p_144801_) {
      if (p_144800_.has(p_144801_)) {
         return convertToBigInteger(p_144800_.get(p_144801_), p_144801_);
      } else {
         throw new JsonSyntaxException("Missing " + p_144801_ + ", expected to find a BigInteger");
      }
   }

   public static BigInteger getAsBigInteger(JsonObject p_144755_, String p_144756_, BigInteger p_144757_) {
      return p_144755_.has(p_144756_) ? convertToBigInteger(p_144755_.get(p_144756_), p_144756_) : p_144757_;
   }

   public static short convertToShort(JsonElement p_144788_, String p_144789_) {
      if (p_144788_.isJsonPrimitive() && p_144788_.getAsJsonPrimitive().isNumber()) {
         return p_144788_.getAsShort();
      } else {
         throw new JsonSyntaxException("Expected " + p_144789_ + " to be a Short, was " + getType(p_144788_));
      }
   }

   public static short getAsShort(JsonObject p_144803_, String p_144804_) {
      if (p_144803_.has(p_144804_)) {
         return convertToShort(p_144803_.get(p_144804_), p_144804_);
      } else {
         throw new JsonSyntaxException("Missing " + p_144804_ + ", expected to find a Short");
      }
   }

   public static short getAsShort(JsonObject p_144759_, String p_144760_, short p_144761_) {
      return p_144759_.has(p_144760_) ? convertToShort(p_144759_.get(p_144760_), p_144760_) : p_144761_;
   }

   public static JsonObject convertToJsonObject(JsonElement p_13919_, String p_13920_) {
      if (p_13919_.isJsonObject()) {
         return p_13919_.getAsJsonObject();
      } else {
         throw new JsonSyntaxException("Expected " + p_13920_ + " to be a JsonObject, was " + getType(p_13919_));
      }
   }

   public static JsonObject getAsJsonObject(JsonObject p_13931_, String p_13932_) {
      if (p_13931_.has(p_13932_)) {
         return convertToJsonObject(p_13931_.get(p_13932_), p_13932_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13932_ + ", expected to find a JsonObject");
      }
   }

   @Nullable
   @Contract("_,_,!null->!null;_,_,null->_")
   public static JsonObject getAsJsonObject(JsonObject p_13842_, String p_13843_, @Nullable JsonObject p_13844_) {
      return p_13842_.has(p_13843_) ? convertToJsonObject(p_13842_.get(p_13843_), p_13843_) : p_13844_;
   }

   public static JsonArray convertToJsonArray(JsonElement p_13925_, String p_13926_) {
      if (p_13925_.isJsonArray()) {
         return p_13925_.getAsJsonArray();
      } else {
         throw new JsonSyntaxException("Expected " + p_13926_ + " to be a JsonArray, was " + getType(p_13925_));
      }
   }

   public static JsonArray getAsJsonArray(JsonObject p_13934_, String p_13935_) {
      if (p_13934_.has(p_13935_)) {
         return convertToJsonArray(p_13934_.get(p_13935_), p_13935_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13935_ + ", expected to find a JsonArray");
      }
   }

   @Nullable
   @Contract("_,_,!null->!null;_,_,null->_")
   public static JsonArray getAsJsonArray(JsonObject p_13833_, String p_13834_, @Nullable JsonArray p_13835_) {
      return p_13833_.has(p_13834_) ? convertToJsonArray(p_13833_.get(p_13834_), p_13834_) : p_13835_;
   }

   public static <T> T convertToObject(@Nullable JsonElement p_13809_, String p_13810_, JsonDeserializationContext p_13811_, Class<? extends T> p_13812_) {
      if (p_13809_ != null) {
         return p_13811_.deserialize(p_13809_, p_13812_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13810_);
      }
   }

   public static <T> T getAsObject(JsonObject p_13837_, String p_13838_, JsonDeserializationContext p_13839_, Class<? extends T> p_13840_) {
      if (p_13837_.has(p_13838_)) {
         return convertToObject(p_13837_.get(p_13838_), p_13838_, p_13839_, p_13840_);
      } else {
         throw new JsonSyntaxException("Missing " + p_13838_);
      }
   }

   @Nullable
   @Contract("_,_,!null,_,_->!null;_,_,null,_,_->_")
   public static <T> T getAsObject(JsonObject p_13846_, String p_13847_, @Nullable T p_13848_, JsonDeserializationContext p_13849_, Class<? extends T> p_13850_) {
      return (T)(p_13846_.has(p_13847_) ? convertToObject(p_13846_.get(p_13847_), p_13847_, p_13849_, p_13850_) : p_13848_);
   }

   public static String getType(@Nullable JsonElement p_13884_) {
      String s = StringUtils.abbreviateMiddle(String.valueOf((Object)p_13884_), "...", 10);
      if (p_13884_ == null) {
         return "null (missing)";
      } else if (p_13884_.isJsonNull()) {
         return "null (json)";
      } else if (p_13884_.isJsonArray()) {
         return "an array (" + s + ")";
      } else if (p_13884_.isJsonObject()) {
         return "an object (" + s + ")";
      } else {
         if (p_13884_.isJsonPrimitive()) {
            JsonPrimitive jsonprimitive = p_13884_.getAsJsonPrimitive();
            if (jsonprimitive.isNumber()) {
               return "a number (" + s + ")";
            }

            if (jsonprimitive.isBoolean()) {
               return "a boolean (" + s + ")";
            }
         }

         return s;
      }
   }

   @Nullable
   public static <T> T fromJson(Gson p_13781_, Reader p_13782_, Class<T> p_13783_, boolean p_13784_) {
      try {
         JsonReader jsonreader = new JsonReader(p_13782_);
         jsonreader.setLenient(p_13784_);
         return p_13781_.getAdapter(p_13783_).read(jsonreader);
      } catch (IOException ioexception) {
         throw new JsonParseException(ioexception);
      }
   }

   @Nullable
   public static <T> T fromJson(Gson p_13772_, Reader p_13773_, TypeToken<T> p_13774_, boolean p_13775_) {
      try {
         JsonReader jsonreader = new JsonReader(p_13773_);
         jsonreader.setLenient(p_13775_);
         return p_13772_.getAdapter(p_13774_).read(jsonreader);
      } catch (IOException ioexception) {
         throw new JsonParseException(ioexception);
      }
   }

   @Nullable
   public static <T> T fromJson(Gson p_13790_, String p_13791_, TypeToken<T> p_13792_, boolean p_13793_) {
      return fromJson(p_13790_, new StringReader(p_13791_), p_13792_, p_13793_);
   }

   @Nullable
   public static <T> T fromJson(Gson p_13799_, String p_13800_, Class<T> p_13801_, boolean p_13802_) {
      return fromJson(p_13799_, new StringReader(p_13800_), p_13801_, p_13802_);
   }

   @Nullable
   public static <T> T fromJson(Gson p_13768_, Reader p_13769_, TypeToken<T> p_13770_) {
      return fromJson(p_13768_, p_13769_, p_13770_, false);
   }

   @Nullable
   public static <T> T fromJson(Gson p_13786_, String p_13787_, TypeToken<T> p_13788_) {
      return fromJson(p_13786_, p_13787_, p_13788_, false);
   }

   @Nullable
   public static <T> T fromJson(Gson p_13777_, Reader p_13778_, Class<T> p_13779_) {
      return fromJson(p_13777_, p_13778_, p_13779_, false);
   }

   @Nullable
   public static <T> T fromJson(Gson p_13795_, String p_13796_, Class<T> p_13797_) {
      return fromJson(p_13795_, p_13796_, p_13797_, false);
   }

   public static JsonObject parse(String p_13870_, boolean p_13871_) {
      return parse(new StringReader(p_13870_), p_13871_);
   }

   public static JsonObject parse(Reader p_13862_, boolean p_13863_) {
      return fromJson(GSON, p_13862_, JsonObject.class, p_13863_);
   }

   public static JsonObject parse(String p_13865_) {
      return parse(p_13865_, false);
   }

   public static JsonObject parse(Reader p_13860_) {
      return parse(p_13860_, false);
   }

   public static JsonArray parseArray(Reader p_144766_) {
      return fromJson(GSON, p_144766_, JsonArray.class, false);
   }
}
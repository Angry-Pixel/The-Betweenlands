package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class DisplayInfo {
   private final Component title;
   private final Component description;
   private final ItemStack icon;
   @Nullable
   private final ResourceLocation background;
   private final FrameType frame;
   private final boolean showToast;
   private final boolean announceChat;
   private final boolean hidden;
   private float x;
   private float y;

   public DisplayInfo(ItemStack p_14969_, Component p_14970_, Component p_14971_, @Nullable ResourceLocation p_14972_, FrameType p_14973_, boolean p_14974_, boolean p_14975_, boolean p_14976_) {
      this.title = p_14970_;
      this.description = p_14971_;
      this.icon = p_14969_;
      this.background = p_14972_;
      this.frame = p_14973_;
      this.showToast = p_14974_;
      this.announceChat = p_14975_;
      this.hidden = p_14976_;
   }

   public void setLocation(float p_14979_, float p_14980_) {
      this.x = p_14979_;
      this.y = p_14980_;
   }

   public Component getTitle() {
      return this.title;
   }

   public Component getDescription() {
      return this.description;
   }

   public ItemStack getIcon() {
      return this.icon;
   }

   @Nullable
   public ResourceLocation getBackground() {
      return this.background;
   }

   public FrameType getFrame() {
      return this.frame;
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public boolean shouldShowToast() {
      return this.showToast;
   }

   public boolean shouldAnnounceChat() {
      return this.announceChat;
   }

   public boolean isHidden() {
      return this.hidden;
   }

   public static DisplayInfo fromJson(JsonObject p_14982_) {
      Component component = Component.Serializer.fromJson(p_14982_.get("title"));
      Component component1 = Component.Serializer.fromJson(p_14982_.get("description"));
      if (component != null && component1 != null) {
         ItemStack itemstack = getIcon(GsonHelper.getAsJsonObject(p_14982_, "icon"));
         ResourceLocation resourcelocation = p_14982_.has("background") ? new ResourceLocation(GsonHelper.getAsString(p_14982_, "background")) : null;
         FrameType frametype = p_14982_.has("frame") ? FrameType.byName(GsonHelper.getAsString(p_14982_, "frame")) : FrameType.TASK;
         boolean flag = GsonHelper.getAsBoolean(p_14982_, "show_toast", true);
         boolean flag1 = GsonHelper.getAsBoolean(p_14982_, "announce_to_chat", true);
         boolean flag2 = GsonHelper.getAsBoolean(p_14982_, "hidden", false);
         return new DisplayInfo(itemstack, component, component1, resourcelocation, frametype, flag, flag1, flag2);
      } else {
         throw new JsonSyntaxException("Both title and description must be set");
      }
   }

   private static ItemStack getIcon(JsonObject p_14987_) {
      if (!p_14987_.has("item")) {
         throw new JsonSyntaxException("Unsupported icon type, currently only items are supported (add 'item' key)");
      } else {
         Item item = GsonHelper.getAsItem(p_14987_, "item");
         if (p_14987_.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            ItemStack itemstack = new ItemStack(item);
            if (p_14987_.has("nbt")) {
               try {
                  CompoundTag compoundtag = TagParser.parseTag(GsonHelper.convertToString(p_14987_.get("nbt"), "nbt"));
                  itemstack.setTag(compoundtag);
               } catch (CommandSyntaxException commandsyntaxexception) {
                  throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
               }
            }

            return itemstack;
         }
      }
   }

   public void serializeToNetwork(FriendlyByteBuf p_14984_) {
      p_14984_.writeComponent(this.title);
      p_14984_.writeComponent(this.description);
      p_14984_.writeItem(this.icon);
      p_14984_.writeEnum(this.frame);
      int i = 0;
      if (this.background != null) {
         i |= 1;
      }

      if (this.showToast) {
         i |= 2;
      }

      if (this.hidden) {
         i |= 4;
      }

      p_14984_.writeInt(i);
      if (this.background != null) {
         p_14984_.writeResourceLocation(this.background);
      }

      p_14984_.writeFloat(this.x);
      p_14984_.writeFloat(this.y);
   }

   public static DisplayInfo fromNetwork(FriendlyByteBuf p_14989_) {
      Component component = p_14989_.readComponent();
      Component component1 = p_14989_.readComponent();
      ItemStack itemstack = p_14989_.readItem();
      FrameType frametype = p_14989_.readEnum(FrameType.class);
      int i = p_14989_.readInt();
      ResourceLocation resourcelocation = (i & 1) != 0 ? p_14989_.readResourceLocation() : null;
      boolean flag = (i & 2) != 0;
      boolean flag1 = (i & 4) != 0;
      DisplayInfo displayinfo = new DisplayInfo(itemstack, component, component1, resourcelocation, frametype, flag, false, flag1);
      displayinfo.setLocation(p_14989_.readFloat(), p_14989_.readFloat());
      return displayinfo;
   }

   public JsonElement serializeToJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.add("icon", this.serializeIcon());
      jsonobject.add("title", Component.Serializer.toJsonTree(this.title));
      jsonobject.add("description", Component.Serializer.toJsonTree(this.description));
      jsonobject.addProperty("frame", this.frame.getName());
      jsonobject.addProperty("show_toast", this.showToast);
      jsonobject.addProperty("announce_to_chat", this.announceChat);
      jsonobject.addProperty("hidden", this.hidden);
      if (this.background != null) {
         jsonobject.addProperty("background", this.background.toString());
      }

      return jsonobject;
   }

   private JsonObject serializeIcon() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("item", Registry.ITEM.getKey(this.icon.getItem()).toString());
      if (this.icon.hasTag()) {
         jsonobject.addProperty("nbt", this.icon.getTag().toString());
      }

      return jsonobject;
   }
}
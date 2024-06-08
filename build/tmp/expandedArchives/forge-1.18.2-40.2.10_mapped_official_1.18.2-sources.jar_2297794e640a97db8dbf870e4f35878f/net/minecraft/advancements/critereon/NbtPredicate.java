package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class NbtPredicate {
   public static final NbtPredicate ANY = new NbtPredicate((CompoundTag)null);
   @Nullable
   private final CompoundTag tag;

   public NbtPredicate(@Nullable CompoundTag p_57475_) {
      this.tag = p_57475_;
   }

   public boolean matches(ItemStack p_57480_) {
      return this == ANY ? true : this.matches(p_57480_.getTag());
   }

   public boolean matches(Entity p_57478_) {
      return this == ANY ? true : this.matches(getEntityTagToCompare(p_57478_));
   }

   public boolean matches(@Nullable Tag p_57484_) {
      if (p_57484_ == null) {
         return this == ANY;
      } else {
         return this.tag == null || NbtUtils.compareNbt(this.tag, p_57484_, true);
      }
   }

   public JsonElement serializeToJson() {
      return (JsonElement)(this != ANY && this.tag != null ? new JsonPrimitive(this.tag.toString()) : JsonNull.INSTANCE);
   }

   public static NbtPredicate fromJson(@Nullable JsonElement p_57482_) {
      if (p_57482_ != null && !p_57482_.isJsonNull()) {
         CompoundTag compoundtag;
         try {
            compoundtag = TagParser.parseTag(GsonHelper.convertToString(p_57482_, "nbt"));
         } catch (CommandSyntaxException commandsyntaxexception) {
            throw new JsonSyntaxException("Invalid nbt tag: " + commandsyntaxexception.getMessage());
         }

         return new NbtPredicate(compoundtag);
      } else {
         return ANY;
      }
   }

   public static CompoundTag getEntityTagToCompare(Entity p_57486_) {
      CompoundTag compoundtag = p_57486_.saveWithoutId(new CompoundTag());
      if (p_57486_ instanceof Player) {
         ItemStack itemstack = ((Player)p_57486_).getInventory().getSelected();
         if (!itemstack.isEmpty()) {
            compoundtag.put("SelectedItem", itemstack.save(new CompoundTag()));
         }
      }

      return compoundtag;
   }
}
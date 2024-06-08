package net.minecraft.world.item;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import org.apache.commons.lang3.StringUtils;

public class PlayerHeadItem extends StandingAndWallBlockItem {
   public static final String TAG_SKULL_OWNER = "SkullOwner";

   public PlayerHeadItem(Block p_42971_, Block p_42972_, Item.Properties p_42973_) {
      super(p_42971_, p_42972_, p_42973_);
   }

   public Component getName(ItemStack p_42977_) {
      if (p_42977_.is(Items.PLAYER_HEAD) && p_42977_.hasTag()) {
         String s = null;
         CompoundTag compoundtag = p_42977_.getTag();
         if (compoundtag.contains("SkullOwner", 8)) {
            s = compoundtag.getString("SkullOwner");
         } else if (compoundtag.contains("SkullOwner", 10)) {
            CompoundTag compoundtag1 = compoundtag.getCompound("SkullOwner");
            if (compoundtag1.contains("Name", 8)) {
               s = compoundtag1.getString("Name");
            }
         }

         if (s != null) {
            return new TranslatableComponent(this.getDescriptionId() + ".named", s);
         }
      }

      return super.getName(p_42977_);
   }

   public void verifyTagAfterLoad(CompoundTag p_151179_) {
      super.verifyTagAfterLoad(p_151179_);
      if (p_151179_.contains("SkullOwner", 8) && !StringUtils.isBlank(p_151179_.getString("SkullOwner"))) {
         GameProfile gameprofile = new GameProfile((UUID)null, p_151179_.getString("SkullOwner"));
         SkullBlockEntity.updateGameprofile(gameprofile, (p_151177_) -> {
            p_151179_.put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), p_151177_));
         });
      }

   }
}
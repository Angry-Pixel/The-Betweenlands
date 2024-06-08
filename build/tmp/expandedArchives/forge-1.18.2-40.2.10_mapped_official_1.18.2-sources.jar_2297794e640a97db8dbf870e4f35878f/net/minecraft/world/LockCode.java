package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

@Immutable
public class LockCode {
   public static final LockCode NO_LOCK = new LockCode("");
   public static final String TAG_LOCK = "Lock";
   private final String key;

   public LockCode(String p_19106_) {
      this.key = p_19106_;
   }

   public boolean unlocksWith(ItemStack p_19108_) {
      return this.key.isEmpty() || !p_19108_.isEmpty() && p_19108_.hasCustomHoverName() && this.key.equals(p_19108_.getHoverName().getString());
   }

   public void addToTag(CompoundTag p_19110_) {
      if (!this.key.isEmpty()) {
         p_19110_.putString("Lock", this.key);
      }

   }

   public static LockCode fromTag(CompoundTag p_19112_) {
      return p_19112_.contains("Lock", 8) ? new LockCode(p_19112_.getString("Lock")) : NO_LOCK;
   }
}
package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemInput implements Predicate<ItemStack> {
   private static final Dynamic2CommandExceptionType ERROR_STACK_TOO_BIG = new Dynamic2CommandExceptionType((p_120986_, p_120987_) -> {
      return new TranslatableComponent("arguments.item.overstacked", p_120986_, p_120987_);
   });
   private final Item item;
   @Nullable
   private final CompoundTag tag;

   public ItemInput(Item p_120977_, @Nullable CompoundTag p_120978_) {
      this.item = p_120977_;
      this.tag = p_120978_;
   }

   public Item getItem() {
      return this.item;
   }

   public boolean test(ItemStack p_120984_) {
      return p_120984_.is(this.item) && NbtUtils.compareNbt(this.tag, p_120984_.getTag(), true);
   }

   public ItemStack createItemStack(int p_120981_, boolean p_120982_) throws CommandSyntaxException {
      ItemStack itemstack = new ItemStack(this.item, p_120981_);
      if (this.tag != null) {
         itemstack.setTag(this.tag);
      }

      if (p_120982_ && p_120981_ > itemstack.getMaxStackSize()) {
         throw ERROR_STACK_TOO_BIG.create(Registry.ITEM.getKey(this.item), itemstack.getMaxStackSize());
      } else {
         return itemstack;
      }
   }

   public String serialize() {
      StringBuilder stringbuilder = new StringBuilder(Registry.ITEM.getId(this.item));
      if (this.tag != null) {
         stringbuilder.append((Object)this.tag);
      }

      return stringbuilder.toString();
   }
}
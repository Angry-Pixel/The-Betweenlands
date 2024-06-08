package net.minecraft.client.player.inventory;

import com.google.common.collect.ForwardingList;
import java.util.List;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Hotbar extends ForwardingList<ItemStack> {
   private final NonNullList<ItemStack> items = NonNullList.withSize(Inventory.getSelectionSize(), ItemStack.EMPTY);

   protected List<ItemStack> delegate() {
      return this.items;
   }

   public ListTag createTag() {
      ListTag listtag = new ListTag();

      for(ItemStack itemstack : this.delegate()) {
         listtag.add(itemstack.save(new CompoundTag()));
      }

      return listtag;
   }

   public void fromTag(ListTag p_108784_) {
      List<ItemStack> list = this.delegate();

      for(int i = 0; i < list.size(); ++i) {
         list.set(i, ItemStack.of(p_108784_.getCompound(i)));
      }

   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.delegate()) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }
}
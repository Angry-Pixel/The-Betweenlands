package net.minecraft.world.item.trading;

import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class MerchantOffers extends ArrayList<MerchantOffer> {
   public MerchantOffers() {
   }

   public MerchantOffers(CompoundTag p_45387_) {
      ListTag listtag = p_45387_.getList("Recipes", 10);

      for(int i = 0; i < listtag.size(); ++i) {
         this.add(new MerchantOffer(listtag.getCompound(i)));
      }

   }

   @Nullable
   public MerchantOffer getRecipeFor(ItemStack p_45390_, ItemStack p_45391_, int p_45392_) {
      if (p_45392_ > 0 && p_45392_ < this.size()) {
         MerchantOffer merchantoffer1 = this.get(p_45392_);
         return merchantoffer1.satisfiedBy(p_45390_, p_45391_) ? merchantoffer1 : null;
      } else {
         for(int i = 0; i < this.size(); ++i) {
            MerchantOffer merchantoffer = this.get(i);
            if (merchantoffer.satisfiedBy(p_45390_, p_45391_)) {
               return merchantoffer;
            }
         }

         return null;
      }
   }

   public void writeToStream(FriendlyByteBuf p_45394_) {
      p_45394_.writeByte((byte)(this.size() & 255));

      for(int i = 0; i < this.size(); ++i) {
         MerchantOffer merchantoffer = this.get(i);
         p_45394_.writeItem(merchantoffer.getBaseCostA());
         p_45394_.writeItem(merchantoffer.getResult());
         ItemStack itemstack = merchantoffer.getCostB();
         p_45394_.writeBoolean(!itemstack.isEmpty());
         if (!itemstack.isEmpty()) {
            p_45394_.writeItem(itemstack);
         }

         p_45394_.writeBoolean(merchantoffer.isOutOfStock());
         p_45394_.writeInt(merchantoffer.getUses());
         p_45394_.writeInt(merchantoffer.getMaxUses());
         p_45394_.writeInt(merchantoffer.getXp());
         p_45394_.writeInt(merchantoffer.getSpecialPriceDiff());
         p_45394_.writeFloat(merchantoffer.getPriceMultiplier());
         p_45394_.writeInt(merchantoffer.getDemand());
      }

   }

   public static MerchantOffers createFromStream(FriendlyByteBuf p_45396_) {
      MerchantOffers merchantoffers = new MerchantOffers();
      int i = p_45396_.readByte() & 255;

      for(int j = 0; j < i; ++j) {
         ItemStack itemstack = p_45396_.readItem();
         ItemStack itemstack1 = p_45396_.readItem();
         ItemStack itemstack2 = ItemStack.EMPTY;
         if (p_45396_.readBoolean()) {
            itemstack2 = p_45396_.readItem();
         }

         boolean flag = p_45396_.readBoolean();
         int k = p_45396_.readInt();
         int l = p_45396_.readInt();
         int i1 = p_45396_.readInt();
         int j1 = p_45396_.readInt();
         float f = p_45396_.readFloat();
         int k1 = p_45396_.readInt();
         MerchantOffer merchantoffer = new MerchantOffer(itemstack, itemstack2, itemstack1, k, l, i1, f, k1);
         if (flag) {
            merchantoffer.setToOutOfStock();
         }

         merchantoffer.setSpecialPriceDiff(j1);
         merchantoffers.add(merchantoffer);
      }

      return merchantoffers;
   }

   public CompoundTag createTag() {
      CompoundTag compoundtag = new CompoundTag();
      ListTag listtag = new ListTag();

      for(int i = 0; i < this.size(); ++i) {
         MerchantOffer merchantoffer = this.get(i);
         listtag.add(merchantoffer.createTag());
      }

      compoundtag.put("Recipes", listtag);
      return compoundtag;
   }
}
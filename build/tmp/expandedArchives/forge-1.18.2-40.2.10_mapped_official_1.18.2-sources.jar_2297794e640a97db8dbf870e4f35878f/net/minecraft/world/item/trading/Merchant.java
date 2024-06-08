package net.minecraft.world.item.trading;

import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.ItemStack;

public interface Merchant {
   void setTradingPlayer(@Nullable Player p_45307_);

   @Nullable
   Player getTradingPlayer();

   MerchantOffers getOffers();

   void overrideOffers(MerchantOffers p_45306_);

   void notifyTrade(MerchantOffer p_45305_);

   void notifyTradeUpdated(ItemStack p_45308_);

   int getVillagerXp();

   void overrideXp(int p_45309_);

   boolean showProgressBar();

   SoundEvent getNotifyTradeSound();

   default boolean canRestock() {
      return false;
   }

   default void openTradingScreen(Player p_45302_, Component p_45303_, int p_45304_) {
      OptionalInt optionalint = p_45302_.openMenu(new SimpleMenuProvider((p_45298_, p_45299_, p_45300_) -> {
         return new MerchantMenu(p_45298_, p_45299_, this);
      }, p_45303_));
      if (optionalint.isPresent()) {
         MerchantOffers merchantoffers = this.getOffers();
         if (!merchantoffers.isEmpty()) {
            p_45302_.sendMerchantOffers(optionalint.getAsInt(), merchantoffers, p_45304_, this.getVillagerXp(), this.showProgressBar(), this.canRestock());
         }
      }

   }

   boolean isClientSide();
}
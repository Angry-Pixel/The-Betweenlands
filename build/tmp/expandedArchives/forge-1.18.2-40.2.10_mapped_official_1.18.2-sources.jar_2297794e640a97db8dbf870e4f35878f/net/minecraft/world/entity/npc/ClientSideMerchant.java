package net.minecraft.world.entity.npc;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;

public class ClientSideMerchant implements Merchant {
   private final Player source;
   private MerchantOffers offers = new MerchantOffers();
   private int xp;

   public ClientSideMerchant(Player p_35344_) {
      this.source = p_35344_;
   }

   public Player getTradingPlayer() {
      return this.source;
   }

   public void setTradingPlayer(@Nullable Player p_35356_) {
   }

   public MerchantOffers getOffers() {
      return this.offers;
   }

   public void overrideOffers(MerchantOffers p_35348_) {
      this.offers = p_35348_;
   }

   public void notifyTrade(MerchantOffer p_35346_) {
      p_35346_.increaseUses();
   }

   public void notifyTradeUpdated(ItemStack p_35358_) {
   }

   public boolean isClientSide() {
      return this.source.getLevel().isClientSide;
   }

   public int getVillagerXp() {
      return this.xp;
   }

   public void overrideXp(int p_35360_) {
      this.xp = p_35360_;
   }

   public boolean showProgressBar() {
      return true;
   }

   public SoundEvent getNotifyTradeSound() {
      return SoundEvents.VILLAGER_YES;
   }
}
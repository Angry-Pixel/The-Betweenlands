package net.minecraft.world.inventory;

import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;

public class MerchantResultSlot extends Slot {
   private final MerchantContainer slots;
   private final Player player;
   private int removeCount;
   private final Merchant merchant;

   public MerchantResultSlot(Player p_40083_, Merchant p_40084_, MerchantContainer p_40085_, int p_40086_, int p_40087_, int p_40088_) {
      super(p_40085_, p_40086_, p_40087_, p_40088_);
      this.player = p_40083_;
      this.merchant = p_40084_;
      this.slots = p_40085_;
   }

   public boolean mayPlace(ItemStack p_40095_) {
      return false;
   }

   public ItemStack remove(int p_40090_) {
      if (this.hasItem()) {
         this.removeCount += Math.min(p_40090_, this.getItem().getCount());
      }

      return super.remove(p_40090_);
   }

   protected void onQuickCraft(ItemStack p_40097_, int p_40098_) {
      this.removeCount += p_40098_;
      this.checkTakeAchievements(p_40097_);
   }

   protected void checkTakeAchievements(ItemStack p_40100_) {
      p_40100_.onCraftedBy(this.player.level, this.player, this.removeCount);
      this.removeCount = 0;
   }

   public void onTake(Player p_150631_, ItemStack p_150632_) {
      this.checkTakeAchievements(p_150632_);
      MerchantOffer merchantoffer = this.slots.getActiveOffer();
      if (merchantoffer != null) {
         ItemStack itemstack = this.slots.getItem(0);
         ItemStack itemstack1 = this.slots.getItem(1);
         if (merchantoffer.take(itemstack, itemstack1) || merchantoffer.take(itemstack1, itemstack)) {
            this.merchant.notifyTrade(merchantoffer);
            p_150631_.awardStat(Stats.TRADED_WITH_VILLAGER);
            this.slots.setItem(0, itemstack);
            this.slots.setItem(1, itemstack1);
         }

         this.merchant.overrideXp(this.merchant.getVillagerXp() + merchantoffer.getXp());
      }

   }
}
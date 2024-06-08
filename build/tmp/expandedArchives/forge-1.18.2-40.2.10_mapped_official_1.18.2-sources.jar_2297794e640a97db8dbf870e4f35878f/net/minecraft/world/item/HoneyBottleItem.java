package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class HoneyBottleItem extends Item {
   private static final int DRINK_DURATION = 40;

   public HoneyBottleItem(Item.Properties p_41346_) {
      super(p_41346_);
   }

   public ItemStack finishUsingItem(ItemStack p_41348_, Level p_41349_, LivingEntity p_41350_) {
      super.finishUsingItem(p_41348_, p_41349_, p_41350_);
      if (p_41350_ instanceof ServerPlayer) {
         ServerPlayer serverplayer = (ServerPlayer)p_41350_;
         CriteriaTriggers.CONSUME_ITEM.trigger(serverplayer, p_41348_);
         serverplayer.awardStat(Stats.ITEM_USED.get(this));
      }

      if (!p_41349_.isClientSide) {
         p_41350_.removeEffect(MobEffects.POISON);
      }

      if (p_41348_.isEmpty()) {
         return new ItemStack(Items.GLASS_BOTTLE);
      } else {
         if (p_41350_ instanceof Player && !((Player)p_41350_).getAbilities().instabuild) {
            ItemStack itemstack = new ItemStack(Items.GLASS_BOTTLE);
            Player player = (Player)p_41350_;
            if (!player.getInventory().add(itemstack)) {
               player.drop(itemstack, false);
            }
         }

         return p_41348_;
      }
   }

   public int getUseDuration(ItemStack p_41360_) {
      return 40;
   }

   public UseAnim getUseAnimation(ItemStack p_41358_) {
      return UseAnim.DRINK;
   }

   public SoundEvent getDrinkingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public SoundEvent getEatingSound() {
      return SoundEvents.HONEY_DRINK;
   }

   public InteractionResultHolder<ItemStack> use(Level p_41352_, Player p_41353_, InteractionHand p_41354_) {
      return ItemUtils.startUsingInstantly(p_41352_, p_41353_, p_41354_);
   }
}
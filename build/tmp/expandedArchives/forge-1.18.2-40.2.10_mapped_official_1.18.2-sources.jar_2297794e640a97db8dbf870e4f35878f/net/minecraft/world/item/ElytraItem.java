package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;

public class ElytraItem extends Item implements Wearable {
   public ElytraItem(Item.Properties p_41132_) {
      super(p_41132_);
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public static boolean isFlyEnabled(ItemStack p_41141_) {
      return p_41141_.getDamageValue() < p_41141_.getMaxDamage() - 1;
   }

   public boolean isValidRepairItem(ItemStack p_41134_, ItemStack p_41135_) {
      return p_41135_.is(Items.PHANTOM_MEMBRANE);
   }

   public InteractionResultHolder<ItemStack> use(Level p_41137_, Player p_41138_, InteractionHand p_41139_) {
      ItemStack itemstack = p_41138_.getItemInHand(p_41139_);
      EquipmentSlot equipmentslot = Mob.getEquipmentSlotForItem(itemstack);
      ItemStack itemstack1 = p_41138_.getItemBySlot(equipmentslot);
      if (itemstack1.isEmpty()) {
         p_41138_.setItemSlot(equipmentslot, itemstack.copy());
         if (!p_41137_.isClientSide()) {
            p_41138_.awardStat(Stats.ITEM_USED.get(this));
         }

         itemstack.setCount(0);
         return InteractionResultHolder.sidedSuccess(itemstack, p_41137_.isClientSide());
      } else {
         return InteractionResultHolder.fail(itemstack);
      }
   }

   @Override
   public boolean canElytraFly(ItemStack stack, net.minecraft.world.entity.LivingEntity entity) {
      return ElytraItem.isFlyEnabled(stack);
   }

   @Override
   public boolean elytraFlightTick(ItemStack stack, net.minecraft.world.entity.LivingEntity entity, int flightTicks) {
      if (!entity.level.isClientSide) {
         int nextFlightTick = flightTicks + 1;
         if (nextFlightTick % 10 == 0) {
            if (nextFlightTick % 20 == 0) {
               stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(net.minecraft.world.entity.EquipmentSlot.CHEST));
            }
            entity.gameEvent(net.minecraft.world.level.gameevent.GameEvent.ELYTRA_FREE_FALL);
         }
      }
      return true;
   }

   @Nullable
   public SoundEvent getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_ELYTRA;
   }
}

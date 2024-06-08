package net.minecraft.world.item;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BottleItem extends Item {
   public BottleItem(Item.Properties p_40648_) {
      super(p_40648_);
   }

   public InteractionResultHolder<ItemStack> use(Level p_40656_, Player p_40657_, InteractionHand p_40658_) {
      List<AreaEffectCloud> list = p_40656_.getEntitiesOfClass(AreaEffectCloud.class, p_40657_.getBoundingBox().inflate(2.0D), (p_40650_) -> {
         return p_40650_ != null && p_40650_.isAlive() && p_40650_.getOwner() instanceof EnderDragon;
      });
      ItemStack itemstack = p_40657_.getItemInHand(p_40658_);
      if (!list.isEmpty()) {
         AreaEffectCloud areaeffectcloud = list.get(0);
         areaeffectcloud.setRadius(areaeffectcloud.getRadius() - 0.5F);
         p_40656_.playSound((Player)null, p_40657_.getX(), p_40657_.getY(), p_40657_.getZ(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.NEUTRAL, 1.0F, 1.0F);
         p_40656_.gameEvent(p_40657_, GameEvent.FLUID_PICKUP, p_40657_.blockPosition());
         return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem(itemstack, p_40657_, new ItemStack(Items.DRAGON_BREATH)), p_40656_.isClientSide());
      } else {
         HitResult hitresult = getPlayerPOVHitResult(p_40656_, p_40657_, ClipContext.Fluid.SOURCE_ONLY);
         if (hitresult.getType() == HitResult.Type.MISS) {
            return InteractionResultHolder.pass(itemstack);
         } else {
            if (hitresult.getType() == HitResult.Type.BLOCK) {
               BlockPos blockpos = ((BlockHitResult)hitresult).getBlockPos();
               if (!p_40656_.mayInteract(p_40657_, blockpos)) {
                  return InteractionResultHolder.pass(itemstack);
               }

               if (p_40656_.getFluidState(blockpos).is(FluidTags.WATER)) {
                  p_40656_.playSound(p_40657_, p_40657_.getX(), p_40657_.getY(), p_40657_.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 1.0F, 1.0F);
                  p_40656_.gameEvent(p_40657_, GameEvent.FLUID_PICKUP, blockpos);
                  return InteractionResultHolder.sidedSuccess(this.turnBottleIntoItem(itemstack, p_40657_, PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER)), p_40656_.isClientSide());
               }
            }

            return InteractionResultHolder.pass(itemstack);
         }
      }
   }

   protected ItemStack turnBottleIntoItem(ItemStack p_40652_, Player p_40653_, ItemStack p_40654_) {
      p_40653_.awardStat(Stats.ITEM_USED.get(this));
      return ItemUtils.createFilledResult(p_40652_, p_40653_, p_40654_);
   }
}
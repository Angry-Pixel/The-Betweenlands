package net.minecraft.world.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.GlowItemFrame;
import net.minecraft.world.entity.decoration.HangingEntity;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;

public class HangingEntityItem extends Item {
   private final EntityType<? extends HangingEntity> type;

   public HangingEntityItem(EntityType<? extends HangingEntity> p_41324_, Item.Properties p_41325_) {
      super(p_41325_);
      this.type = p_41324_;
   }

   public InteractionResult useOn(UseOnContext p_41331_) {
      BlockPos blockpos = p_41331_.getClickedPos();
      Direction direction = p_41331_.getClickedFace();
      BlockPos blockpos1 = blockpos.relative(direction);
      Player player = p_41331_.getPlayer();
      ItemStack itemstack = p_41331_.getItemInHand();
      if (player != null && !this.mayPlace(player, direction, itemstack, blockpos1)) {
         return InteractionResult.FAIL;
      } else {
         Level level = p_41331_.getLevel();
         HangingEntity hangingentity;
         if (this.type == EntityType.PAINTING) {
            hangingentity = new Painting(level, blockpos1, direction);
         } else if (this.type == EntityType.ITEM_FRAME) {
            hangingentity = new ItemFrame(level, blockpos1, direction);
         } else {
            if (this.type != EntityType.GLOW_ITEM_FRAME) {
               return InteractionResult.sidedSuccess(level.isClientSide);
            }

            hangingentity = new GlowItemFrame(level, blockpos1, direction);
         }

         CompoundTag compoundtag = itemstack.getTag();
         if (compoundtag != null) {
            EntityType.updateCustomEntityTag(level, player, hangingentity, compoundtag);
         }

         if (hangingentity.survives()) {
            if (!level.isClientSide) {
               hangingentity.playPlacementSound();
               level.gameEvent(player, GameEvent.ENTITY_PLACE, blockpos);
               level.addFreshEntity(hangingentity);
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.CONSUME;
         }
      }
   }

   protected boolean mayPlace(Player p_41326_, Direction p_41327_, ItemStack p_41328_, BlockPos p_41329_) {
      return !p_41327_.getAxis().isVertical() && p_41326_.mayUseItemAt(p_41329_, p_41327_, p_41328_);
   }
}
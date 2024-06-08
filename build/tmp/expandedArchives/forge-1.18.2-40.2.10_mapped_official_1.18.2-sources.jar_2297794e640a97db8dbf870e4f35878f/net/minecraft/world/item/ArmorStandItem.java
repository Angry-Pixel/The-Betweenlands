package net.minecraft.world.item;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Rotations;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ArmorStandItem extends Item {
   public ArmorStandItem(Item.Properties p_40503_) {
      super(p_40503_);
   }

   public InteractionResult useOn(UseOnContext p_40510_) {
      Direction direction = p_40510_.getClickedFace();
      if (direction == Direction.DOWN) {
         return InteractionResult.FAIL;
      } else {
         Level level = p_40510_.getLevel();
         BlockPlaceContext blockplacecontext = new BlockPlaceContext(p_40510_);
         BlockPos blockpos = blockplacecontext.getClickedPos();
         ItemStack itemstack = p_40510_.getItemInHand();
         Vec3 vec3 = Vec3.atBottomCenterOf(blockpos);
         AABB aabb = EntityType.ARMOR_STAND.getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
         if (level.noCollision((Entity)null, aabb) && level.getEntities((Entity)null, aabb).isEmpty()) {
            if (level instanceof ServerLevel) {
               ServerLevel serverlevel = (ServerLevel)level;
               ArmorStand armorstand = EntityType.ARMOR_STAND.create(serverlevel, itemstack.getTag(), (Component)null, p_40510_.getPlayer(), blockpos, MobSpawnType.SPAWN_EGG, true, true);
               if (armorstand == null) {
                  return InteractionResult.FAIL;
               }

               float f = (float)Mth.floor((Mth.wrapDegrees(p_40510_.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
               armorstand.moveTo(armorstand.getX(), armorstand.getY(), armorstand.getZ(), f, 0.0F);
               this.randomizePose(armorstand, level.random);
               serverlevel.addFreshEntityWithPassengers(armorstand);
               level.playSound((Player)null, armorstand.getX(), armorstand.getY(), armorstand.getZ(), SoundEvents.ARMOR_STAND_PLACE, SoundSource.BLOCKS, 0.75F, 0.8F);
               level.gameEvent(p_40510_.getPlayer(), GameEvent.ENTITY_PLACE, armorstand);
            }

            itemstack.shrink(1);
            return InteractionResult.sidedSuccess(level.isClientSide);
         } else {
            return InteractionResult.FAIL;
         }
      }
   }

   private void randomizePose(ArmorStand p_40507_, Random p_40508_) {
      Rotations rotations = p_40507_.getHeadPose();
      float f = p_40508_.nextFloat() * 5.0F;
      float f1 = p_40508_.nextFloat() * 20.0F - 10.0F;
      Rotations rotations1 = new Rotations(rotations.getX() + f, rotations.getY() + f1, rotations.getZ());
      p_40507_.setHeadPose(rotations1);
      rotations = p_40507_.getBodyPose();
      f = p_40508_.nextFloat() * 10.0F - 5.0F;
      rotations1 = new Rotations(rotations.getX(), rotations.getY() + f, rotations.getZ());
      p_40507_.setBodyPose(rotations1);
   }
}
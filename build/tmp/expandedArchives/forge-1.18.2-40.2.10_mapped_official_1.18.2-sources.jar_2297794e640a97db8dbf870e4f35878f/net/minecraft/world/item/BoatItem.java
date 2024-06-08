package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BoatItem extends Item {
   private static final Predicate<Entity> ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
   private final Boat.Type type;

   public BoatItem(Boat.Type p_40619_, Item.Properties p_40620_) {
      super(p_40620_);
      this.type = p_40619_;
   }

   public InteractionResultHolder<ItemStack> use(Level p_40622_, Player p_40623_, InteractionHand p_40624_) {
      ItemStack itemstack = p_40623_.getItemInHand(p_40624_);
      HitResult hitresult = getPlayerPOVHitResult(p_40622_, p_40623_, ClipContext.Fluid.ANY);
      if (hitresult.getType() == HitResult.Type.MISS) {
         return InteractionResultHolder.pass(itemstack);
      } else {
         Vec3 vec3 = p_40623_.getViewVector(1.0F);
         double d0 = 5.0D;
         List<Entity> list = p_40622_.getEntities(p_40623_, p_40623_.getBoundingBox().expandTowards(vec3.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
         if (!list.isEmpty()) {
            Vec3 vec31 = p_40623_.getEyePosition();

            for(Entity entity : list) {
               AABB aabb = entity.getBoundingBox().inflate((double)entity.getPickRadius());
               if (aabb.contains(vec31)) {
                  return InteractionResultHolder.pass(itemstack);
               }
            }
         }

         if (hitresult.getType() == HitResult.Type.BLOCK) {
            Boat boat = new Boat(p_40622_, hitresult.getLocation().x, hitresult.getLocation().y, hitresult.getLocation().z);
            boat.setType(this.type);
            boat.setYRot(p_40623_.getYRot());
            if (!p_40622_.noCollision(boat, boat.getBoundingBox())) {
               return InteractionResultHolder.fail(itemstack);
            } else {
               if (!p_40622_.isClientSide) {
                  p_40622_.addFreshEntity(boat);
                  p_40622_.gameEvent(p_40623_, GameEvent.ENTITY_PLACE, new BlockPos(hitresult.getLocation()));
                  if (!p_40623_.getAbilities().instabuild) {
                     itemstack.shrink(1);
                  }
               }

               p_40623_.awardStat(Stats.ITEM_USED.get(this));
               return InteractionResultHolder.sidedSuccess(itemstack, p_40622_.isClientSide());
            }
         } else {
            return InteractionResultHolder.pass(itemstack);
         }
      }
   }
}
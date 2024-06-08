package net.minecraft.world.entity.vehicle;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Minecart extends AbstractMinecart {
   public Minecart(EntityType<?> p_38470_, Level p_38471_) {
      super(p_38470_, p_38471_);
   }

   public Minecart(Level p_38473_, double p_38474_, double p_38475_, double p_38476_) {
      super(EntityType.MINECART, p_38473_, p_38474_, p_38475_, p_38476_);
   }

   public InteractionResult interact(Player p_38483_, InteractionHand p_38484_) {
      InteractionResult ret = super.interact(p_38483_, p_38484_);
      if (ret.consumesAction()) return ret;
      if (p_38483_.isSecondaryUseActive()) {
         return InteractionResult.PASS;
      } else if (this.isVehicle()) {
         return InteractionResult.PASS;
      } else if (!this.level.isClientSide) {
         return p_38483_.startRiding(this) ? InteractionResult.CONSUME : InteractionResult.PASS;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void activateMinecart(int p_38478_, int p_38479_, int p_38480_, boolean p_38481_) {
      if (p_38481_) {
         if (this.isVehicle()) {
            this.ejectPassengers();
         }

         if (this.getHurtTime() == 0) {
            this.setHurtDir(-this.getHurtDir());
            this.setHurtTime(10);
            this.setDamage(50.0F);
            this.markHurt();
         }
      }

   }

   public AbstractMinecart.Type getMinecartType() {
      return AbstractMinecart.Type.RIDEABLE;
   }
}

package net.minecraft.world.entity.boss.enderdragon.phases;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public interface DragonPhaseInstance {
   boolean isSitting();

   void doClientTick();

   void doServerTick();

   void onCrystalDestroyed(EndCrystal p_31315_, BlockPos p_31316_, DamageSource p_31317_, @Nullable Player p_31318_);

   void begin();

   void end();

   float getFlySpeed();

   float getTurnSpeed();

   EnderDragonPhase<? extends DragonPhaseInstance> getPhase();

   @Nullable
   Vec3 getFlyTargetLocation();

   float onHurt(DamageSource p_31313_, float p_31314_);
}
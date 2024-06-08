package net.minecraft.world.entity.boss.enderdragon.phases;

import com.mojang.logging.LogUtils;
import javax.annotation.Nullable;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import org.slf4j.Logger;

public class EnderDragonPhaseManager {
   private static final Logger LOGGER = LogUtils.getLogger();
   private final EnderDragon dragon;
   private final DragonPhaseInstance[] phases = new DragonPhaseInstance[EnderDragonPhase.getCount()];
   @Nullable
   private DragonPhaseInstance currentPhase;

   public EnderDragonPhaseManager(EnderDragon p_31414_) {
      this.dragon = p_31414_;
      this.setPhase(EnderDragonPhase.HOVERING);
   }

   public void setPhase(EnderDragonPhase<?> p_31417_) {
      if (this.currentPhase == null || p_31417_ != this.currentPhase.getPhase()) {
         if (this.currentPhase != null) {
            this.currentPhase.end();
         }

         this.currentPhase = this.getPhase(p_31417_);
         if (!this.dragon.level.isClientSide) {
            this.dragon.getEntityData().set(EnderDragon.DATA_PHASE, p_31417_.getId());
         }

         LOGGER.debug("Dragon is now in phase {} on the {}", p_31417_, this.dragon.level.isClientSide ? "client" : "server");
         this.currentPhase.begin();
      }
   }

   public DragonPhaseInstance getCurrentPhase() {
      return this.currentPhase;
   }

   public <T extends DragonPhaseInstance> T getPhase(EnderDragonPhase<T> p_31419_) {
      int i = p_31419_.getId();
      if (this.phases[i] == null) {
         this.phases[i] = p_31419_.createInstance(this.dragon);
      }

      return (T)this.phases[i];
   }
}
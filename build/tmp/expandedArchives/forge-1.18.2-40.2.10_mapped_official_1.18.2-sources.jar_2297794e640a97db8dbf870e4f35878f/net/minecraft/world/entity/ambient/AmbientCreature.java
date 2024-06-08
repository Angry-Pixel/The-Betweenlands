package net.minecraft.world.entity.ambient;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public abstract class AmbientCreature extends Mob {
   protected AmbientCreature(EntityType<? extends AmbientCreature> p_27403_, Level p_27404_) {
      super(p_27403_, p_27404_);
   }

   public boolean canBeLeashed(Player p_27406_) {
      return false;
   }
}
package thebetweenlands.herblore.elixirs.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.decay.DecayManager;

public class ElixirRipening extends ElixirEffect {
	public ElixirRipening(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(!entity.worldObj.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(DecayManager.isDecayEnabled(player)) {
				DecayManager.setDecayLevel(DecayManager.getDecayLevel(player) + 1, player);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerHeal = 100 >> strength;
		return ticksPerHeal > 0 ? ticks % ticksPerHeal == 0 : true;
	}
}

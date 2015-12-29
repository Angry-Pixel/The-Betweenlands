package thebetweenlands.herblore.elixirs.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.decay.DecayManager;

public class ElixirDecay extends ElixirEffect {
	public ElixirDecay(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(!entity.worldObj.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(DecayManager.isDecayEnabled(player)) {
				DecayManager.setDecayLevel(DecayManager.getDecayLevel(player) - 1, player);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerDecay = 100 >> strength;
		return ticksPerDecay > 0 ? ticks % ticksPerDecay == 0 : true;
	}
}

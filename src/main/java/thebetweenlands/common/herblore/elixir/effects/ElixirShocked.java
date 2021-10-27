package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.network.clientbound.MessageShockParticle;

public class ElixirShocked extends ElixirEffect {

	public ElixirShocked(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
		this.addAttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED, "4cde9a4b-1e4f-4a4e-a41b-036b0a79d94c", -0.95f, 2);
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
	
	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier) {
		if(!entityLivingBaseIn.world.isRemote && entityLivingBaseIn.world.rand.nextInt(20) == 0) {
			TheBetweenlands.networkWrapper.sendToAllTracking(new MessageShockParticle(entityLivingBaseIn), entityLivingBaseIn);
		}
	}
	
}

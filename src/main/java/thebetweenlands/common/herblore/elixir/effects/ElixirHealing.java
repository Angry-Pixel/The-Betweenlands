package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class ElixirHealing extends ElixirEffect {
	public ElixirHealing(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(!entity.world.isRemote && entity.getHealth() < entity.getMaxHealth()) {
			entity.heal(1.0F);
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerHeal = 50 >> strength;
		return ticksPerHeal > 0 ? ticks % ticksPerHeal == 0 : true;
	}
}

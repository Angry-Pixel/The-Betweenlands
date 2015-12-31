package thebetweenlands.herblore.elixirs.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

public class ElixirDraining extends ElixirEffect {
	public ElixirDraining(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(!entity.worldObj.isRemote) {
			entity.attackEntityFrom(DamageSource.magic, 1);
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerDamage = 50 >> strength;
		return ticksPerDamage > 0 ? ticks % ticksPerDamage == 0 : true;
	}
}

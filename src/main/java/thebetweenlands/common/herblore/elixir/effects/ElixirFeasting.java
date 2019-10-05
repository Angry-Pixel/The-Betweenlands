package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ElixirFeasting extends ElixirEffect {
	public ElixirFeasting(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(!entity.world.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(player.getFoodStats().needFood()) {
				player.getFoodStats().addStats(1, 0.5F);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerFood = 100 >> strength;
		return ticksPerFood > 0 ? ticks % ticksPerFood == 0 : true;
	}
}

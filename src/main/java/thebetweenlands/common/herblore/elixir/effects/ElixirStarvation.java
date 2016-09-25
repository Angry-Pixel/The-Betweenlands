package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ElixirStarvation extends ElixirEffect {
	public ElixirStarvation(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(!entity.worldObj.isRemote && entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(player.getFoodStats().getFoodLevel() > 0) {
				player.getFoodStats().addStats(-1, 5.0F);
			} else {
				player.getFoodStats().setFoodLevel(0);
			}
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		int ticksPerStarve = 80 >> strength;
		return ticksPerStarve > 0 ? ticks % ticksPerStarve == 0 : true;
	}
}

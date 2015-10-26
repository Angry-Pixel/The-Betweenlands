package thebetweenlands.herblore.elixirs.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class ElixirSwiftarm extends ElixirEffect {
	public ElixirSwiftarm(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(entity instanceof EntityPlayer) {
			//System.out.println("tes");
			EntityPlayer player = (EntityPlayer) entity;
			if(player.getSwingProgress(0) == 20) player.swingItem();
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}

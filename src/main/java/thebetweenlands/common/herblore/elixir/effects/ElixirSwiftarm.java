package thebetweenlands.common.herblore.elixir.effects;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;

public class ElixirSwiftarm extends ElixirEffect {
	public ElixirSwiftarm(int id, String name, ResourceLocation icon) {
		super(id, name, icon);
	}

	@Override
	protected void performEffect(EntityLivingBase entity, int strength) {
		if(entity instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) entity;
			if(player.swingProgress >= 20) player.swingArm(EnumHand.MAIN_HAND);
		}
	}

	@Override
	protected boolean isReady(int ticks, int strength) {
		return true;
	}
}

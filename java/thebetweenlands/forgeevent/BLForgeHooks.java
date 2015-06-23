package thebetweenlands.forgeevent;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import thebetweenlands.forgeevent.entity.LivingSetRevengeTargetEvent;
import thebetweenlands.forgeevent.entity.player.PlayerEventGetHurtSound;

public final class BLForgeHooks {
	private BLForgeHooks() {
	}

	public static void onLivingSetRevengeTarget(EntityLivingBase entity, EntityLivingBase target) {
		MinecraftForge.EVENT_BUS.post(new LivingSetRevengeTargetEvent(entity, target));
	}

	public static String onPlayerGetHurtSound(EntityPlayer player) {
		PlayerEventGetHurtSound event = new PlayerEventGetHurtSound(player, "game.player.hurt");
		MinecraftForge.EVENT_BUS.post(event);
		return event.hurtSound;
	}
}

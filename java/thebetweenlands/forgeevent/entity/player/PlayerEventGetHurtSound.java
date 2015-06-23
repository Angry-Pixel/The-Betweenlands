package thebetweenlands.forgeevent.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerEventGetHurtSound extends PlayerEvent {
	public String hurtSound;

	public PlayerEventGetHurtSound(EntityPlayer player, String hurtSound) {
		super(player);
		this.hurtSound = hurtSound;
	}
}

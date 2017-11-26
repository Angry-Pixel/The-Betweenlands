package thebetweenlands.api.entity;

import net.minecraft.entity.player.EntityPlayer;
import thebetweenlands.common.sound.BLSoundEvent;

public interface IEntityMusic {
	public BLSoundEvent getMusicFile(EntityPlayer listener);
	public double getMusicRange(EntityPlayer listener);
	public boolean isMusicActive(EntityPlayer listener);
}

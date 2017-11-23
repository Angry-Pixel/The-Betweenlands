package thebetweenlands.api.entity;

import net.minecraft.entity.player.EntityPlayer;

public interface IEntityMusic {
	public String getMusicFile(EntityPlayer listener);
	public double getMusicRange(EntityPlayer listener);
	public boolean isMusicActive(EntityPlayer listener);
}

package thebetweenlands.api.audio;

import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;

public interface IEntitySound extends ISound {
	public Entity getMusicEntity();
	
	public void stopEntityMusic();
}

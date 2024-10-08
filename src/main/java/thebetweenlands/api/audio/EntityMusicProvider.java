package thebetweenlands.api.audio;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import thebetweenlands.api.entity.MusicPlayer;

public interface EntityMusicProvider {
	@Nullable
	MusicPlayer getEntityMusic(Entity entity);
}

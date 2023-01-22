package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;

public interface IEntityMusicProvider {
	@Nullable
	public IEntityMusic getEntityMusic(Entity entity);
}

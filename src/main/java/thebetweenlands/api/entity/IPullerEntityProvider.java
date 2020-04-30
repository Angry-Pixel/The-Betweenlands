package thebetweenlands.api.entity;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import thebetweenlands.common.entity.draeton.DraetonPhysicsPart;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public interface IPullerEntityProvider<T extends Entity & IPullerEntity> {
	/**
	 * Creates a Draeton puller entity variant of this entity.
	 * The returned entity may be the same as this entity.
	 * @param draeton
	 * @param puller
	 * @return
	 */
	@Nullable
	public T createPuller(EntityDraeton draeton, DraetonPhysicsPart puller);
}

package thebetweenlands.api.entity;

import net.minecraft.entity.Entity;
import thebetweenlands.common.entity.draeton.DraetonPhysicsPart;
import thebetweenlands.common.entity.draeton.EntityDraeton;

public interface IPullerEntity {
	public void setPuller(EntityDraeton carriage, DraetonPhysicsPart puller);

	public float getPull(float pull);

	public float getCarriageDrag(float drag);

	public float getDrag(float drag);

	public Entity createReleasedEntity();
	
	public void spawnReleasedEntity();
}
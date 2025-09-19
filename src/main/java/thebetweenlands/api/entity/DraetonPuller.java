package thebetweenlands.api.entity;

import net.minecraft.world.entity.Entity;

public interface DraetonPuller {

//	@Nullable
//	Draeton getCarriage();

//	void setPuller(Draeton carriage, DraetonPhysicsPart puller);

	float getPull(float pull);

	float getCarriageDrag(float drag);

	float getDrag(float drag);

	Entity createReleasedEntity();

	void spawnReleasedEntity();
}

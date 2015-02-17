package thebetweenlands.world.teleporter;

import net.minecraft.entity.Entity;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

public class TeleporterBetweenlands extends Teleporter
{
	//TODO: No special functionality yet, just for testing purposes
	public TeleporterBetweenlands(WorldServer worldServer) {
		super(worldServer);
	}

	//Just putting this here to stop nether portals appearing in the overworld
	@Override
	public void placeInPortal(Entity pEntity, double posX, double posY, double posZ, float rotationYaw) {
	}
}
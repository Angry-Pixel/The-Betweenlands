package thebetweenlands.client.audio;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import thebetweenlands.common.entity.mobs.EntityFortressBossTeleporter;
import thebetweenlands.common.registries.SoundRegistry;

public class TeleporterSound extends EntitySound<EntityFortressBossTeleporter> {
	public TeleporterSound(EntityFortressBossTeleporter teleporter, Entity target) {
		super(SoundRegistry.FORTRESS_TELEPORT, SoundCategory.HOSTILE, teleporter, e -> e.getTarget() != null && e.getTarget() == target && target.isEntityAlive());
	}
}

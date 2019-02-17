package thebetweenlands.client.audio;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import thebetweenlands.common.entity.mobs.EntityPrimordialMalevolenceTeleporter;
import thebetweenlands.common.registries.SoundRegistry;

public class TeleporterSound extends EntitySound<EntityPrimordialMalevolenceTeleporter> {
	public TeleporterSound(EntityPrimordialMalevolenceTeleporter teleporter, Entity target) {
		super(SoundRegistry.FORTRESS_TELEPORT, SoundCategory.HOSTILE, teleporter, e -> e.getTarget() != null && e.getTarget() == target && target.isAlive());
	}
}

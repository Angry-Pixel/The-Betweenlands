package thebetweenlands.client.audio;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import thebetweenlands.common.entity.boss.malevolence.PrimordialMalevolenceTeleporter;
import thebetweenlands.common.registries.SoundRegistry;

public class TeleporterSoundInstance extends DefaultEntitySoundInstance<PrimordialMalevolenceTeleporter> {
	public TeleporterSoundInstance(PrimordialMalevolenceTeleporter entity, Entity target) {
		super(SoundRegistry.FORTRESS_TELEPORT.get(), SoundSource.HOSTILE, entity, e -> e.getTarget() != null && e.getTarget() == target && target.isAlive());
	}
}

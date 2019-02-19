package thebetweenlands.client.audio;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.SoundCategory;
import thebetweenlands.common.entity.mobs.EntityPrimordialMalevolence;
import thebetweenlands.common.registries.SoundRegistry;

public class FortressBossIdleSound extends EntitySound<EntityPrimordialMalevolence> {
	public FortressBossIdleSound(EntityPrimordialMalevolence boss) {
		super(SoundRegistry.FORTRESS_BOSS_LIVING, SoundCategory.HOSTILE, boss, EntityLivingBase::isAlive);
		repeat = false;
	}
}

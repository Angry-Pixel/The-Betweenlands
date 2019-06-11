package thebetweenlands.client.audio.ambience.list;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.entity.EntityGrapplingHookNode;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class SwingingAmbienceType extends AmbienceType {
	@Override
	public boolean isActive() {
		Entity ridingEntity = this.getPlayer().getRidingEntity();
		return ridingEntity instanceof EntityGrapplingHookNode;
	}

	@Override
	public AmbienceLayer getAmbienceLayer() {
		return AmbienceRegistry.BASE_LAYER;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getFadeTime() {
		return 10;
	}
	
	@Override
	public float getVolume() {
		Entity ridingEntity = this.getPlayer().getRidingEntity();
		if(ridingEntity != null) {
			double dx = ridingEntity.posX - ridingEntity.prevPosX;
			double dy = ridingEntity.posY - ridingEntity.prevPosY;
			double dz = ridingEntity.posZ - ridingEntity.prevPosZ;
			return (1.0F - (float) Math.pow(1.0F - Math.min((float) Math.sqrt(dx*dx + dy*dy + dz*dz) * 1.5F, 2.0F) / 2.0F, 1 / 2.0F)) * 1.25F;
		}
		return 0.0F;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.ROPE_SWING;
	}

	@Override
	public float getLowerPriorityVolume() {
		return 1;
	}
	
	@Override
	public boolean isActiveInWorld(World world) {
		return true;
	}
}

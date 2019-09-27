package thebetweenlands.client.audio.ambience.list;

import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.entity.EntityGrapplingHookNode;
import thebetweenlands.common.entity.EntityVolarkite;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class WindAmbienceType extends AmbienceType {
	@Override
	public boolean isActive() {
		Entity ridingEntity = this.getPlayer().getRidingEntity();
		return ridingEntity instanceof EntityGrapplingHookNode || ridingEntity instanceof EntityVolarkite;
	}

	@Override
	public AmbienceLayer getAmbienceLayer() {
		return AmbienceRegistry.DETAIL_LAYER;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getFadeTime() {
		return 10;
	}
	
	private double getVelocity(Entity entity) {
		double dx = entity.posX - entity.prevPosX;
		double dy = entity.posY - entity.prevPosY;
		double dz = entity.posZ - entity.prevPosZ;
		return Math.sqrt(dx*dx + dy*dy + dz*dz);
	}
	
	private double getSoundVariation(Entity entity) {
		return (Math.sin(entity.ticksExisted * 0.1D) + 1) / 2.0D * (Math.cos(entity.ticksExisted * 0.2D) + 1) / 2.0D * (Math.sin(entity.ticksExisted * 0.3D) + 1) / 2.0D * (Math.cos(entity.ticksExisted * 0.4D) + 1) / 2.0D;
	}
	
	@Override
	public float getVolume() {
		Entity ridingEntity = this.getPlayer().getRidingEntity();
		if(ridingEntity != null) {
			return (1.0F - (float) Math.pow(1.0F - Math.min((float) this.getVelocity(ridingEntity) * 1.5F, 2.0F) / 2.0F, 1 / 2.0F)) * 1.25F * (float)(1 - this.getSoundVariation(this.getPlayer()) * 0.5D);
		}
		return 0.0F;
	}

	@Override
	public float getPitch() {
		Entity ridingEntity = this.getPlayer().getRidingEntity();
		if(ridingEntity != null) {
			return 0.9F + (1.0F - (float) Math.pow(1.0F - Math.min((float) this.getVelocity(ridingEntity) * 1.5F, 4.0F) / 4.0F, 1 / 2.0F)) * 1.8F * (float)(1 - this.getSoundVariation(this.getPlayer()) * 0.5D);
		}
		return 1.0F;
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

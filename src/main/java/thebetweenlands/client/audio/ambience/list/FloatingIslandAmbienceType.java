package thebetweenlands.client.audio.ambience.list;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class FloatingIslandAmbienceType extends AmbienceType {
	private float volume;
	
	protected float getSoundStrength() {
		EntityPlayer player = this.getPlayer();
		
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(player.world);
		
		List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, player.getEntityBoundingBox().grow(8), location -> location.getType() == EnumLocationType.FLOATING_ISLAND);
		
		if(!locations.isEmpty()) {
			double minDist = Double.MAX_VALUE;
			
			for(LocationStorage location : locations) {
				AxisAlignedBB aabb = location.getBoundingBox();
				
				double px = MathHelper.clamp(player.posX, aabb.minX, aabb.maxX);
				double py = MathHelper.clamp(player.posY, aabb.minY, aabb.maxY);
				double pz = MathHelper.clamp(player.posZ, aabb.minZ, aabb.maxZ);

				double dx = player.posX - px;
				double dy = player.posY - py;
				double dz = player.posZ - pz;
				
				double dst = MathHelper.sqrt(dx*dx + dy*dy + dz*dz);
				
				minDist = Math.min(minDist, dst);
			}
			
			return 1.0f - Math.min(1.0f, (float)minDist / 8.0f);
		}
		
		return 0.0f;
	}
	
	@Override
	public boolean isActive() {
		this.volume = this.getSoundStrength();
		return this.volume > 0.01f;
	}

	@Override
	public float getVolume() {
		return this.volume;
	}
	
	@Override
	public AmbienceLayer getAmbienceLayer() {
		return AmbienceRegistry.BASE_LAYER;
	}

	@Override
	public int getPriority() {
		return 5;
	}

	@Override
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_FLOATING_ISLAND;
	}
}

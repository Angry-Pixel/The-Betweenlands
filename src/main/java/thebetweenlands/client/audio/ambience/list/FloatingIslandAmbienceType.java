package thebetweenlands.client.audio.ambience.list;

import java.util.List;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.WorldStorageGetter;
import thebetweenlands.common.world.storage.location.EnumLocationType;
import thebetweenlands.common.world.storage.location.LocationStorage;

public class FloatingIslandAmbienceType extends AmbienceType {

	private float volume;

	protected float getSoundStrength() {
		Player player = this.getPlayer();

		BetweenlandsWorldStorage worldStorage = WorldStorageGetter.getNullable(player.level());

		if (worldStorage != null) {
			List<LocationStorage> locations = worldStorage.getLocalStorageHandler().getLocalStorages(player.level(), LocationStorage.class, player.getBoundingBox().inflate(8), location -> location.getType() == EnumLocationType.FLOATING_ISLAND);

			if (!locations.isEmpty()) {
				double minDist = Double.MAX_VALUE;

				for (LocationStorage location : locations) {
					AABB aabb = location.getBoundingBox();

					double px = Mth.clamp(player.getX(), aabb.minX, aabb.maxX);
					double py = Mth.clamp(player.getY(), aabb.minY, aabb.maxY);
					double pz = Mth.clamp(player.getZ(), aabb.minZ, aabb.maxZ);

					double dx = player.getX() - px;
					double dy = player.getY() - py;
					double dz = player.getZ() - pz;

					double dst = Mth.sqrt((float) (dx * dx + dy * dy + dz * dz));

					minDist = Math.min(minDist, dst);
				}

				return 1.0f - Math.min(1.0f, (float) minDist / 8.0f);
			}
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
	public SoundSource getCategory() {
		return SoundSource.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.AMBIENT_FLOATING_ISLAND.get();
	}
}

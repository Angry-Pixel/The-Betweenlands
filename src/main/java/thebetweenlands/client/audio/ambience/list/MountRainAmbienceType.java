package thebetweenlands.client.audio.ambience.list;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class MountRainAmbienceType extends AmbienceType {

	private float getAirRainStrength() {
		Player player = this.getPlayer();
		BlockPos pos = player.blockPosition();
		if(!player.level().isRainingAt(pos.above(2))) {
			return 0;
		}
		BlockPos surface = player.level().getHeightmapPos(Heightmap.Types.WORLD_SURFACE, pos);
		float distance = (float)(player.getY() - surface.getY());
		if(distance > -1) {
			return Mth.clamp((distance + 1) / 3.0f + 0.3f, 0, 1);
		}
		return 0.0f;
	}

	//TODO
	@Override
	public boolean isActive() {
		Entity ridingEntity = this.getPlayer().getVehicle();
		return false/*(ridingEntity instanceof EntityVolarkite || ridingEntity instanceof EntityDraeton)*/ && this.getAirRainStrength() > 0.01f;
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

	@Override
	public float getVolume() {
		return this.getAirRainStrength() * 0.5f;
	}

	@Override
	public SoundSource getCategory() {
		return SoundSource.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.RAIN_MOUNT.get();
	}

	@Override
	public float getLowerPriorityVolume() {
		return 1;
	}

	@Override
	public boolean isActiveInWorld(Level level) {
		return true;
	}
}

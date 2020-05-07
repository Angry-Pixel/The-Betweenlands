package thebetweenlands.client.audio.ambience.list;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.entity.EntityVolarkite;
import thebetweenlands.common.entity.draeton.EntityDraeton;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;

public class MountRainAmbienceType extends AmbienceType {
	private float getAirRainStrength() {
		EntityPlayer player = this.getPlayer();
		BlockPos pos = new BlockPos(player);
		if(!player.world.isRainingAt(pos.up(2))) {
			return 0;
		}
		BlockPos surface = player.world.getHeight(pos);
		float distance = (float)(player.posY - surface.getY());
		if(distance > -1) {
			return MathHelper.clamp((distance + 1) / 3.0f + 0.3f, 0, 1);
		}
		return 0.0f;
	}

	@Override
	public boolean isActive() {
		Entity ridingEntity = this.getPlayer().getRidingEntity();
		return (ridingEntity instanceof EntityVolarkite || ridingEntity instanceof EntityDraeton) && this.getAirRainStrength() > 0.01f;
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
	public SoundCategory getCategory() {
		return SoundCategory.AMBIENT;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.RAIN_MOUNT;
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

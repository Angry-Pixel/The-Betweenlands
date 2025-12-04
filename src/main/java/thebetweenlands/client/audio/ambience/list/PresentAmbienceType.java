package thebetweenlands.client.audio.ambience.list;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.client.audio.ambience.AmbienceLayer;
import thebetweenlands.client.audio.ambience.AmbienceType;
import thebetweenlands.common.block.entity.PresentBlockEntity;
import thebetweenlands.common.registries.AmbienceRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.world.event.WinterEvent;

public class PresentAmbienceType extends AmbienceType {

	@Nullable
	protected PresentBlockEntity getClosestPresent(Player player, double range) {
		int sx = Mth.floor(player.getX() - range) >> 4;
		int sz = Mth.floor(player.getZ() - range) >> 4;
		int ex = Mth.floor(player.getX() + range) >> 4;
		int ez = Mth.floor(player.getZ() + range) >> 4;
		PresentBlockEntity closest = null;
		for (int cx = sx; cx <= ex; cx++) {
			for (int cz = sz; cz <= ez; cz++) {
				ChunkAccess chunk = player.level().getChunk(cx, cz);
				for (BlockPos entityPos : chunk.getBlockEntitiesPos()) {
					BlockEntity tile = player.level().getBlockEntity(entityPos);
					if (tile instanceof PresentBlockEntity present) {
						double dstSq = entityPos.distToCenterSqr(player.position());
						if (dstSq <= range * range && (closest == null || dstSq <= closest.getBlockPos().distToCenterSqr(player.position()))) {
							closest = present;
						}
					}
				}
			}
		}
		return closest;
	}

	@Override
	public boolean isActive() {
		return WinterEvent.isFroooosty(this.getPlayer().level()) && this.getClosestPresent(this.getPlayer(), 32.0D) != null;
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
	public SoundSource getCategory() {
		return SoundSource.RECORDS;
	}

	@Override
	public SoundEvent getSound() {
		return SoundRegistry.CHRISTMAS_ON_THE_MARSH.get();
	}

	@Override
	public float getVolume() {
		PresentBlockEntity present = this.getClosestPresent(this.getPlayer(), 32.0D);
		if (present != null) {
			float volume = (1 - Mth.clamp((float) Math.sqrt(present.getBlockPos().distToCenterSqr(this.getPlayer().position())) / 64.0F, 0, 1));
			return volume * volume * 0.3F;
		}
		return 0.0F;
	}

	@Override
	public float getLowerPriorityVolume() {
		return 1.0F;
	}
}

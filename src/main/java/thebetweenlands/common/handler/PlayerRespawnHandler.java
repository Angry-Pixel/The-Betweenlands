package thebetweenlands.common.handler;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.player.PlayerRespawnPositionEvent;
import thebetweenlands.api.entity.spawning.WeightProvider;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.util.WeightedList;

import javax.annotation.Nullable;
import java.util.Collections;

public class PlayerRespawnHandler {

	public static void handleRespawnInDimension(PlayerRespawnPositionEvent event) {
		if (event.getEntity() instanceof ServerPlayer player) {
			BlockPos spawnPos = player.getRespawnPosition();
			DimensionTransition oldTransition = event.getDimensionTransition();

			//only run when spawning in betweenlands config is true and we havent set our spawn
			if (BetweenlandsConfig.startInBetweenlands && (spawnPos == null || oldTransition.missingRespawnBlock())) {
				//if our spawn point is obstructed, place us near our last used portal (if it exists)
				if (oldTransition.missingRespawnBlock() && spawnPos != null) {
					CompoundTag tag = player.getPersistentData();
					CompoundTag persistentTag = tag.getCompound(Player.PERSISTED_NBT_TAG);
					//TODO once telporter is back in business
//					if (persistentTag.contains(BetweenlandsTeleporter.LAST_PORTAL_POS_NBT, Tag.TAG_LONG)) {
//						BlockPos lastPortal = BlockPos.of(persistentTag.getLong(BetweenlandsTeleporter.LAST_PORTAL_POS_NBT));
//
//						BlockPos placePos = getRespawnPointNearPos(player.level(), lastPortal, 64, event.isFromEndFight());
//						event.setDimensionTransition(new DimensionTransition(player.getServer().getLevel(DimensionRegistries.DIMENSION_KEY), placePos.getBottomCenter(), Vec3.ZERO, player.getYRot(), player.getXRot(), DimensionTransition.DO_NOTHING));
//						return;
//					}
				}

				//if this is a fresh spawn or our spawn point is not set, place us somewhere "safe"
				//(note: safety not guaranteed)
				BlockPos adjustedPos = player.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BlockPos.containing(oldTransition.pos()));
				event.setDimensionTransition(new DimensionTransition(player.getServer().getLevel(DimensionRegistries.DIMENSION_KEY), adjustedPos.getBottomCenter(), Vec3.ZERO, player.getYRot(), player.getXRot(), DimensionTransition.DO_NOTHING));
			}
		}
	}

	public static BlockPos getRespawnPointNearPos(ServerLevel level, BlockPos pos, int fuzz, boolean normalTeleport) {
		BlockPos result = getSpawnPointNearPos(level, pos, fuzz, true, 16, 3, normalTeleport);
		if (result == null) {
			int spawnFuzzHalf = fuzz / 2;
			result = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, pos.offset(level.getRandom().nextInt(fuzz) - spawnFuzzHalf, 0, level.getRandom().nextInt(fuzz) - spawnFuzzHalf));
		}
		return result;
	}

	@Nullable
	public static BlockPos getSpawnPointNearPos(ServerLevel level, BlockPos pos, int fuzz, boolean surface, int yRange, int xzSkip, boolean normalTeleport) {
		xzSkip = Math.max(xzSkip, 1);

		int spawnFuzzHalf = fuzz / 2;

		class WeightedPos implements WeightProvider {
			final BlockPos pos;
			short weight;

			WeightedPos(BlockPos pos) {
				this.pos = pos;
			}

			@Override
			public short getWeight() {
				return this.weight;
			}
		}

		short maxWeight = 0;

		WeightedList<WeightedPos> spawnCandidates = new WeightedList<>();

		BlockPos.MutableBlockPos checkPos = new BlockPos.MutableBlockPos();

		for (int xo = -spawnFuzzHalf; xo <= spawnFuzzHalf; xo += 1 + level.getRandom().nextInt(xzSkip)) {
			for (int zo = -spawnFuzzHalf; zo <= spawnFuzzHalf; zo += 1 + level.getRandom().nextInt(xzSkip)) {
				checkPos.set(pos.getX() + xo, 0, pos.getZ() + zo);

				ChunkAccess chunk = level.getChunk(checkPos);

				for (int yo = yRange; surface || yo >= -yRange; yo--) {
					if (surface) {
						checkPos.set(checkPos.getX(), chunk.getBlockFloorHeight(checkPos), checkPos.getZ());
					} else {
						checkPos.set(checkPos.getX(), pos.getY() + yo, checkPos.getZ());
					}

					if (Math.abs(checkPos.getY() - pos.getY()) <= yRange && ServerPlayer.findRespawnAndUseSpawnBlock(level, checkPos, 0.0F, true, normalTeleport).isPresent()) {
						checkPos.setY(checkPos.getY() - 1);

						BlockState stateDown = chunk.getBlockState(checkPos);
						if (stateDown.blocksMotion() && !stateDown.is(BlockTags.LEAVES)) {
							BlockPos newPos = checkPos.above();
							WeightedPos p = new WeightedPos(newPos);
							p.weight = (short) Math.abs(newPos.getY() - pos.getY());
							maxWeight = (short) Math.max(maxWeight, p.weight);
							spawnCandidates.add(p);
						}
					}

					if (surface) {
						break;
					}
				}
			}
		}

		if (!spawnCandidates.isEmpty()) {
			Collections.shuffle(spawnCandidates);

			for (WeightedPos p : spawnCandidates) {
				p.weight = (short) (maxWeight - p.weight);
				p.weight = (short) Math.pow(p.weight, 1.5D);
			}

			spawnCandidates.recalculateWeight();

			return spawnCandidates.getRandomItem(level.getRandom()).pos;
		}

		return null;
	}
}

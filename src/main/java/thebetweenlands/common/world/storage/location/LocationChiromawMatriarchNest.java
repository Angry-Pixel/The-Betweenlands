package thebetweenlands.common.world.storage.location;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawHatchling;
import thebetweenlands.common.entity.monster.chiromaw.ChiromawMatriarch;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.EntityRegistry;

import javax.annotation.Nullable;

public class LocationChiromawMatriarchNest extends LocationGuarded {
	private static final int RESPAWN_TIME = 20 * 6; //20 * 6 * 10s = 20min.

	@Nullable
	private BlockPos nest;

	private int respawnCounter = 0;

	public LocationChiromawMatriarchNest(IWorldStorage worldStorage, StorageID id, LocalRegion region) {
		super(worldStorage, id, region, "chiromaw_matriarch_nest", EnumLocationType.CHIROMAW_MATRIARCH_NEST);
	}

	public LocationChiromawMatriarchNest(IWorldStorage worldStorage, StorageID id, LocalRegion region, BlockPos nest) {
		super(worldStorage, id, region, "chiromaw_matriarch_nest", EnumLocationType.CHIROMAW_MATRIARCH_NEST);
		this.setNestPosition(nest);
	}

	public void setNestPosition(BlockPos nest) {
		this.nest = nest;
	}

	@Nullable
	public BlockPos getNestPosition() {
		return this.nest;
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt = super.writeToNBT(nbt);
		if (this.nest != null) {
			nbt.putInt("NestX", this.nest.getX());
			nbt.putInt("NestY", this.nest.getY());
			nbt.putInt("NestZ", this.nest.getZ());
		}
		nbt.putInt("RespawnCounter", this.respawnCounter);
		return nbt;
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		this.nest = new BlockPos(nbt.getInt("NestX"), nbt.getInt("NestY"), nbt.getInt("NestZ"));
		this.respawnCounter = nbt.getInt("RespawnCounter");
	}

	@Override
	public void tick(Level level) {
		super.tick(level);

		if (!level.isClientSide() && this.nest != null && !this.getGuard().isClear(level)) {
			//Check for player claiming
			if (!level.getEntitiesOfClass(Player.class, new AABB(this.nest), player -> !player.isCreative() && !player.isSpectator()).isEmpty()) {
				this.getGuard().clear(level);

				this.setVisible(false);

				for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox())) {
					player.displayClientMessage(Component.translatable("location.thebetweenlands.chiromaw_matriarch_nest.tainted"), false);

					AdvancementCriteriaRegistry.CHIROMAW_MATRIARCH_NEST_CLAIMED.get().trigger(player);
				}
			}

			//Check for respawn
			if (level.getGameTime() % 200 == 0 && level.getEntitiesOfClass(ChiromawMatriarch.class, this.getBoundingBox().inflate(160)).isEmpty()) {
				this.respawnCounter++;

				if (this.respawnCounter >= RESPAWN_TIME) {
					this.respawnCounter = 0;

					ChiromawMatriarch matriarch = new ChiromawMatriarch(EntityRegistry.CHIROMAW_MATRIARCH.get(), level);
					matriarch.setPos(this.nest.getX() + 0.5D, this.nest.getY() + 0.01D, this.nest.getZ() + 0.5D);

					if (matriarch.checkSpawnObstruction(level)) {
						matriarch.finalizeSpawn((ServerLevelAccessor) level, level.getCurrentDifficultyAt(this.nest), MobSpawnType.STRUCTURE, null);
						level.addFreshEntity(matriarch);
					} else {
						matriarch.discard();
					}

					if (level.getEntitiesOfClass(ChiromawHatchling.class, this.getBoundingBox()).isEmpty()) {
						for (Direction facing : Direction.Plane.HORIZONTAL) {
							if (level.getRandom().nextBoolean()) {
								BlockPos pos = this.nest.relative(facing).below();

								ChiromawHatchling egg = new ChiromawHatchling(EntityRegistry.CHIROMAW_HATCHLING.get(), level);
								egg.setPos(pos.getX() + 0.5D, pos.getY() + 0.01D, pos.getZ() + 0.5D);

								if (egg.checkSpawnObstruction(level)) {
									level.addFreshEntity(egg);
								} else {
									egg.discard();
								}
							}
						}
					}
				}
			}
		}
	}
}

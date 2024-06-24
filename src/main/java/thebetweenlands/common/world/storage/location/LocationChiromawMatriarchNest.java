package thebetweenlands.common.world.storage.location;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import thebetweenlands.api.storage.IWorldStorage;
import thebetweenlands.api.storage.LocalRegion;
import thebetweenlands.api.storage.StorageID;

public class LocationChiromawMatriarchNest extends LocationGuarded {
	private static final int RESPAWN_TIME = 20 * 6; //20 * 6 * 10s = 20min.

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

	public BlockPos getNestPosition() {
		return this.nest;
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbt) {
		nbt = super.writeToNBT(nbt);
		if(this.nest != null) {
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
	public void tick() {
		super.tick();

		Level level = this.getWorldStorage().getLevel();
		if(!level.isClientSide() && this.nest != null && !this.getGuard().isClear(level)) {
			//Check for player claiming
			if(!level.getEntitiesOfClass(Player.class, new AABB(this.nest), player -> !player.isCreative() && !player.isSpectator()).isEmpty()) {
				this.getGuard().clear(level);

				this.setVisible(false);

				for(ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, this.getBoundingBox())) {
					player.displayClientMessage(Component.translatable("chat.chiromaw_matriarch_nest.tainted"), false);

					AdvancementCriterionRegistry.CHIROMAW_MATRIARCH_NEST_CLAIMED.trigger(player);
				}
			}

			//FIXME reimplement once chiromaws are added back
			//Check for respawn
//			if(level.getGameTime() % 200 == 0 && level.getEntitiesOfClass(EntityChiromawMatriarch.class, this.getBoundingBox().inflate(160)).isEmpty()) {
//				this.respawnCounter++;
//
//				if(this.respawnCounter >= RESPAWN_TIME) {
//					this.respawnCounter = 0;
//
//					EntityChiromawMatriarch matriarch = new EntityChiromawMatriarch(world);
//					matriarch.setPosition(this.nest.getX() + 0.5D, this.nest.getY() + 0.01D, this.nest.getZ() + 0.5D);
//
//					if(matriarch.isNotColliding()) {
//						matriarch.onInitialSpawn(level.getCurrentDifficultyAt(this.nest), null);
//						level.addFreshEntity(matriarch);
//					} else {
//						matriarch.discard();
//					}
//
//					if(level.getEntitiesOfClass(EntityChiromawHatchling.class, this.getBoundingBox()).isEmpty()) {
//						for(Direction facing : Direction.Plane.HORIZONTAL) {
//							if(level.getRandom().nextBoolean()) {
//								BlockPos pos = this.nest.relative(facing).below();
//
//								EntityChiromawHatchling egg = new EntityChiromawHatchling(level);
//								egg.setPosition(pos.getX() + 0.5D, pos.getY() + 0.01D, pos.getZ() + 0.5D);
//
//								if(egg.isNotColliding()) {
//									level.addFreshEntity(egg);
//								} else {
//									egg.discard();
//								}
//							}
//						}
//					}
//				}
//			}
		}
	}
}

package thebetweenlands.common.block.entity.spawner;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import thebetweenlands.common.block.entity.SyncedBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.BlockRegistry;

import javax.annotation.Nullable;

public class MobSpawnerBlockEntity extends SyncedBlockEntity implements Spawner {

	public float counter = 0.0F;
	public float lastCounter = 0.0F;

	private final BetweenlandsBaseSpawner spawner = new BetweenlandsBaseSpawner() {

		@Override
		public void broadcastEvent(Level level, BlockPos pos, int eventId) {
			level.blockEvent(pos, BlockRegistry.MOB_SPAWNER.get(), eventId, 0);
		}

		@Override
		protected void spawnParticles(Level level, BlockPos pos) {
			if(level.getRandom().nextInt(2) == 0) {
				double rx = level.getRandom().nextFloat();
				double ry = level.getRandom().nextFloat();
				double rz = level.getRandom().nextFloat();

				double len = Math.sqrt(rx * rx + ry * ry + rz * rz);
				float counter = -MobSpawnerBlockEntity.this.counter;

//				BLParticles.SPAWNER.spawn(level, (float) pos.getX() + rx, (float) pos.getY() + ry, (float) pos.getZ() + rz,
//					ParticleFactory.ParticleArgs.get().withMotion((rx - 0.5D) / len * 0.05D, (ry - 0.5D) / len * 0.05D, (rz - 0.5D) / len * 0.05D).withColor(1.0F, Mth.clamp(4 + (float) Math.sin(counter) * 3, 0, 1), Mth.clamp((float) Math.sin(counter) * 2, 0, 1), 0.65F));
			}
		}

		@Override
		public Either<BlockEntity, Entity> getOwner() {
			return Either.left(MobSpawnerBlockEntity.this);
		}

		@Override
		public void setChanged(@Nullable Level level, BlockPos pos) {
			if (level != null) {
				BlockState blockState = level.getBlockState(pos);
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	};

	public MobSpawnerBlockEntity(BlockPos pos, BlockState state) {
		super(BlockEntityRegistry.MOB_SPAWNER.get(), pos, state);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.spawner.load(this.level, this.worldPosition, tag);
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		this.spawner.save(tag);
	}

	public static void clientTick(Level level, BlockPos pos, BlockState state, MobSpawnerBlockEntity entity) {
		entity.lastCounter = entity.counter;
		entity.counter += 0.0085F;
		entity.spawner.clientTick(level, pos);
	}

	public static void serverTick(Level level, BlockPos pos, BlockState state, MobSpawnerBlockEntity entity) {
		entity.lastCounter = entity.counter;
		entity.counter += 0.0085F;
		entity.spawner.serverTick((ServerLevel)level, pos);
	}

	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
		CompoundTag compoundtag = this.saveCustomOnly(registries);
		compoundtag.remove("SpawnPotentials");
		return compoundtag;
	}

	@Override
	public boolean triggerEvent(int id, int type) {
		return this.spawner.onEventTriggered(this.level, id) || super.triggerEvent(id, type);
	}

	@Override
	public boolean onlyOpCanSetNbt() {
		return true;
	}

	@Override
	public void setEntityId(EntityType<?> type, RandomSource random) {
		this.spawner.setEntityId(type, this.level, random, this.worldPosition);
		this.setChanged();
	}

	public BetweenlandsBaseSpawner getSpawner() {
		return this.spawner;
	}
}

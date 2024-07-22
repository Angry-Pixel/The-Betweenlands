package thebetweenlands.common.block.entity.simulacrum;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.Spawner;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.SimulacrumEffect;
import thebetweenlands.common.block.entity.OfferingTableBlockEntity;
import thebetweenlands.common.block.entity.RepellerBlockEntity;
import thebetweenlands.common.block.entity.spawner.BetweenlandsBaseSpawner;
import thebetweenlands.common.registries.*;

import javax.annotation.Nullable;
import java.util.function.Predicate;

public class SimulacrumBlockEntity extends RepellerBlockEntity implements Spawner {

	private SimulacrumEffect effect = SimulacrumEffectRegistry.NONE.get();
	private SimulacrumEffect secondaryEffect = SimulacrumEffectRegistry.NONE.get();

	private boolean isActive = false;
	@Nullable
	private Component name;
	public int soundCooldown = 0;
	@Nullable
	public RepellerBlockEntity sourceRepeller;
	private boolean readFromNbt = false;

	private final BetweenlandsBaseSpawner mireSnailSpawner = new BetweenlandsBaseSpawner() {
		{
			this.setDelayRange(600, 1200);
			this.setSpawnInAir(false);
			this.setParticles(false);
			this.setCheckRange(24);
			this.setMaxSpawnCount(1);
		}

		@Override
		public void broadcastEvent(Level level, BlockPos pos, int eventId) {
		}

		@Override
		public Either<BlockEntity, Entity> getOwner() {
			return Either.left(SimulacrumBlockEntity.this);
		}

		@Override
		public void setChanged(@Nullable Level level, BlockPos pos) {
			if (level != null) {
				BlockState blockState = level.getBlockState(pos);
				level.sendBlockUpdated(pos, blockState, blockState, 3);
			}
		}
	};

	public SimulacrumBlockEntity(BlockPos pos, BlockState state) {
		super(pos, state);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);

		if (!this.readFromNbt) {
			this.mireSnailSpawner.delay(level, this.getBlockPos());
		}
	}

	@Override
	public BlockEntityType<?> getType() {
		return BlockEntityRegistry.SIMULACRUM.get();
	}

	public static void tick(Level level, BlockPos pos, BlockState state, SimulacrumBlockEntity entity) {
		if (entity.isActive()) {
			entity.effect.executeEffect(level, pos, state, entity);
			entity.secondaryEffect.executeEffect(level, pos, state, entity);
		}
	}

	@Nullable
	public Component getCustomName() {
		return this.name;
	}

	public void setCustomName(@Nullable Component name) {
		this.name = name;
	}

	public void setActive(Level level, BlockPos pos, BlockState state, boolean active) {
		this.isActive = active;
		level.sendBlockUpdated(pos, state, state, 2);
	}

	public boolean isActive() {
		return this.isActive;
	}

	public SimulacrumEffect getEffect() {
		return this.effect;
	}

	public void setEffect(SimulacrumEffect effect) {
		this.effect = effect;
		this.setSecondaryEffect(SimulacrumEffectRegistry.NONE.get());
		this.setChanged();
	}

	public SimulacrumEffect getSecondaryEffect() {
		return this.secondaryEffect;
	}

	public void setSecondaryEffect(SimulacrumEffect effect) {
		this.secondaryEffect = effect;
		this.setChanged();
	}

	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends BlockEntity> T getClosestActiveTile(Class<T> tileCls, @Nullable BlockEntity exclude, Level level, double x, double y, double z, double range, @Nullable SimulacrumEffect effect, @Nullable Predicate<ItemStack> offeringPredicate) {
		int sx = (Mth.floor(x - range) >> 4);
		int sz = (Mth.floor(z - range) >> 4);
		int ex = (Mth.floor(x + range) >> 4);
		int ez = (Mth.floor(z + range) >> 4);

		T closest = null;

		for (int cx = sx; cx <= ex; cx++) {
			for (int cz = sz; cz <= ez; cz++) {
				ChunkAccess chunk = level.getChunkSource().getChunkNow(cx, cz);

				if (chunk != null) {
					for (BlockPos pos : chunk.getBlockEntitiesPos()) {
						BlockEntity tile = level.getBlockEntity(pos);

						if (tile != exclude && tileCls.isInstance(tile)) {
							double dstSq = pos.distToCenterSqr(x, y, z);

							if (dstSq <= range * range && (closest == null || dstSq <= closest.getBlockPos().distToCenterSqr(x, y, z)) &&
								(effect == null || !(tile instanceof SimulacrumBlockEntity simulacrum) || simulacrum.getEffect() == effect && simulacrum.isActive()) &&
								(offeringPredicate == null || !(tile instanceof OfferingTableBlockEntity table) || offeringPredicate.test(table.getTheItem()))) {
								closest = (T) tile;
							}
						}
					}
				}
			}
		}

		return closest;
	}

	@Override
	protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.saveAdditional(tag, registries);
		tag.putString("effect_id", BLRegistries.SIMULACRUM_EFFECTS.getKey(this.effect).toString());
		tag.putString("secondary_effect_id", BLRegistries.SIMULACRUM_EFFECTS.getKey(this.secondaryEffect).toString());
		tag.putBoolean("is_active", this.isActive);
		if (this.name != null) {
			tag.putString("name", Component.Serializer.toJson(this.name, registries));
		}

		this.mireSnailSpawner.save(tag);
	}

	@Override
	protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
		super.loadAdditional(tag, registries);
		this.readFromNbt = true;

		this.effect = BLRegistries.SIMULACRUM_EFFECTS.get(ResourceLocation.parse(tag.getString("effect_id")));
		this.secondaryEffect = BLRegistries.SIMULACRUM_EFFECTS.get(ResourceLocation.parse(tag.getString("secondary_effect_id")));
		this.isActive = tag.getBoolean("is_active");
		if (tag.contains("name", Tag.TAG_STRING)) {
			this.name = parseCustomNameSafe(tag.getString("name"), registries);
		}

		this.mireSnailSpawner.load(this.getLevel(), this.getBlockPos(), tag);
	}

	@Override
	public void setChanged() {
		super.setChanged();
		if (this.getLevel() != null) {
			this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 2);
		}
	}

	public BetweenlandsBaseSpawner getSpawner() {
		return this.mireSnailSpawner;
	}

	@Override
	public void setEntityId(EntityType<?> entityType, RandomSource random) {
		this.mireSnailSpawner.setEntityId(EntityRegistry.MIRE_SNAIL.get(), this.getLevel(), random, this.getBlockPos());
	}
}

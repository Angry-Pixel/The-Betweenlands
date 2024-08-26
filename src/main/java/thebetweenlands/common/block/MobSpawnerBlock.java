package thebetweenlands.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import thebetweenlands.common.block.entity.spawner.BetweenlandsBaseSpawner;
import thebetweenlands.common.block.entity.spawner.MobSpawnerBlockEntity;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.function.Consumer;

public class MobSpawnerBlock extends SpawnerBlock {

	public MobSpawnerBlock(Properties properties) {
		super(properties);
	}

	@SafeVarargs
	@Nullable
	public static BetweenlandsBaseSpawner setMob(Level level, BlockPos pos, EntityType<?> type, Consumer<BetweenlandsBaseSpawner>... consumers) {
		BetweenlandsBaseSpawner spawnerLogic = getLogic(level, pos);
		if(spawnerLogic != null) {
			spawnerLogic.setNextEntityName(type, level, level.getRandom(), pos);
			for(Consumer<BetweenlandsBaseSpawner> c : consumers) {
				c.accept(spawnerLogic);
			}
		}
		return spawnerLogic;
	}

	@SafeVarargs
	@Nullable
	public static BetweenlandsBaseSpawner setRandomMob(Level level, BlockPos pos, RandomSource random, Consumer<BetweenlandsBaseSpawner>... consumers) {
		RandomSpawnerMob mob = RandomSpawnerMob.values()[random.nextInt(RandomSpawnerMob.values().length)];
		BetweenlandsBaseSpawner logic = setMob(level, pos, mob.getType());
		if(logic != null) {
			logic.setDelayRange(mob.getMinDelay(), mob.getMaxDelay());
			logic.setMaxEntities(mob.getMaxEntities());
			for(Consumer<BetweenlandsBaseSpawner> c : consumers) {
				c.accept(logic);
			}
		}
		return logic;
	}

	@Nullable
	public static BetweenlandsBaseSpawner getLogic(Level level, BlockPos pos) {
		if (level.getBlockEntity(pos) instanceof MobSpawnerBlockEntity spawner) {
			return spawner.getSpawner();
		}
		return null;
	}

	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		super.setPlacedBy(level, pos, state, placer, stack);
		if (!level.isClientSide()) {
			setRandomMob(level, pos, RandomSource.create());
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new MobSpawnerBlockEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		return createTickerHelper(type, BlockEntityRegistry.MOB_SPAWNER.get(), level.isClientSide() ? MobSpawnerBlockEntity::clientTick : MobSpawnerBlockEntity::serverTick);
	}

	public enum RandomSpawnerMob {
		SWAMP_HAG(EntityRegistry.SWAMP_HAG.get(), 200, 500, 4),
		WIGHT(EntityRegistry.WIGHT.get(), 400, 800, 2);//,
//		BLOOD_SNAIL(EntityRegistry.BLOOD_SNAIL.get(), 100, 400, 4),
//		TERMITE(EntityRegistry.TERMITE.get(), 100, 300, 6),
//		LEECH(EntityRegistry.LEECH.get(), 150, 500, 3);

		private final EntityType<?> type;
		private final int minDelay;
		private final int maxDelay;
		private final int maxEntities;

		RandomSpawnerMob(EntityType<?> type, int minDelay, int maxDelay, int maxEntities) {
			this.type = type;
			this.minDelay = minDelay;
			this.maxDelay = maxDelay;
			this.maxEntities = maxEntities;
		}

		public EntityType<?> getType() {
			return this.type;
		}

		public int getMinDelay() {
			return this.minDelay;
		}

		public int getMaxDelay() {
			return this.maxDelay;
		}

		public int getMaxEntities() {
			return this.maxEntities;
		}
	}
}

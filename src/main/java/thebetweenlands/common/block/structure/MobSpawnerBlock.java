package thebetweenlands.common.block.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import thebetweenlands.common.block.entity.spawner.BetweenlandsBaseSpawner;
import thebetweenlands.common.block.entity.spawner.MobSpawnerBlockEntity;
import thebetweenlands.common.block.waterlog.SwampWaterLoggable;
import thebetweenlands.common.registries.BlockEntityRegistry;
import thebetweenlands.common.registries.EntityRegistry;

import java.util.function.Consumer;

public class MobSpawnerBlock extends SpawnerBlock implements SwampWaterLoggable {

	public MobSpawnerBlock(Properties properties) {
		super(properties);
		this.registerDefaultState(this.getStateDefinition().any().setValue(WATER_TYPE, WaterType.NONE));
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

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) {
		return super.getStateForPlacement(context).setValue(WATER_TYPE, WaterType.getFromFluid(context.getLevel().getFluidState(context.getClickedPos()).getType()));
	}

	@Override
	protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
		if (state.getValue(WATER_TYPE) != WaterType.NONE) {
			level.scheduleTick(pos, state.getValue(WATER_TYPE).getFluid(), state.getValue(WATER_TYPE).getFluid().getTickDelay(level));
		}

		return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
	}

	@Override
	protected FluidState getFluidState(BlockState state) {
		return state.getValue(WATER_TYPE).getFluid().defaultFluidState();
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

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		super.createBlockStateDefinition(builder.add(WATER_TYPE));
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

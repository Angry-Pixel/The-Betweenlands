package thebetweenlands.common.block.farming;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import thebetweenlands.common.entity.creature.Sporeling;
import thebetweenlands.common.registries.AdvancementCriteriaRegistry;
import thebetweenlands.common.registries.EntityRegistry;
import thebetweenlands.common.registries.ItemRegistry;

import java.util.List;

public class FungusCropBlock extends DecayableCropBlock {

	private static final IntegerProperty AGE = IntegerProperty.create("age", 0, 3);
	private static final List<VoxelShape> SHAPES = List.of(
		Block.box(6, 0, 6, 10, 4, 10),
		Block.box(5, 0, 5, 11, 8, 11),
		Block.box(4, 0, 4, 12, 14, 12),
		Block.box(3, 0, 3, 13, 16, 13)
	);

	public FungusCropBlock(Properties properties) {
		super(properties);
	}

	@Override
	public int getMaxHeight() {
		return 1;
	}

	@Override
	public IntegerProperty getAgeProperty() {
		return AGE;
	}

	@Override
	public int getMaxAge() {
		return 3;
	}

	@Override
	protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
		return SHAPES.get(state.getValue(this.getAgeProperty()));
	}

	@Override
	protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		super.randomTick(state, level, pos, random);

		if (this.isDecayed(level, pos) && state.getValue(this.getAgeProperty()) >= this.getMaxAge() && random.nextInt(6) == 0) {
			Sporeling sporeling = new Sporeling(EntityRegistry.SPORELING.get(), level);
			sporeling.moveTo(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D, random.nextFloat() * 360.0F, 0.0F);
			level.addFreshEntity(sporeling);
			level.removeBlock(pos, false);
			this.harvestAndUpdateSoil(level, pos, 5);

			for (ServerPlayer player : level.getEntitiesOfClass(ServerPlayer.class, new AABB(pos).inflate(10.0D, 5.0D, 10.0D))) {
				AdvancementCriteriaRegistry.SPORELING_HATCH.get().trigger(player);
			}
		}
	}

	@Override
	protected float getGrowthChance(Level level, BlockPos pos, BlockState state, RandomSource random) {
		return 0.9F;
	}

	@Override
	protected ItemLike getBaseSeedId() {
		return ItemRegistry.SPORES;
	}
}

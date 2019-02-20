package thebetweenlands.common.block.structure;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import thebetweenlands.client.render.particle.BLParticles;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;

public class BlockDruidStone extends Block {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

	public final boolean active;

	public BlockDruidStone(boolean active) {
		super(Properties.create(Material.ROCK)
				.hardnessAndResistance(1.5F, 10.0F)
				.sound(SoundType.STONE)
				.lightValue(12));

		this.setDefaultState(this.getStateContainer().getBaseState().with(FACING, EnumFacing.NORTH));

		this.active = active;
	}

	@Override
	public int getHarvestLevel(IBlockState state) {
		return 0;
	}

	@Override
	public ToolType getHarvestTool(IBlockState state) {
		return ToolType.PICKAXE;
	}

	@Override
	protected void fillStateContainer(Builder<Block, IBlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING);
	}

	@Override
	public IBlockState rotate(IBlockState state, IWorld world, BlockPos pos, Rotation direction) {
		return state.with(FACING, direction.rotate(state.get(FACING)));
	}

	@Override
	public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public IBlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlayer().getHorizontalFacing().getOpposite());
	}

	@Override
	public void animateTick(IBlockState state, World world, BlockPos pos, Random rand) {
		double pixel = 0.625;
		if (!this.active && rand.nextInt(80) == 0) {
			for (EnumFacing facing : EnumFacing.values()) {
				BlockPos side = pos.offset(facing);
				if (!world.getBlockState(side).isFullCube()) {
					double dx = rand.nextFloat() - 0.5, dy = rand.nextFloat() - 0.5, dz = rand.nextFloat() - 0.5;
					int vx = facing.getXOffset();
					int vy = facing.getYOffset();
					int vz = facing.getZOffset();
					dx *= (1 - Math.abs(vx));
					dy *= (1 - Math.abs(vy));
					dz *= (1 - Math.abs(vz));
					double particleX = pos.getX() + 0.5 + dx + vx * pixel;
					double particleY = pos.getY() + 0.5 + dy + vy * pixel;
					double particleZ = pos.getZ() + 0.5 + dz + vz * pixel;
					BLParticles.DRUID_CASTING_BIG.spawn(world, particleX, particleY, particleZ, 
							ParticleArgs.get()
							.withMotion((rand.nextFloat() - rand.nextFloat()) * 0.1, 0, (rand.nextFloat() - rand.nextFloat()) * 0.1)
							.withScale(rand.nextFloat() * 0.5F + 0.5F)
							.withColor(0.8F + rand.nextFloat() * 0.2F, 0.8F + rand.nextFloat() * 0.2F, 0.8F, 1));
				}                    
			}
		}
	}
}

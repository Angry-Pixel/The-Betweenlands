package thebetweenlands.common.block.terrain;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.entity.IEntityBL;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BiomeRegistry;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.world.gen.biome.decorator.SurfaceType;

public class BlockSpreadingSludgyDirt extends BlockSpreadingDeath {
	private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0, 0, 0, 1, 1 - 0.125F, 1);

	public BlockSpreadingSludgyDirt() {
		super(Material.GRASS);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setTickRandomly(true);
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		if(world.getLight(pos.up()) < 4 && world.getBlockLightOpacity(pos.up()) > 2) {
			world.setBlockState(pos, BlockRegistry.SWAMP_DIRT.getDefaultState());
			this.checkAndRevertBiome(world, pos);
		} else {
			super.updateTick(world, pos, state, rand);
		}
	}

	@Override
	public boolean canSpreadInto(World world, BlockPos pos, IBlockState state, BlockPos offsetPos, IBlockState offsetState) {
		return super.canSpreadInto(world, pos, state, offsetPos, offsetState) && SurfaceType.GRASS_AND_DIRT.matches(offsetState);
	}

	@Override
	public void spreadInto(World world, BlockPos pos, IBlockState state, BlockPos offsetPos, IBlockState offsetState) {
		world.setBlockState(offsetPos, this.getDefaultState());
		for(int yo = 1; yo < 6; yo++) {
			if(this.canSpreadInto(world, pos, state, offsetPos.down(yo), world.getBlockState(offsetPos.down(yo)))) {
				world.setBlockState(offsetPos.down(yo), BlockRegistry.MUD.getDefaultState());
			}
		}
		if(world.rand.nextInt(3) == 0 && world.isAirBlock(offsetPos.up())) {
			world.setBlockState(offsetPos.up(), BlockRegistry.DEAD_WEEDWOOD_BUSH.getDefaultState());
		}
	}
	
	@Override
	protected boolean shouldSpread(World world, BlockPos pos, IBlockState state) {
		return world.rand.nextInt(2) == 0;
	}

	@Override
	public Biome getSpreadingBiome() {
		return BiomeRegistry.SLUDGE_PLAINS;
	}

	@Override
	public Biome getPreviousBiome() {
		return BiomeRegistry.SWAMPLANDS_CLEARING;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return BlockRegistry.SWAMP_DIRT.getItemDropped(state, rand, fortune);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return BOUNDING_BOX;
	}

	@Override
	public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity){
		if(entity instanceof IEntityBL == false) entity.setInWeb();
	}

	@Override
	public boolean isOpaqueCube(IBlockState s) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public BlockRenderLayer getBlockLayer() {
		return BlockRenderLayer.TRANSLUCENT;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess iblockaccess, BlockPos pos, EnumFacing side) {
		Block block = iblockaccess.getBlockState(pos.offset(side)).getBlock();
		return block instanceof BlockSludgyDirt == false && block instanceof BlockSpreadingSludgyDirt == false;
	}
}

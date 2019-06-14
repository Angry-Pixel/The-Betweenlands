package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.entity.EntityDecayPitTarget;

public class BlockDecayPitOuterChainHousing extends BlockHorizontal {
//TODO This block will be removed once the entity is sorted - for now it's just a quick way of setting one in the world
	public BlockDecayPitOuterChainHousing() {
		super(Material.ROCK);
		setHardness(10.0F);
		setResistance(2000.0F);
		setSoundType(SoundType.STONE);
		setCreativeTab(BLCreativeTabs.BLOCKS);
		setDefaultState(blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}

	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
		// S = 0, W = 1, N = 2, E = 3
		if (!world.isRemote) {
			EntityDecayPitTarget entity = new EntityDecayPitTarget(world);
			entity.setLocationAndAngles(pos.getX() + 0.5D, pos.getY() + 4D, pos.getZ() + 0.5D, 0F, 0F);
			//entity.setFacing(state.getValue(FACING).getHorizontalIndex());
			//entity.setLength(7);
			//entity.setHanging(true); // change this if you want to see hanging or scroll chains
			world.spawnEntity(entity);
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		/*if(!world.isRemote) {
			// made it 10 up because it needs to find it or poopy stuff happens (test only) don't stack them ;P
			EntityDecayPitTarget entity = (EntityDecayPitTarget) world.getEntitiesWithinAABB(EntityDecayPitTarget.class, new AxisAlignedBB(pos).grow(0D, 10D, 0D)).get(0);
			if(entity != null)
				entity.setDead();
		}*/
    }

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(FACING, EnumFacing.byIndex(meta));
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		int meta = 0;
		meta = meta | ((EnumFacing) state.getValue(FACING)).getIndex();
		return meta;
	}

	@Override
	 public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
	}

	@Override
	public IBlockState withRotation(IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate((EnumFacing) state.getValue(FACING)));
	}

	@Override
	public IBlockState withMirror(IBlockState state, Mirror mirrorIn) {
		return state.withRotation(mirrorIn.toRotation((EnumFacing) state.getValue(FACING)));
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, new IProperty[] {FACING});
	}
}

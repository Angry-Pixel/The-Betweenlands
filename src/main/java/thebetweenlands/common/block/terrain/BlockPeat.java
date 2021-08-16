package thebetweenlands.common.block.terrain;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.armor.ItemRubberBoots;
import thebetweenlands.common.registries.BlockRegistry;

public class BlockPeat extends Block {
	private static final AxisAlignedBB PEAT_AABB = new AxisAlignedBB(0, 0, 0, 1, 1 - 0.125, 1);

	public BlockPeat() {
		super(Material.GROUND);
		setHardness(0.5F);
		setSoundType(SoundType.GROUND);
		setHarvestLevel("shovel", 0);
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
		return PEAT_AABB;
	}

	@Override
	public void onEntityCollision(World world, BlockPos pos, IBlockState state, Entity entity) {
		boolean canWalk = entity instanceof EntityPlayer && ((EntityPlayer) entity).inventory.armorInventory.get(0).getItem() instanceof ItemRubberBoots;
		if(!canWalk) {
			entity.motionX *= 0.85D;
			entity.motionY *= 0.85D;
			entity.motionZ *= 0.85D;
		}
	}

	@Override
	public boolean isFireSource(World world, BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
		return 0;
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
		if(!worldIn.isRemote && fromPos.getY() > pos.getY() && blockIn.getDefaultState().getMaterial() == Material.FIRE && worldIn.getBlockState(fromPos).getMaterial() != Material.FIRE) {
			worldIn.setBlockState(pos, BlockRegistry.PEAT_SMOULDERING.getDefaultState());
		}
	}
}

package thebetweenlands.common.block.plant;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.block.Block;
import net.minecraft.block.BlockVine;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.api.block.ISickleHarvestable;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockVineBL extends BlockVine implements ISickleHarvestable, IShearable {
	public BlockVineBL(){
		this.setSoundType(SoundType.PLANT);
		this.setHardness(0.2F);
		this.setCreativeTab(BLCreativeTabs.PLANTS);
	}
	
	@Override
	public boolean canAttachTo(World world, BlockPos pos, EnumFacing face) {
		Block block = world.getBlockState(pos.up()).getBlock();
        return this.isAcceptableNeighbor(world, pos.offset(face.getOpposite()), face) && (block == Blocks.AIR || block == this || this.isAcceptableNeighbor(world, pos.up(), EnumFacing.UP));
    }

	private boolean isAcceptableNeighbor(World world, BlockPos pos, EnumFacing face) {
        IBlockState iblockstate = world.getBlockState(pos);
        return iblockstate.getBlockFaceShape(world, pos, face) == BlockFaceShape.SOLID && !isExceptBlockForAttaching(iblockstate.getBlock());
    }
	
	@Override
	public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
		return false;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return item.getItem() == ItemRegistry.SYRMORITE_SHEARS || item.getItem() == ItemRegistry.SILT_CRAB_CLAW;
	}

	@Override
	public List<ItemStack> onSheared(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of(new ItemStack(Item.getItemFromBlock(this)));
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, BlockPos pos) {
		return true;
	}

	@Override
	public List<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, BlockPos pos, int fortune) {
		return ImmutableList.of();
	}
}

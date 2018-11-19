package thebetweenlands.common.block.structure;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockWallSign;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.tile.TileEntityWeedwoodSign;
import thebetweenlands.util.AdvancedStateMap.Builder;

public class BlockWallWeedwoodSign extends BlockWallSign implements ICustomItemBlock, IStateMappedBlock {
	public BlockWallWeedwoodSign() {
		this.setHardness(1.0F);
		this.setSoundType(SoundType.WOOD);
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityWeedwoodSign();
	}

	@Override
	@Nullable
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return this.getSignItem();
	}

	@Override
	public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
		return new ItemStack(this.getSignItem());
	}

	protected Item getSignItem() {
		return ItemRegistry.WEEDWOOD_SIGN_ITEM;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void setStateMapper(Builder builder) {
		builder.ignore(FACING);
	}
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}

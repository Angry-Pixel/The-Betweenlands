package thebetweenlands.common.block.structure;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockWall;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.StateMap.Builder;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.registries.BlockRegistry.IStateMappedBlock;

public class BlockWallBetweenlands extends BlockWall implements IStateMappedBlock {
	public BlockWallBetweenlands(Block modelBlock) {
		super(modelBlock);
		this.setDefaultState(this.blockState.getBaseState().withProperty(UP, Boolean.valueOf(false)).withProperty(NORTH, Boolean.valueOf(false)).withProperty(EAST, Boolean.valueOf(false)).withProperty(SOUTH, Boolean.valueOf(false)).withProperty(WEST, Boolean.valueOf(false)));
		setCreativeTab(BLCreativeTabs.BLOCKS);
	}

	public int damageDropped(IBlockState state) {
		return getMetaFromState(state);
	}

	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list) {
		list.add(new ItemStack(itemIn, 1, 0));
	}

	@Override
	public void setStateMapper(Builder builder) {
		//No variants pls
		builder.ignore(VARIANT);
	}
}

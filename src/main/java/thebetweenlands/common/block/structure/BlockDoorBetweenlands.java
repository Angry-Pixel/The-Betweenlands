package thebetweenlands.common.block.structure;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import thebetweenlands.common.registries.BlockRegistryOld.ICustomItemBlock;
import thebetweenlands.common.registries.BlockRegistryOld.IStateMappedBlock;
import thebetweenlands.util.AdvancedStateMap;

public abstract class BlockDoorBetweenlands extends BlockDoor implements ICustomItemBlock, IStateMappedBlock {
	public BlockDoorBetweenlands(Material material) {
		super(material);
	}

	@Override
	public BlockDoorBetweenlands setSoundType(SoundType type) {
		return (BlockDoorBetweenlands) super.setSoundType(type);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void setStateMapper(AdvancedStateMap.Builder builder) {
		builder.ignore(POWERED);
	}

	@Override
	public IItemProvider getItemDropped(IBlockState state, World world, BlockPos pos, int fortune) {
		return state.get(HALF) == DoubleBlockHalf.UPPER ? Items.AIR : this.getDoorItem().getItem();
	}

	@Override
	public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
		return this.getDoorItem().copy();
	}

	protected abstract ItemStack getDoorItem();
	
	@Override
	public ItemBlock getItemBlock() {
		return null;
	}
}

package thebetweenlands.common.block.farming;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.registries.ItemRegistry;

public class BlockMiddleFruitBush extends BlockGenericCrop implements ICustomItemBlock {
	public BlockMiddleFruitBush() {
		this.setCreativeTab(null);
	}

	@Override
	public ItemStack getSeedDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return new ItemStack(ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS);	
	}

	@Override
	public ItemStack getCropDrop(IBlockAccess world, BlockPos pos, Random rand) {
		return this.isDecayed(world, pos) ? null : new ItemStack(ItemRegistry.MIDDLE_FRUIT);
	}

	@Override
	@SideOnly(Side.CLIENT)
	@Nullable
	public Item getRenderedItem() {
		return ItemRegistry.MIDDLE_FRUIT_BUSH_SEEDS;
	}
}

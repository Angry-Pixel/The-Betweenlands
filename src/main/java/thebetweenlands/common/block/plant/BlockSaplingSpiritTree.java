package thebetweenlands.common.block.plant;

import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import thebetweenlands.common.registries.BlockRegistry.ICustomItemBlock;
import thebetweenlands.common.world.gen.feature.WorldGenSmallSpiritTree;

public class BlockSaplingSpiritTree extends BlockSaplingBetweenlands implements ICustomItemBlock {
	public BlockSaplingSpiritTree() {
		super(new WorldGenSmallSpiritTree());
	}

	@Override
	public ItemBlock getItemBlock() {
		return new ItemBlock(this) {
			@Override
			public EnumRarity getRarity(ItemStack stack) {
				return EnumRarity.RARE;
			}
		};
	}
}

package thebetweenlands.blocks;

import java.util.Random;

import thebetweenlands.creativetabs.ModCreativeTabs;

import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class BlockDruidSpawner extends BlockSpawner {

	public BlockDruidSpawner(String mobName) {
		super("thebetweenlands." + mobName);
		setHarvestLevel("pickaxe", 0);
		setBlockName("thebetweenlands.druidSpawner");
		setBlockTextureName("thebetweenlands:druidSpawner");	
		setCreativeTab(ModCreativeTabs.blocks);
	}

	@Override
	public Item getItemDropped(int id, Random rand, int fortune) {
		return Items.magma_cream;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 1 + rand.nextInt(3);
	}
}
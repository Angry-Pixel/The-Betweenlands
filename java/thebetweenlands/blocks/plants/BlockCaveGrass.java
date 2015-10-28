package thebetweenlands.blocks.plants;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.items.ISyrmoriteShearable;
import thebetweenlands.items.ItemGenericPlantDrop;
import thebetweenlands.items.ItemGenericPlantDrop.EnumItemPlantDrop;

public class BlockCaveGrass extends BlockBLSmallPlants implements IShearable, ISyrmoriteShearable {
	public BlockCaveGrass(String type) {
		super(type);
	}

	@Override
	public boolean canPlaceBlockOn(Block block) {
		return block == BLBlockRegistry.pitstone || block == BLBlockRegistry.limestone;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		return drops;
	}

	@Override
	public boolean isShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return item.getItem() == BLItemRegistry.sickle;
	}

	@Override
	public ArrayList<ItemStack> onSheared(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> dropList = new ArrayList<ItemStack>();
		dropList.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.CAVE_GRASS_BLADES));
		return dropList;
	}
}

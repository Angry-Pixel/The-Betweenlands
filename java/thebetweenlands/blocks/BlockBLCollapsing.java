package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;

public class BlockBLCollapsing extends BlockFalling {
	
	private String type;

	public BlockBLCollapsing(String blockName, Material material, boolean falling) {
		super(material);
		setCreativeTab(BLCreativeTabs.blocks);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
		fallInstantly = false;
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

	@Override
    public void onBlockAdded(World world, int x, int y, int z) {
    }

	@Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbour) { 
    }

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
		world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}

}
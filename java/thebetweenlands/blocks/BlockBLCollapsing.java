package thebetweenlands.blocks;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
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
	public void updateTick(World world, int x, int y,
			int z, Random rand) {
		if (!world.isRemote)
			createFallingBlock(world, x, y, z);
	}

	private void createFallingBlock(World world, int x, int y, int z) {
		if (func_149831_e(world, x, y - 1, z) && y >= 0) {
			byte b0 = 32;

			if (!fallInstantly && world.checkChunksExist(x - b0, y - b0, z - b0, x + b0, y + b0, z + b0)) {
				if (!world.isRemote) {
					world.playSoundEffect(x, y, z, "thebetweenlands:crumble", 0.5F, 1.0F);
					EntityFallingBlock entityfallingblock = new EntityFallingBlock(world, (double) ((float) x + 0.5F), (double) ((float) y + 0.5F), (double) ((float) z + 0.5F), this, world.getBlockMetadata(x, y, z));
					this.func_149829_a(entityfallingblock);
					world.spawnEntityInWorld(entityfallingblock);
				}
			} else {
				world.setBlockToAir(x, y, z);

				while (func_149831_e(world, x, y - 1, z) && y > 0)
					--y;

				if (y > 0)
					world.setBlock(x, y, z, this);
			}
		}
	}

	@Override
	public void onEntityWalking(World world, int x, int y, int z, Entity entity) {
		if (!world.isRemote)
			if(entity instanceof EntityPlayer && !entity.isSneaking())
				world.scheduleBlockUpdate(x, y, z, this, this.tickRate(world));
	}

	@Override
	public void func_149828_a(World world, int x, int y, int z, int meta) {
		if (!world.isRemote) {
			world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), stepSound.getStepResourcePath(), (stepSound.getVolume() + 1.0F) / 2.0F, stepSound.getPitch() * 0.8F);
			world.playAuxSFXAtEntity(null, 2001, x, y + 1, z, Block.getIdFromBlock(world.getBlock(x, y, z)));
			world.setBlockToAir(x, y, z);
		}
	}

}
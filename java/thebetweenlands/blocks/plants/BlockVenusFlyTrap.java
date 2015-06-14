package thebetweenlands.blocks.plants;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

import java.util.Random;

public class BlockVenusFlyTrap extends BlockBLSmallPlants {
	public IIcon modelTexture;
	public IIcon modelTextureBlooming;

	public BlockVenusFlyTrap() {
		super("venusFlyTrap");
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture = reg.registerIcon("thebetweenlands:venusFlyTrap");
		modelTextureBlooming = reg.registerIcon("thebetweenlands:venusFlyTrapBlooming");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return modelTexture;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return null;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		super.updateTick(world, x, y, z, rand);
		if (rand.nextInt(25) == 0) {
			int meta = world.getBlockMetadata(x, y, z);
			if(meta == 0) {
				world.setBlockMetadataWithNotify(x, y, z, 1, 2);
			} else {
				world.setBlockMetadataWithNotify(x, y, z, 0, 2);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.rand.nextInt(50) == 0) {
			TheBetweenlands.proxy.spawnCustomParticle("fly", world, x, y, z, 0.0D, 0.0D, 0.0D, 0);
		}
	}
}

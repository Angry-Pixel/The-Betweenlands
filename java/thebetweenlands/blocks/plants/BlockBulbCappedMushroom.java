package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import thebetweenlands.world.feature.plants.WorldGenHugeMushroom;

public class BlockBulbCappedMushroom extends BlockBLSmallPlants implements IGrowable {
	public IIcon modelTexture1;

	public BlockBulbCappedMushroom() {
		super("bulbCappedMushroom");
		setLightLevel(0.9375F);
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.MODEL_PLANT.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		modelTexture1 = reg.registerIcon("thebetweenlands:bulbCappedMushroom");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return modelTexture1;
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(new ItemStack(BLItemRegistry.bulbCappedMushroomItem, 1 + fortune));
		return drops;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.rand.nextInt(22) == 0) {
			int particle = rand.nextInt(3);
			if(particle == 0) {
				BLParticle.MOSQUITO.spawn(world, x, y, z);
			} else if(particle == 1) {
				BLParticle.FLY.spawn(world, x, y, z);
			} else {
				BLParticle.MOTH.spawn(world, x, y, z);
			}
		}
	}

	@Override
	public void func_149853_b(World world, Random rand, int x, int y, int z) {
		if(rand.nextBoolean()) {
			world.setBlockMetadataWithNotify(x, y, z, world.getBlockMetadata(x, y, z) + 1, 2);
		}
		if(world.getBlockMetadata(x, y, z) >= 3) {
			WorldGenHugeMushroom gen = new WorldGenHugeMushroom();
			world.setBlock(x, y, z, Blocks.air);
			if(!gen.generate(world, rand, x, y, z)) {
				world.setBlock(x, y, z, this);
			}
		}
	}
}

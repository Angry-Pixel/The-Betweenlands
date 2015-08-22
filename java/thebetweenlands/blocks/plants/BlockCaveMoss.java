package thebetweenlands.blocks.plants;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.stalactite.StalactiteHelper;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.utils.Point2D;
import thebetweenlands.world.feature.gen.cave.WorldGenCaveMoss;

public class BlockCaveMoss extends BlockBush {
	private IIcon lower;

	public BlockCaveMoss() {
		super(Material.plants);
		setTickRandomly(false);
		setHardness(0);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockName("thebetweenlands.caveMoss");
		setBlockTextureName("thebetweenlands:caveMoss");
		setStepSound(Block.soundTypeGrass);
		setBlockBounds(0.25F, 0, 0.25F, 0.75F, 1, 0.75F);
	}

	@Override
	public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity) {
		return true;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return isValidBlock(world.getBlock(x, y + 1, z)) && canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return isValidBlock(world.getBlock(x, y + 1, z));
	}

	@Override
	public int getRenderType() {
		return 6;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return random.nextInt(2);
	}

	@Override
	public Item getItemDropped(int id, Random random, int fortune) {
		return BLItemRegistry.caveMoss;
	}

	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float sideHitX, float sideHitY, float sideHitZ, int meta) {
		return world.getBlock(x, y - 1, z) != this ? 1 : 0;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		if (world.isAirBlock(x, y + 1, z)) {
			world.setBlockToAir(x, y, z);
			dropBlockAsItem(world, x, y, z, 0, 0);
		} else {
			world.setBlockMetadataWithNotify(x, y, z, world.getBlock(x, y - 1, z) != this ? 1 : 0, 2);
		}
	}

	@Override
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		if (random.nextInt(40) == 0) {
			float dripRange = 0.5F;
			float px = random.nextFloat() - 0.5F;
			float py = random.nextFloat();
			float pz = random.nextFloat() - 0.5F;
			float u = Math.max(Math.abs(px), Math.abs(pz));
			px = px / u * dripRange + 0.5F;
			pz = pz / u * dripRange + 0.5F;
			BLParticle.CAVE_WATER_DRIP.spawn(world, x + px, y + py, z + pz);
		}
	}

	@Override
	public void registerBlockIcons(IIconRegister iconRegister) {
		super.registerBlockIcons(iconRegister);
		lower = iconRegister.registerIcon(textureName + "Lower");
	}

	@Override
	public IIcon getIcon(int side, int metadata) {
		return metadata == 1 ? lower : blockIcon;
	}

	protected boolean isValidBlock(Block block) {
		return block == BLBlockRegistry.betweenstone || block == this;
	}
}

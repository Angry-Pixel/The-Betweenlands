package thebetweenlands.blocks.terrain;

import java.util.Random;

import net.minecraft.block.BlockContainer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.recipes.BLMaterial;
import thebetweenlands.tileentities.TileEntityWisp;
import thebetweenlands.world.WorldProviderBetweenlands;
import thebetweenlands.world.events.EnvironmentEventRegistry;

public class BlockWisp extends BlockContainer {
	public BlockWisp() {
		super(BLMaterial.wisp);
		setStepSound(soundTypeStone);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName("thebetweenlands.wisp");
		setHardness(0);
		setBlockBounds(0.2F, 0.2F, 0.2F, 0.8F, 0.8F, 0.8F);
		setBlockTextureName("thebetweenlands:wisp");
	}

	public static boolean canSee(World world) {
		if(world.provider instanceof WorldProviderBetweenlands) {
			WorldProviderBetweenlands provider = (WorldProviderBetweenlands)world.provider;
			EnvironmentEventRegistry eeRegistry = provider.getWorldData().getEnvironmentEventRegistry();
			if(eeRegistry.AURORAS.isActive()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		if(canSee(world))
			return super.collisionRayTrace(world, x, y, z, start, end);
		return null;
	}

	@Override
	public Item getItemDropped(int metadata, Random random, int fortune) {
		return null;
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
		if(!world.isRemote && canSee(world)) {
			EntityItem wispItem = new EntityItem(world, x + 0.5D, y + 0.5D, z + 0.5D, new ItemStack(Item.getItemFromBlock(this), 1));
			world.spawnEntityInWorld(wispItem);
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world, int meta) {
		return new TileEntityWisp();
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		world.setBlock(x, y, z, this, world.rand.nextInt(this.colors.length / 2) * 2, 2);
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int i) {
		return null;
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return true;
	}

	// Colors can be added here, always add a pair of colors for outer color and inner color
	public int[] colors = new int[] {
			0xFF7F1659, 0xFFFFFFFF, // Pink/White
			0xFF0707C8, 0xFFC8077B, // Blue/Pink
			0xFF0E2E0B, 0xFFC8077B, // Green/Yellow/White
			0xFF9A6908, 0xFF4F0303 // Red/Yellow/White
	};

	/**
	 * Sets the block at the giving position to a wisp block with a random color
	 * 
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 */
	public void generateBlock(World world, int x, int y, int z) {
		world.setBlock(x, y, z, this, world.rand.nextInt(this.colors.length / 2) * 2, 2);
	}
}

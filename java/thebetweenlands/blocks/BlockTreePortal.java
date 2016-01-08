package thebetweenlands.blocks;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesPortal;
import thebetweenlands.world.teleporter.TeleporterHandler;
public class BlockTreePortal extends Block {

	public BlockTreePortal() {
		super(Material.portal);
		setLightLevel(1.0F);
		setBlockUnbreakable();
		setBlockName("thebetweenlands.treePortalBlock");
		setStepSound(Block.soundTypeGlass);
		setBlockTextureName("thebetweenlands:portal");
	}

	public static boolean makePortalX(World world, int x, int y, int z) {
		world.setBlock(x, y + 2, z - 1, BLBlockRegistry.portalBarkFrame, 8, 2);
		world.setBlock(x, y + 2, z, BLBlockRegistry.portalBarkFrame, 9, 2);
		world.setBlock(x, y + 2, z + 1, BLBlockRegistry.portalBarkFrame, 10, 2);
		world.setBlock(x, y + 1, z - 1, BLBlockRegistry.portalBarkFrame, 11, 2);
		world.setBlock(x, y + 1, z + 1, BLBlockRegistry.portalBarkFrame, 12, 2);
		world.setBlock(x, y, z - 1, BLBlockRegistry.portalBarkFrame, 11, 2);
		world.setBlock(x, y, z + 1, BLBlockRegistry.portalBarkFrame, 12, 2);
		world.setBlock(x, y - 1, z - 1, BLBlockRegistry.portalBarkFrame, 13, 2);
		world.setBlock(x, y - 1, z, BLBlockRegistry.portalBarkFrame, 14, 2);
		world.setBlock(x, y - 1, z + 1, BLBlockRegistry.portalBarkFrame, 15, 2);

		if (isPatternValidX(world, x, y, z)) {
			world.setBlock(x, y, z, BLBlockRegistry.treePortalBlock, 0, 2);
			world.setBlock(x, y + 1, z, BLBlockRegistry.treePortalBlock, 0, 2);
			return true;
		}
		return false;
	}

	public static boolean makePortalZ(World world, int x, int y, int z) {
		world.setBlock(x - 1, y + 2, z, BLBlockRegistry.portalBarkFrame, 0, 2);
		world.setBlock(x, y + 2, z, BLBlockRegistry.portalBarkFrame, 1, 2);
		world.setBlock(x + 1, y + 2, z, BLBlockRegistry.portalBarkFrame, 2, 2);
		world.setBlock(x - 1, y + 1, z, BLBlockRegistry.portalBarkFrame, 3, 2);
		world.setBlock(x + 1, y + 1, z, BLBlockRegistry.portalBarkFrame, 4, 2);
		world.setBlock(x - 1, y, z, BLBlockRegistry.portalBarkFrame, 3, 2);
		world.setBlock(x + 1, y, z, BLBlockRegistry.portalBarkFrame, 4, 2);
		world.setBlock(x - 1, y - 1, z, BLBlockRegistry.portalBarkFrame, 5, 2);
		world.setBlock(x, y - 1, z, BLBlockRegistry.portalBarkFrame, 6, 2);
		world.setBlock(x + 1, y - 1, z, BLBlockRegistry.portalBarkFrame, 7, 2);

		if (isPatternValidZ(world, x, y, z)) {
			world.setBlock(x, y, z, BLBlockRegistry.treePortalBlock, 1, 2);
			world.setBlock(x, y + 1, z, BLBlockRegistry.treePortalBlock, 1, 2);
			return true;
		}
		return false;
	}

	public static boolean isPatternValidX(World world, int x, int y, int z) {
		// Layer 0
		if (!check(world, x, y - 1, z, BLBlockRegistry.portalBarkFrame) && !checkPortal(world, x, y - 1, z, BLBlockRegistry.treePortalBlock, 0))
			return false;

		// Layer 1
		if (!check(world, x, y, z - 1, BLBlockRegistry.portalBarkFrame))
			return false;
		if (!check(world, x, y, z + 1, BLBlockRegistry.portalBarkFrame))
			return false;

		// Layer 2
		if (!check(world, x, y + 1, z - 1, BLBlockRegistry.portalBarkFrame))
			return false;
		if (!check(world, x, y + 1, z + 1, BLBlockRegistry.portalBarkFrame))
			return false;

		// Layer 3
		if (!check(world, x, y + 2, z, BLBlockRegistry.portalBarkFrame))
			return false;

		return true;
	}

	public static boolean isPatternValidZ(World world, int x, int y, int z) {
		// Layer 0
		if (!check(world, x, y - 1, z, BLBlockRegistry.portalBarkFrame) && !checkPortal(world, x, y - 1, z, BLBlockRegistry.treePortalBlock, 1))
			return false;

		// Layer 1
		if (!check(world, x - 1, y, z, BLBlockRegistry.portalBarkFrame))
			return false;
		if (!check(world, x + 1, y, z, BLBlockRegistry.portalBarkFrame))
			return false;

		// Layer 2
		if (!check(world, x - 1, y + 1, z, BLBlockRegistry.portalBarkFrame))
			return false;
		if (!check(world, x + 1, y + 1, z, BLBlockRegistry.portalBarkFrame))
			return false;

		// Layer 3
		if (!check(world, x, y + 2, z, BLBlockRegistry.portalBarkFrame))
			return false;

		return true;
	}

	private static boolean check(World world, int x, int y, int z, Block target) {
		return world.getBlock(x, y, z) == target;
	}

	private static boolean checkPortal(World world, int x, int y, int z, Block target, int meta) {
		return world.getBlock(x, y, z) == target && world.getBlockMetadata(x, y, z) == meta;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		canBlockStay(world, x, y, z);
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		if(checkPortal(world, x, y + 1, z, BLBlockRegistry.treePortalBlock, 0) && isPatternValidX(world, x, y, z))
			return true;
		if(checkPortal(world, x, y - 1, z, BLBlockRegistry.treePortalBlock, 0) && isPatternValidX(world, x, y - 1, z))
			return true;
		if(checkPortal(world, x, y + 1, z, BLBlockRegistry.treePortalBlock, 1) && isPatternValidZ(world, x, y, z))
			return true;
		if(checkPortal(world, x, y - 1, z, BLBlockRegistry.treePortalBlock, 1) && isPatternValidZ(world, x, y - 1, z))
			return true;
		else {
			world.playAuxSFXAtEntity(null, 2001, x, y, z, Block.getIdFromBlock(world.getBlock(x, y, z)));
			world.setBlockToAir(x, y, z);
		}
		return false;
	}

	@Override
	public int quantityDropped(Random rand) {
		return 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() {
		return 1;
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float xSize;
		float ySize;

		if( world.getBlock(x - 1, y, z) != BLBlockRegistry.portalBarkFrame && world.getBlock(x + 1, y, z) != BLBlockRegistry.portalBarkFrame ) {
			xSize = 0.125F;
			ySize = 0.5F;
		} else {
			xSize = 0.5F;
			ySize = 0.125F;
		}

		this.setBlockBounds(0.5F - xSize, 0.0F, 0.5F - ySize, 0.5F + xSize, 1.0F, 0.5F + ySize);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		if (entity.ridingEntity == null && entity.riddenByEntity == null && entity.timeUntilPortal <= 0) {
			if(entity instanceof EntityPlayer){
				EntityPropertiesPortal props = BLEntityPropertiesRegistry.HANDLER.getProperties(entity, EntityPropertiesPortal.class);
				props.inPortal = true;
			} else if(!world.isRemote) {
				if (entity.dimension == 0)
					TeleporterHandler.transferToBL(entity);
				else
					TeleporterHandler.transferToOverworld(entity);
				entity.timeUntilPortal = 10;
			}
		}
	}

	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		return side > 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		for( int i = 0; i < 4; i++ ) {
			double particleX = x + rand.nextFloat();
			double particleY = y + rand.nextFloat();
			double particleZ = z + rand.nextFloat();
			double motionX;
			double motionY;
			double motionZ;
			int multi = rand.nextInt(2) * 2 - 1;

			motionX = (rand.nextFloat() - 0.5D) * 0.5D;
			motionY = (rand.nextFloat() - 0.5D) * 0.5D;
			motionZ = (rand.nextFloat() - 0.5D) * 0.5D;

			if( world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this ) {
				particleX = x + 0.5D + 0.25D * multi;
				motionX = rand.nextFloat() * 2.0F * multi;
			} else {
				particleZ = z + 0.5D + 0.25D * multi;
				motionZ = rand.nextFloat() * 2.0F * multi;
			}

			BLParticle.PORTAL.spawn(world, particleX, particleY, particleZ, motionX, motionY, motionZ, 0);
		}
		if(rand.nextInt(100) == 0){
			world.playSound((double)x + .5, (double)y + .5, (double)z + .5, "thebetweenlands:portal", 0.2F, rand.nextFloat() * 0.4F + 0.8F, false);
		}
	}
}
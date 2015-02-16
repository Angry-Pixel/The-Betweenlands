package thebetweenlands.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.world.teleporter.TeleporterBetweenlands;

import java.util.Random;

public class BlockPortal
        extends Block
{
	public static final int[][] types = new int[][] { new int[0], { 3, 1 }, { 2, 0 } };

	public BlockPortal() {
		super(Material.portal);
		setLightLevel(1.0F);
		setBlockUnbreakable();
		setBlockName("thebetweenlands.portalBlock");
		setStepSound(Block.soundTypeGlass);
		setBlockTextureName("thebetweenlands:portal");
		setTickRandomly(true);
	}

	@Override
	public void onEntityCollidedWithBlock(World world, int i, int j, int k, Entity entity) {
		//TODO Add teleport and dimension transfer here
		//TODO: Just temporary to test some stuff
		if( world instanceof WorldServer ) {
			WorldServer worldServer = (WorldServer)world;
			if( entity instanceof EntityPlayerMP ) {
				EntityPlayerMP player = (EntityPlayerMP) entity;
				player.mcServer.getConfigurationManager().transferPlayerToDimension(player, ModInfo.DIMENSION_ID, new TeleporterBetweenlands(worldServer));
			}
		}
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float xSize;
        float ySize;

		if( world.getBlock(x - 1, y, z) != this && world.getBlock(x + 1, y, z) != this ) {
			xSize = 0.125F;
			ySize = 0.5F;
		} else {
			xSize = 0.5F;
			ySize = 0.125F;
		}

        this.setBlockBounds(0.5F - xSize, 0.0F, 0.5F - ySize, 0.5F + xSize, 1.0F, 0.5F + ySize);
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
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbourBlock) {
		int meta = getMetadata(world.getBlockMetadata(x, y, z));
		Size size = new Size(world, x, y, z, 1);
		Size size1 = new Size(world, x, y, z, 2);

		if( meta == 1 && (!size.isValidSize() || size.field_150864_e < size.width * size.height) ) {
            world.setBlockToAir(x, y, z);
        } else if( meta == 2 && (!size1.isValidSize() || size1.field_150864_e < size1.width * size1.height) ) {
            world.setBlockToAir(x, y, z);
        } else if( meta == 0 && !size.isValidSize() && !size1.isValidSize() ) {
            world.setBlockToAir(x, y, z);
        }
	}

	public boolean tryToCreatePortal(World world, int x, int y, int z) {
		Size size = new Size(world, x, y, z, 1);
		Size size1 = new Size(world, x, y, z, 2);

		if( size.isValidSize() && size.field_150864_e == 0 ) {
			size.makePortal();
			return true;
		} else if( size1.isValidSize() && size1.field_150864_e == 0 ) {
			size1.makePortal();
			return true;
		}

        return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		if( world.getBlock(x, y, z) == this ) {
            return false;
        } else {
			boolean isXNegThisAndXNeg2IsNot = world.getBlock(x - 1, y, z) == this && world.getBlock(x - 2, y, z) != this;
			boolean isXPosThisAndXPos2IsNot = world.getBlock(x + 1, y, z) == this && world.getBlock(x + 2, y, z) != this;
			boolean isZNegThisAndZNeg2IsNot = world.getBlock(x, y, z - 1) == this && world.getBlock(x, y, z - 2) != this;
			boolean isZPosThisAndZPos2IsNot = world.getBlock(x, y, z + 1) == this && world.getBlock(x, y, z + 2) != this;
			boolean isXNegOrIsXPos = isXNegThisAndXNeg2IsNot || isXPosThisAndXPos2IsNot;
			boolean isZNegOrIsZPos = isZNegThisAndZNeg2IsNot || isZPosThisAndZPos2IsNot;
			return !(!isXNegOrIsXPos || side != 4) || (!(!isXNegOrIsXPos || side != 5) || (!(!isZNegOrIsZPos || side != 2) || isZNegOrIsZPos && side == 3));
		}
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

			world.spawnParticle("smoke", particleX, particleY, particleZ, motionX / 4D, motionY / 4D, motionZ / 4D);
		}
	}

	public static class Size
    {
		private final World world;
		private final int type, side2, side1;
		private int field_150864_e = 0;
		private ChunkCoordinates porition;
		private int height;
		private int width;

		public Size(World world, int x, int y, int z, int type) {
			this.world = world;
			this.type = type;
			side1 = types[type][0];
			side2 = types[type][1];

			for( int i1 = y; y > i1 - 21 && y > 0 ; --y) {
                if( !isBlockRepleaceable(world.getBlock(x, y - 1, z)) ) {
                    break;
                }
            }

			int j1 = getSize(x, y, z, this.side1) - 1;

			if( j1 >= 0 ) {
				this.porition = new ChunkCoordinates(x + j1 * Direction.offsetX[this.side1], y, z + j1 * Direction.offsetZ[this.side1]);
				this.width = getSize(this.porition.posX, this.porition.posY, this.porition.posZ, this.side2);

				if( this.width < 2 || this.width > 21 ) {
                    this.porition = null;
                    this.width = 0;
				}
			}

			if( this.porition != null ) {
                this.height = func_150858_a();
            }
		}

		protected int getSize(int x, int y, int z, int type) {
			int j1 = Direction.offsetX[type];
			int k1 = Direction.offsetZ[type];
			int i1;
			Block block;

			for (i1 = 0; i1 < 22; ++i1) {
				block = world.getBlock(x + j1 * i1, y, z + k1 * i1);

				if (!isBlockRepleaceable(block))
					break;

				Block block1 = world.getBlock(x + j1 * i1, y - 1, z + k1 * i1);

				if (block1 != Blocks.mossy_cobblestone)
					break;
			}

			block = world.getBlock(x + j1 * i1, y, z + k1 * i1);
			return block == Blocks.mossy_cobblestone ? i1 : 0;
		}

		protected int func_150858_a() {
			int i;
			int j;
			int k;
			int l;
			label56:

			for (height = 0; height < 21; height++) {
				i = porition.posY + height;

				for (j = 0; j < width; j++) {
					k = porition.posX + j * Direction.offsetX[types[type][1]];
					l = porition.posZ + j * Direction.offsetZ[types[type][1]];
					Block block = world.getBlock(k, i, l);

					if (!isBlockRepleaceable(block))
						break label56;

					if (block == BLBlockRegistry.portalBlock)
						field_150864_e++;

					if (j == 0) {
						block = world.getBlock(k + Direction.offsetX[types[type][0]], i, l + Direction.offsetZ[types[type][0]]);

						if (block != Blocks.mossy_cobblestone)
							break label56;
					} else if (j == width - 1) {
						block = world.getBlock(k + Direction.offsetX[types[type][1]], i, l + Direction.offsetZ[types[type][1]]);

						if (block != Blocks.mossy_cobblestone)
							break label56;
					}
				}
			}

			for (i = 0; i < width; ++i) {
				j = porition.posX + i * Direction.offsetX[types[type][1]];
				k = porition.posY + height;
				l = porition.posZ + i * Direction.offsetZ[types[type][1]];

				if (world.getBlock(j, k, l) != Blocks.mossy_cobblestone) {
					height = 0;
					break;
				}
			}

			if (height <= 21 && height >= 3)
				return height;
			else {
				porition = null;
				width = 0;
				height = 0;
				return 0;
			}
		}

		protected boolean isBlockRepleaceable(Block block) {
			return block == null || block.getMaterial() == Material.air || block == Blocks.fire || block == BLBlockRegistry.portalBlock;
		}

		public boolean isValidSize() {
			return porition != null && width >= 2 && width <= 21 && height >= 3 && height <= 21;
		}

		public void makePortal() {
			for (int i = 0; i < width; i++) {
				int j = porition.posX + Direction.offsetX[side2] * i;
				int k = porition.posZ + Direction.offsetZ[side2] * i;

				for (int l = 0; l < height; l++) {
					int i1 = porition.posY + l;
					world.setBlock(j, i1, k, BLBlockRegistry.portalBlock, type, 2);
				}
			}
		}
	}

	public static int getMetadata(int meta) {
		return meta & 3;
	}
}

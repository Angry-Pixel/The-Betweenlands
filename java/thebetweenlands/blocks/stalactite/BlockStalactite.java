package thebetweenlands.blocks.stalactite;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.Point2D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStalactite extends Block {
	@SideOnly(Side.CLIENT)
	private IIcon topIcon, bottomIcon;

	public BlockStalactite(Material material) {
		super(material);
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return 0;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && (canPlaceBlockOn(world.getBlock(x, y - 1, z)) || canPlaceBlockOn(world.getBlock(x, y + 1, z)));
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return canPlaceBlockOn(world.getBlock(x, y - 1, z)) || canPlaceBlockOn(world.getBlock(x, y + 1, z));
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		checkAndDropBlock(world, x, y, z);
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!canBlockStay(world, x, y, z)) {
			dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
			world.notifyBlockChange(x, y, z, Blocks.air);
		}
	}

	protected boolean canPlaceBlockOn(Block block) {
		return block == this || block.isOpaqueCube();
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
	public int getRenderType() {
		return ClientProxy.BlockRenderIDs.STALACTITE.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("thebetweenlands:stalacmite_middle");
		topIcon = iconRegister.registerIcon("thebetweenlands:stalacmite_top");
		bottomIcon = iconRegister.registerIcon("thebetweenlands:stalacmite_bottom");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		switch (side) {
		case 0:
			return topIcon;
		case 1:
			return bottomIcon;
		default:
			return blockIcon;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		int md = world.getBlockMetadata(x, y, z);
		if (md == 1 && random.nextInt(10) == 0) {
			StalactiteHelper core = StalactiteHelper.getValsFor(x, y, z);
			double midHeight = 0.2 + random.nextDouble() * 0.4;
			Point2D mid = core.getMidway(midHeight);

			float dripRange = 0.1F;
			double sx = x + mid.x + StalactiteHelper.randRange(random, -dripRange, dripRange);
			double sy = y + midHeight;
			double sz = z + mid.y + StalactiteHelper.randRange(random, -dripRange, dripRange);

			BLParticle.STALACTITE_WATER_DRIP.spawn(world, sx, sy, sz);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (world.getBlock(x, y - 1, z) == Blocks.air) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 0x02);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		checkAndDropBlock(world, x, y, z);
		StalactiteData info = StalactiteData.getData(world, x, y, z);
		int md = world.getBlockMetadata(x, y, z);

		if (info.noTop && info.noBottom) {
			//todo
		} else if (md == 0 && info.noBottom && info.distDown == 0) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		} else if (md == 1 && (!info.noBottom || info.distDown > 0)) {
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}
	}

	public static boolean renderBlock(Block block, int x, int y, int z, IBlockAccess world) {
		StalactiteData info = StalactiteData.getData(world, x, y, z);
		return renderBlock(block, info.posX, info.posY, info.posZ, info.noBottom, info.distDown, info.noTop, info.distUp, block.getMixedBrightnessForBlock(world, x, y, z));
	}

	public static boolean renderBlock(Block block, int x, int y, int z, boolean noBottom, int distDown, boolean noTop, int distUp, int brightness) {
		int totalHeight = 1 + distDown + distUp;
		float distToMidBottom, distToMidTop;

		double squareAmount = 1.2D;
		double halfTotalHeightSQ;

		if (noTop) {
			halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
			distToMidBottom = Math.abs(distUp + 1);
			distToMidTop = Math.abs(distUp);
		} else if (noBottom) {
			halfTotalHeightSQ = Math.pow(totalHeight, squareAmount);
			distToMidBottom = Math.abs(distDown);
			distToMidTop = Math.abs(distDown + 1);
		} else {
			float halfTotalHeight = totalHeight * 0.5F;
			halfTotalHeightSQ = Math.pow(halfTotalHeight, squareAmount);
			distToMidBottom = Math.abs(halfTotalHeight - distUp - 1);
			distToMidTop = Math.abs(halfTotalHeight - distUp);
		}

		int minValBottom = noBottom && distDown == 0 ? 0 : 1;
		int minValTop = noTop && distUp == 0 ? 0 : 1;
		int scaledValBottom = (int) (Math.pow(distToMidBottom, squareAmount) / halfTotalHeightSQ * (8 - minValBottom)) + minValBottom;
		int scaledValTop = (int) (Math.pow(distToMidTop, squareAmount) / halfTotalHeightSQ * (8 - minValTop)) + minValTop;

		int iconId = distUp == 0 && !noTop ? 0 : distDown == 0 && !noBottom ? 1 : 2;
		IIcon icon = block.getIcon(iconId, 0);
		float u0 = icon.getMinU();
		float u1 = icon.getMaxU();
		float v0 = icon.getMinV();
		float v1 = icon.getMaxV();

		double halfSize, halfSizeTexW;
		double halfSize1, halfSizeTex1;
		halfSize = (double) scaledValBottom / 16;
		halfSizeTexW = halfSize * (u1 - u0);
		halfSize1 = (double) scaledValTop / 16;
		halfSizeTex1 = halfSize1 * (u1 - u0);

		StalactiteHelper core = StalactiteHelper.getValsFor(x, y, z);

		Tessellator t = Tessellator.instance;
		t.setBrightness(brightness);
		float f = 0.9F;
		t.setColorOpaque_F(f, f, f);

		// front
		t.addVertexWithUV(x + core.bX - halfSize, y, z + core.bZ - halfSize, u0 + halfSizeTexW * 2, v1);
		t.addVertexWithUV(x + core.bX - halfSize, y, z + core.bZ + halfSize, u0, v1);
		t.addVertexWithUV(x + core.tX - halfSize1, y + 1, z + core.tZ + halfSize1, u0, v0);
		t.addVertexWithUV(x + core.tX - halfSize1, y + 1, z + core.tZ - halfSize1, u0 + halfSizeTex1 * 2, v0);
		// back
		t.addVertexWithUV(x + core.bX + halfSize, y, z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v1);
		t.addVertexWithUV(x + core.bX + halfSize, y, z + core.bZ - halfSize, u0, v1);
		t.addVertexWithUV(x + core.tX + halfSize1, y + 1, z + core.tZ - halfSize1, u0, v0);
		t.addVertexWithUV(x + core.tX + halfSize1, y + 1, z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);
		// left
		t.addVertexWithUV(x + core.bX + halfSize, y, z + core.bZ - halfSize, u0 + halfSizeTexW * 2, v1);
		t.addVertexWithUV(x + core.bX - halfSize, y, z + core.bZ - halfSize, u0, v1);
		t.addVertexWithUV(x + core.tX - halfSize1, y + 1, z + core.tZ - halfSize1, u0, v0);
		t.addVertexWithUV(x + core.tX + halfSize1, y + 1, z + core.tZ - halfSize1, u0 + halfSizeTex1 * 2, v0);
		// right
		t.addVertexWithUV(x + core.bX - halfSize, y, z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v1);
		t.addVertexWithUV(x + core.bX + halfSize, y, z + core.bZ + halfSize, u0, v1);
		t.addVertexWithUV(x + core.tX + halfSize1, y + 1, z + core.tZ + halfSize1, u0, v0);
		t.addVertexWithUV(x + core.tX - halfSize1, y + 1, z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);

		icon = block.getIcon(2, 0);
		u0 = icon.getMinU();
		v0 = icon.getMinV();

		// top
		if (distUp == 0) {
			t.addVertexWithUV(x + core.tX - halfSize1, y + 1, z + core.tZ - halfSize1, u0, v0);
			t.addVertexWithUV(x + core.tX - halfSize1, y + 1, z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0);
			t.addVertexWithUV(x + core.tX + halfSize1, y + 1, z + core.tZ + halfSize1, u0 + halfSizeTex1 * 2, v0 + halfSizeTex1 * 2);
			t.addVertexWithUV(x + core.tX + halfSize1, y + 1, z + core.tZ - halfSize1, u0, v0 + halfSizeTex1 * 2);
		}

		// bottom
		if (distDown == 0) {
			t.addVertexWithUV(x + core.bX - halfSize, y, z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v0);
			t.addVertexWithUV(x + core.bX - halfSize, y, z + core.bZ - halfSize, u0, v0);
			t.addVertexWithUV(x + core.bX + halfSize, y, z + core.bZ - halfSize, u0, v0 + halfSizeTexW * 2);
			t.addVertexWithUV(x + core.bX + halfSize, y, z + core.bZ + halfSize, u0 + halfSizeTexW * 2, v0 + halfSizeTexW * 2);
		}

		return true;
	}
}

package thebetweenlands.blocks.stalactite;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.proxy.ClientProxy;
import thebetweenlands.utils.Point2D;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStalactite extends Block {
	private static final int TOP = 0;

	private static final int BOTTOM = 1;

	private static final int MIDDLE = 2;

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
		StalactiteData info = StalactiteData.getData(world, x, y, z);
		return !info.noBottom || !info.noTop;
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
		return side == TOP ? topIcon : side == BOTTOM ? bottomIcon : blockIcon;
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

			BLParticle.CAVE_WATER_DRIP.spawn(world, sx, sy, sz);
		}
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (world.getBlock(x, y - 1, z) == Blocks.air) {
			world.setBlockMetadataWithNotify(x, y, z, 1, 2);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
		checkAndDropBlock(world, x, y, z);
		StalactiteData info = StalactiteData.getData(world, x, y, z);
		int md = world.getBlockMetadata(x, y, z);

		if (md == 0 && info.noBottom && info.distDown == 0) {
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
		int height = 1 + distDown + distUp;
		float distToMidBottom, distToMidTop;

		double exp = 1.2;
		double scale;

		if (noTop) {
			scale = Math.pow(height, exp);
			distToMidBottom = distUp + 1;
			distToMidTop = distUp;
		} else if (noBottom) {
			scale = Math.pow(height, exp);
			distToMidBottom = distDown;
			distToMidTop = distDown + 1;
		} else {
			float halfTotalHeight = height * 0.5F;
			scale = Math.pow(halfTotalHeight, exp);
			distToMidBottom = Math.abs(halfTotalHeight - distUp - 1);
			distToMidTop = Math.abs(halfTotalHeight - distUp);
		}

		int minValBottom = noBottom && distDown == 0 ? 0 : 1;
		int minValTop = noTop && distUp == 0 ? 0 : 1;

		double bottomWidth = ((int) (Math.pow(distToMidBottom, exp) / scale * (8 - minValBottom)) + minValBottom) / 16D;
		double topWidth = ((int) (Math.pow(distToMidTop, exp) / scale * (8 - minValTop)) + minValTop) / 16D;

		boolean hasTop = distUp == 0 && !noTop;
		boolean hasBottom = distDown == 0 && !noBottom;

		int tex = hasTop ? TOP : hasBottom ? BOTTOM : MIDDLE;

		IIcon icon = block.getIcon(tex, 0);

		float u1 = icon.getMinU();
		float u2 = icon.getMaxU();
		float v1 = icon.getMinV();
		float v2 = icon.getMaxV();

		double texBottomWidth = bottomWidth * (u2 - u1) * 2;
		double texTopWidth = topWidth * (u2 - u1) * 2;

		StalactiteHelper core = StalactiteHelper.getValsFor(x, y, z);

		if (hasTop) {
			core.tX = 0.5;
			core.tZ = 0.5;
		}

		if (hasBottom) {
			core.bX = 0.5;
			core.bZ = 0.5;
		}

		Tessellator t = Tessellator.instance;
		t.setBrightness(brightness);
		t.setColorRGBA(255, 255, 255, 255);

		float nx, ny, nz, length;

		// front
		nx = 1;
		ny = (float) ((core.tX - topWidth) - (core.bX - bottomWidth));
		nz = 0;
		length = MathHelper.sqrt_float(1 + ny * ny);
		nx /= length;
		ny /= length;
		t.setNormal(nx, ny, nz);

		t.addVertexWithUV(x + core.bX - bottomWidth, y, z + core.bZ - bottomWidth, u1 + texBottomWidth, v2);
		t.addVertexWithUV(x + core.bX - bottomWidth, y, z + core.bZ + bottomWidth, u1, v2);
		t.addVertexWithUV(x + core.tX - topWidth, y + 1, z + core.tZ + topWidth, u1, v1);
		t.addVertexWithUV(x + core.tX - topWidth, y + 1, z + core.tZ - topWidth, u1 + texTopWidth, v1);

		// back
		nx = 1;
		ny = -(float) ((core.tX + topWidth) - (core.bX + bottomWidth));
		nz = 0;
		length = MathHelper.sqrt_float(1 + ny * ny);
		nx /= length;
		ny /= length;
		t.setNormal(nx, ny, nz);

		t.addVertexWithUV(x + core.bX + bottomWidth, y, z + core.bZ + bottomWidth, u1 + texBottomWidth, v2);
		t.addVertexWithUV(x + core.bX + bottomWidth, y, z + core.bZ - bottomWidth, u1, v2);
		t.addVertexWithUV(x + core.tX + topWidth, y + 1, z + core.tZ - topWidth, u1, v1);
		t.addVertexWithUV(x + core.tX + topWidth, y + 1, z + core.tZ + topWidth, u1 + texTopWidth, v1);

		// left
		nx = 0;
		ny = (float) ((core.tZ - topWidth) - (core.bZ - bottomWidth));
		nz = 1;
		length = MathHelper.sqrt_float(1 + ny * ny);
		ny /= length;
		nz /= length;
		t.setNormal(nx, ny, nz);

		t.addVertexWithUV(x + core.bX + bottomWidth, y, z + core.bZ - bottomWidth, u1 + texBottomWidth, v2);
		t.addVertexWithUV(x + core.bX - bottomWidth, y, z + core.bZ - bottomWidth, u1, v2);
		t.addVertexWithUV(x + core.tX - topWidth, y + 1, z + core.tZ - topWidth, u1, v1);
		t.addVertexWithUV(x + core.tX + topWidth, y + 1, z + core.tZ - topWidth, u1 + texTopWidth, v1);

		// right
		nx = 0;
		ny = -(float) ((core.tZ + topWidth) - (core.bZ + bottomWidth));
		nz = 1;
		length = MathHelper.sqrt_float(1 + ny * ny);
		ny /= length;
		nz /= length;
		t.setNormal(nx, ny, nz);

		t.addVertexWithUV(x + core.bX - bottomWidth, y, z + core.bZ + bottomWidth, u1 + texBottomWidth, v2);
		t.addVertexWithUV(x + core.bX + bottomWidth, y, z + core.bZ + bottomWidth, u1, v2);
		t.addVertexWithUV(x + core.tX + topWidth, y + 1, z + core.tZ + topWidth, u1, v1);
		t.addVertexWithUV(x + core.tX - topWidth, y + 1, z + core.tZ + topWidth, u1 + texTopWidth, v1);

		icon = block.getIcon(MIDDLE, 0);
		u1 = icon.getMinU();
		v1 = icon.getMinV();

		// top
		if (distUp == 0) {
			t.setNormal(0, 1, 0);
			t.addVertexWithUV(x + core.tX - topWidth, y + 1, z + core.tZ - topWidth, u1, v1);
			t.addVertexWithUV(x + core.tX - topWidth, y + 1, z + core.tZ + topWidth, u1 + texTopWidth, v1);
			t.addVertexWithUV(x + core.tX + topWidth, y + 1, z + core.tZ + topWidth, u1 + texTopWidth, v1 + texTopWidth);
			t.addVertexWithUV(x + core.tX + topWidth, y + 1, z + core.tZ - topWidth, u1, v1 + texTopWidth);
		}

		// bottom
		if (distDown == 0) {
			t.setNormal(0, -1, 0);
			t.addVertexWithUV(x + core.bX - bottomWidth, y, z + core.bZ + bottomWidth, u1 + texBottomWidth, v1);
			t.addVertexWithUV(x + core.bX - bottomWidth, y, z + core.bZ - bottomWidth, u1, v1);
			t.addVertexWithUV(x + core.bX + bottomWidth, y, z + core.bZ - bottomWidth, u1, v1 + texBottomWidth);
			t.addVertexWithUV(x + core.bX + bottomWidth, y, z + core.bZ + bottomWidth, u1 + texBottomWidth, v1 + texBottomWidth);
		}

		return true;
	}
}

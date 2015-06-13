package thebetweenlands.blocks.terrain;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.client.render.block.water.IWaterRenderer;
import thebetweenlands.items.ItemRubberBoots;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockSwampWater extends BlockFluidClassic {
	@SideOnly(Side.CLIENT)
	protected IIcon stillIcon, flowingIcon;

	protected boolean canSpread = true;
	protected boolean hasBoundingBox = false;
	protected boolean canCollide = false;

	private static final HashMap<Block, IWaterRenderer> SPECIAL_RENDERERS = new HashMap<Block, IWaterRenderer>();

	public BlockSwampWater() {
		super(BLFluidRegistry.swampWater, Material.water);
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
		setBlockName("thebetweenlands.swampWater");
	}

	public BlockSwampWater(Fluid fluid, Material material) {
		super(fluid, material);
	}

	public BlockSwampWater setSpecialRenderer(IWaterRenderer renderer) {
		SPECIAL_RENDERERS.put(this, renderer);
		return this;
	}

	public IWaterRenderer getSpecialRenderer() {
		if(SPECIAL_RENDERERS.containsKey(this)) {
			return SPECIAL_RENDERERS.get(this);
		}
		return null;
	}

	@Override
	public IIcon getIcon(int side, int meta) {
		IWaterRenderer renderer = this.getSpecialRenderer();
		if(renderer != null) {
			return renderer.getIcon();
		}
		return side == 0 || side == 1 ? stillIcon : flowingIcon;
	}

	public IIcon getWaterIcon(int side) {
		return side == 0 || side == 1 ? this.stillIcon : this.flowingIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		this.stillIcon = register.registerIcon("thebetweenlands:swampWater");
		this.flowingIcon = register.registerIcon("thebetweenlands:swampWaterFlowing");
	}

	@Override
	public boolean canDisplace(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z).isAir(world, x, y, z)) return true;

		Block block = world.getBlock(x, y, z);

		if (this.canConnectTo(block))
		{
			return false;
		}

		if (displacements.containsKey(block))
		{
			return displacements.get(block);
		}

		Material material = block.getMaterial();
		if (material.blocksMovement() || material == Material.portal)
		{
			return false;
		}

		int density = getDensity(world, x, y, z);
		if (density == Integer.MAX_VALUE) 
		{
			return true;
		}

		if (this.density > density)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@SideOnly(Side.CLIENT)
	@Override
	public int colorMultiplier(IBlockAccess blockAccess, int x, int y, int z) {
		int avgRed = 0;
		int avgGreen = 0;
		int avgBlue = 0;

		for (int xOff = -1; xOff <= 1; ++xOff) {
			for (int yOff = -1; yOff <= 1; ++yOff) {
				int colorMultiplier = blockAccess.getBiomeGenForCoords(x + yOff, z + xOff).getWaterColorMultiplier();
				avgRed += (colorMultiplier & 16711680) >> 16;
			avgGreen += (colorMultiplier & 65280) >> 8;
			avgBlue += colorMultiplier & 255;
			}
		}

		return (avgRed / 9 & 255) << 16 | (avgGreen / 9 & 255) << 8 | avgBlue / 9 & 255;
	}

	@Override
	public boolean isFlowingVertically(IBlockAccess world, int x, int y, int z) {
		return this.canConnectTo(world.getBlock(x, y + densityDir, z)) ||
				(this.canConnectTo(world.getBlock(x, y, z)) && canFlowInto(world, x, y + densityDir, z));
	}

	@Override
	public boolean isSourceBlock(IBlockAccess world, int x, int y, int z) {
		return this.canConnectTo(world.getBlock(x, y, z)) && world.getBlockMetadata(x, y, z) == 0;
	}

	@Override
	protected boolean canFlowInto(IBlockAccess world, int x, int y, int z) {
		if (world.getBlock(x, y, z).isAir(world, x, y, z)) return true;

		Block block = world.getBlock(x, y, z);
		if (this.canConnectTo(block)) {
			return true;
		}

		if (displacements.containsKey(block))
		{
			return displacements.get(block);
		}

		Material material = block.getMaterial();
		if (material.blocksMovement()  ||
				material == Material.water ||
				material == Material.lava  ||
				material == Material.portal)
		{
			return false;
		}

		int density = getDensity(world, x, y, z);
		if (density == Integer.MAX_VALUE) 
		{
			return true;
		}

		if (this.density > density)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	protected void flowIntoBlock(World world, int x, int y, int z, int meta) {
		if(!this.canSpread) {
			return;
		}
		if (meta < 0) return;
		if (displaceIfPossible(world, x, y, z) && this.canSpread) {
			world.setBlock(x, y, z, this, meta, 3);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		if(!this.canSpread) {
			return;
		}
		int quantaRemaining = quantaPerBlock - world.getBlockMetadata(x, y, z);
		int expQuanta = -101;

		// check adjacent block levels if non-source
		if (quantaRemaining < quantaPerBlock)
		{
			int y2 = y - densityDir;

			if (this.canConnectTo(world.getBlock(x,     y2, z    )) ||
					this.canConnectTo(world.getBlock(x - 1, y2, z    )) ||
					this.canConnectTo(world.getBlock(x + 1, y2, z    )) ||
					this.canConnectTo(world.getBlock(x,     y2, z - 1)) ||
					this.canConnectTo(world.getBlock(x,     y2, z + 1)))
			{
				expQuanta = quantaPerBlock - 1;
			}
			else
			{
				int maxQuanta = -100;
				maxQuanta = getLargerQuanta(world, x - 1, y, z,     maxQuanta);
				maxQuanta = getLargerQuanta(world, x + 1, y, z,     maxQuanta);
				maxQuanta = getLargerQuanta(world, x,     y, z - 1, maxQuanta);
				maxQuanta = getLargerQuanta(world, x,     y, z + 1, maxQuanta);

				expQuanta = maxQuanta - 1;
			}

			// decay calculation
			if (expQuanta != quantaRemaining)
			{
				quantaRemaining = expQuanta;

				if (expQuanta <= 0)
				{
					world.setBlock(x, y, z, Blocks.air);
				}
				else
				{
					world.setBlockMetadataWithNotify(x, y, z, quantaPerBlock - expQuanta, 3);
					world.scheduleBlockUpdate(x, y, z, this, tickRate);
					world.notifyBlocksOfNeighborChange(x, y, z, this);
				}
			}
		}
		// This is a "source" block, set meta to zero, and send a server only update
		else if (quantaRemaining >= quantaPerBlock)
		{
			world.setBlockMetadataWithNotify(x, y, z, 0, 2);
		}

		// Flow vertically if possible
		if (canDisplace(world, x, y + densityDir, z))
		{
			flowIntoBlock(world, x, y + densityDir, z, 1);
			return;
		}

		// Flow outward if possible
		int flowMeta = quantaPerBlock - quantaRemaining + 1;
		if (flowMeta >= quantaPerBlock)
		{
			return;
		}

		if (isSourceBlock(world, x, y, z) || !isFlowingVertically(world, x, y, z))
		{
			if (this.canConnectTo(world.getBlock(x, y - densityDir, z)))
			{
				flowMeta = 1;
			}
			boolean flowTo[] = getOptimalFlowDirections(world, x, y, z);

			if (flowTo[0]) flowIntoBlock(world, x - 1, y, z,     flowMeta);
			if (flowTo[1]) flowIntoBlock(world, x + 1, y, z,     flowMeta);
			if (flowTo[2]) flowIntoBlock(world, x,     y, z - 1, flowMeta);
			if (flowTo[3]) flowIntoBlock(world, x,     y, z + 1, flowMeta);
		}
	}

	@Override
	public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
		Block block = world.getBlock(x, y, z);
		if(side == 0 && block != BLBlockRegistry.swampWater) {
			return true;
		}
		if (!this.canConnectTo(block))
		{
			if(side == 1) {
				return true;
			} else {
				return !block.isOpaqueCube();
			}
		}
		return (block.getMaterial() == this.getMaterial() || this.canConnectTo(block)) ? false : super.shouldSideBeRendered(world, x, y, z, side);
	}

	@Override
	public boolean displaceIfPossible(World world, int x, int y, int z)
	{
		if(!this.canSpread) {
			return false;
		}
		if (world.getBlock(x, y, z).isAir(world, x, y, z))
		{
			return true;
		}

		Block block = world.getBlock(x, y, z);
		if (this.canConnectTo(block))
		{
			return false;
		}

		if (displacements.containsKey(block))
		{
			if (displacements.get(block))
			{
				block.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
				return true;
			}
			return false;
		}

		Material material = block.getMaterial();
		if (material.blocksMovement() || material == Material.portal)
		{
			return false;
		}

		int density = getDensity(world, x, y, z);
		if (density == Integer.MAX_VALUE) 
		{
			block.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			return true;
		}

		if (this.density > density)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	@Override
	public Vec3 getFlowVector(IBlockAccess world, int x, int y, int z)
	{
		if(!this.canSpread) {
			return Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
		}
		Vec3 vec = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);
		int decay = quantaPerBlock - getQuantaValue(world, x, y, z);

		for (int side = 0; side < 4; ++side)
		{
			int x2 = x;
			int z2 = z;

			switch (side)
			{
			case 0: --x2; break;
			case 1: --z2; break;
			case 2: ++x2; break;
			case 3: ++z2; break;
			}

			Block otherBlock = world.getBlock(x2, y, z2);
			if(otherBlock instanceof BlockSwampWater && ((BlockSwampWater)otherBlock).canSpread) {
				int otherDecay = quantaPerBlock - getQuantaValue(world, x2, y, z2);
				if (otherDecay >= quantaPerBlock)
				{
					if (!world.getBlock(x2, y, z2).getMaterial().blocksMovement() && !this.canConnectTo(world.getBlock(x2, y, z2)))
					{
						otherDecay = quantaPerBlock - getQuantaValue(world, x2, y - 1, z2);
						if (otherDecay >= 0)
						{
							int power = otherDecay - (decay - quantaPerBlock);
							vec = vec.addVector((x2 - x) * power, (y - y) * power, (z2 - z) * power);
						}
					}
				}
				else if (otherDecay >= 0)
				{
					int power = otherDecay - decay;
					vec = vec.addVector((x2 - x) * power, (y - y) * power, (z2 - z) * power);
				}
			}
		}

		if (this.canConnectTo(world.getBlock(x, y + 1, z)))
		{
			boolean flag =
					isBlockSolid(world, x,     y,     z - 1, 2) ||
					isBlockSolid(world, x,     y,     z + 1, 3) ||
					isBlockSolid(world, x - 1, y,     z,     4) ||
					isBlockSolid(world, x + 1, y,     z,     5) ||
					isBlockSolid(world, x,     y + 1, z - 1, 2) ||
					isBlockSolid(world, x,     y + 1, z + 1, 3) ||
					isBlockSolid(world, x - 1, y + 1, z,     4) ||
					isBlockSolid(world, x + 1, y + 1, z,     5);

			if (flag)
			{
				vec = vec.normalize().addVector(0.0D, -6.0D, 0.0D);
			}
		}
		vec = vec.normalize();
		return vec;
	}

	@Override
	public int getQuantaValue(IBlockAccess world, int x, int y, int z) {
		if(this.canSpread) {
			return super.getQuantaValue(world, x, y, z);
		} else {
			return this.quantaPerBlock;
		}
	}

	@Override
	public int getRenderType() {
		return BlockRenderIDs.SWAMP_WATER.id();
	}

	@Override
	public boolean canCollideCheck(int meta, boolean fullHit) {
		return this.hasBoundingBox || fullHit && (!this.canSpread || meta == 0);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB bb, List bbList, Entity entity) {
		if(this.canCollide) {
			AxisAlignedBB blockBB = this.getCollisionBoundingBoxFromPool(world, x, y, z);
			if (blockBB != null && bb.intersectsWith(blockBB)) {
				bbList.add(blockBB);
			}
		} else {
			if(entity instanceof EntityPlayer && ItemRubberBoots.checkPlayerEffect((EntityPlayer)entity)) {
				AxisAlignedBB blockBB = AxisAlignedBB.getBoundingBox(x, y, z, x+1, y+((float)this.getQuantaValue(world, x, y, z)/(float)this.quantaPerBlock)*0.8F, z+1);
				if (blockBB != null && bb.intersectsWith(blockBB)) {
					bbList.add(blockBB);
				}
			}
		}
	}

	@Override
	public boolean isCollidable() {
		return this.canCollide;
	}

	public boolean canConnectTo(Block block) {
		return block == this || block == BLBlockRegistry.swampWater || SPECIAL_RENDERERS.containsKey(block);
	}
}
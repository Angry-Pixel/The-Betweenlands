package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.particle.BLParticle;
import thebetweenlands.client.render.block.water.WaterSimplePlantRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemGeneric;
import thebetweenlands.items.ItemGeneric.EnumItemGeneric;

public class BlockSwampKelp extends BlockSwampWater implements IPlantable {
	public IIcon iconWaterPlantBottom;
	public IIcon iconWaterPlantTop;

	public BlockSwampKelp() {
		super(BLFluidRegistry.swampWaterWaterWeeds, Material.water);
		setStepSound(Block.soundTypeGrass);
		setBlockName("thebetweenlands.waterWeeds");
		setHardness(0.5F);
		setLightLevel(0.875F);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockBounds(0.1f, 0.0f, 0.1f, 1.0f, 0.9f, 0.9f);
		setTickRandomly(true);
		this.canSpread = false;
		this.hasBoundingBox = true;
		this.canReplenish = false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.iconWaterPlantBottom = iconRegister.registerIcon("thebetweenlands:waterPlantBottom");
		this.iconWaterPlantTop = iconRegister.registerIcon("thebetweenlands:waterPlantTop");
		this.setSpecialRenderer(new WaterSimplePlantRenderer(this.iconWaterPlantBottom, this.iconWaterPlantTop));
		super.registerBlockIcons(iconRegister);
	}

	@Override
	public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
		return EnumPlantType.Water;
	}

	@Override
	public Block getPlant(IBlockAccess world, int x, int y, int z) {
		return this;
	}

	@Override
	public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
		return world.getBlockMetadata(x, y, z);
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z) && this.canPlaceBlockOn(world.getBlock(x, y-1, z));
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z) {
		return this.canPlaceBlockOn(world.getBlock(x, y-1, z));
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		super.onNeighborBlockChange(world, x, y, z, block);
		this.checkAndDropBlock(world, x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		this.checkAndDropBlock(world, x, y, z);
		if (world.getBlock(x, y + 1, z) == BLBlockRegistry.swampWater) {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta == 10) {
				if(world.isAirBlock(x, y + 1, z)) {
					world.setBlock(x, y + 1, z, BLBlockRegistry.swampKelp);
				} else {
					world.setBlock(x, y + 1, z, this);
				}
				world.setBlockMetadataWithNotify(x, y, z, 0, 4);
			} else {
				world.setBlockMetadataWithNotify(x, y, z, meta + 1, 4);
			}
		}
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
			world.notifyBlockChange(x, y, z, Blocks.air);
		}
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(ItemGeneric.createStack(EnumItemGeneric.SWAMP_KELP, 1 + fortune));
		return drops;
	}

	protected boolean canPlaceBlockOn(Block block) {
		return block instanceof BlockSwampKelp || block == BLBlockRegistry.mud;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
		if(world.rand.nextInt(35) == 0) {
			BLParticle.WATER_BUG.spawn(world, x, y, z);
		}
	}
}

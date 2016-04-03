package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.render.block.water.WaterSimplePlantRenderer;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.items.herblore.ItemGenericPlantDrop;
import thebetweenlands.items.herblore.ItemGenericPlantDrop.EnumItemPlantDrop;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.items.tools.ISickleHarvestable;
import thebetweenlands.items.tools.ISyrmoriteShearable;

public class BlockWaterFlowerStalk extends BlockSwampWater implements IPlantable, ISyrmoriteShearable, ISickleHarvestable {
	public IIcon iconStalk;

	public BlockWaterFlowerStalk() {
		super(BLFluidRegistry.swampWaterStalk, Material.water);
		setStepSound(Block.soundTypeGrass);
		setBlockName("thebetweenlands.waterFlowerStalk");
		setHardness(0.5F);
		setCreativeTab(BLCreativeTabs.plants);
		setBlockBounds(0.1f, 0.0f, 0.1f, 1.0f, 0.9f, 0.9f);
		setTickRandomly(true);
		this.canSpread = false;
		this.canReplenish = false;
		this.hasBoundingBox = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.iconStalk = iconRegister.registerIcon("thebetweenlands:waterFlowerStalk");
		this.setSpecialRenderer(new WaterSimplePlantRenderer(this.iconStalk));
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
		return super.canPlaceBlockAt(world, x, y, z) && this.canBlockStay(world, x, y, z) && this.canPlaceBlockOn(world.getBlock(x, y-1, z)) && world.getBlock(x, y, z).getMaterial() == Material.water;
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
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return ItemGeneric.createStack(EnumItemGeneric.SWAMP_REED).getItemDamage();
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random random, int meta) {
		return null;
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
			world.notifyBlockChange(x, y, z, Blocks.air);
		}
	}

	protected boolean canPlaceBlockOn(Block block) {
		return block instanceof BlockWaterFlowerStalk || block == BLBlockRegistry.mud;
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
		drops.add(ItemGenericPlantDrop.createStack(EnumItemPlantDrop.WATER_FLOWER_STALK));
		return drops;
	}

	@Override
	public boolean isSyrmoriteShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ItemStack getSyrmoriteShearableSpecialDrops(Block block, int x, int y, int z, int meta) {
		return null;
	}
}

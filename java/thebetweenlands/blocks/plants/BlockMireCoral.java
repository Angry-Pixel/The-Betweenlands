package thebetweenlands.blocks.plants;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.render.block.water.WaterSimplePlantRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockMireCoral extends BlockSwampWater implements IPlantable {
	public IIcon iconMireCoral;

	public BlockMireCoral() {
		super(BLFluidRegistry.swampWaterMireCoral, Material.water);
		setStepSound(Block.soundTypeGrass);
		setBlockName("thebetweenlands.mireCoral");
		setHardness(0.5F);
		setCreativeTab(ModCreativeTabs.plants);
		setBlockBounds(0.1f, 0.0f, 0.1f, 0.9f, 0.9f, 0.9f);
		setTickRandomly(true);
		setLightLevel(1.0F);
		this.canSpread = false;
		this.hasBoundingBox = true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		this.iconMireCoral = iconRegister.registerIcon("thebetweenlands:mireCoral");
		this.setSpecialRenderer(new WaterSimplePlantRenderer(this.iconMireCoral));
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
	public void onNeighborBlockChange(World p_149695_1_, int p_149695_2_, int p_149695_3_, int p_149695_4_, Block p_149695_5_) {
		super.onNeighborBlockChange(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_, p_149695_5_);
		this.checkAndDropBlock(p_149695_1_, p_149695_2_, p_149695_3_, p_149695_4_);
	}

	@Override
	public void updateTick(World p_149674_1_, int p_149674_2_, int p_149674_3_, int p_149674_4_, Random p_149674_5_) {
		this.checkAndDropBlock(p_149674_1_, p_149674_2_, p_149674_3_, p_149674_4_);
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.MIRE_CORAL).getItemDamage();
	}

	@Override
	public int quantityDropped(Random rnd) {
        return 1;
    }
	
	@Override
	public int damageDropped(int p_149692_1_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.MIRE_CORAL).getItemDamage();
    }
	
	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.MIRE_CORAL).getItem();
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
		}
	}

	public boolean canPlaceBlockOn(Block block) {
		return block == BLBlockRegistry.mud;
	}
}

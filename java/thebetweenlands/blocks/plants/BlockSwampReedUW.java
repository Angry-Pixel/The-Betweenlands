package thebetweenlands.blocks.plants;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import thebetweenlands.blocks.BLBlockRegistry;
import thebetweenlands.blocks.BLFluidRegistry;
import thebetweenlands.blocks.terrain.BlockSwampWater;
import thebetweenlands.client.render.block.water.SwampReedUWRenderer;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.items.ItemMaterialsBL;
import thebetweenlands.items.ItemMaterialsBL.EnumMaterialsBL;

public class BlockSwampReedUW extends BlockSwampWater implements IPlantable {
	public BlockSwampReedUW() {
		super(BLFluidRegistry.swampWaterReed, Material.water);
		setStepSound(Block.soundTypeGrass);
		setBlockName("thebetweenlands.swampReedBlockUW");
		setHardness(0.5F);
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
		this.setSpecialRenderer(new SwampReedUWRenderer());
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
	public int getDamageValue(World world, int x, int y, int z) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED).getItemDamage();
	}

	@Override
	public int quantityDropped(Random rnd) {
		return 1;
	}

	@Override
	public int damageDropped(int p_149692_1_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED).getItemDamage();
	}

	@Override
	public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
		return ItemMaterialsBL.createStack(EnumMaterialsBL.SWAMP_REED).getItem();
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
		return false;
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand) {
		this.checkAndDropBlock(world, x, y, z);
		if (world.isAirBlock(x, y + 1, z) || world.getBlock(x, y + 1, z) == BLBlockRegistry.swampWater) {
			int meta = world.getBlockMetadata(x, y, z);
			if (meta == 10) {
				if(world.isAirBlock(x, y + 1, z)) {
					world.setBlock(x, y + 1, z, BLBlockRegistry.swampReed);
				} else {
					world.setBlock(x, y + 1, z, this);
				}
				world.setBlockMetadataWithNotify(x, y, z, 0, 4);
			} else {
				world.setBlockMetadataWithNotify(x, y, z, meta + 1, 4);
			}
		}
	}

	protected void checkAndDropBlock(World world, int x, int y, int z) {
		if (!this.canBlockStay(world, x, y, z)) {
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, 2);
			world.notifyBlockChange(x, y, z, Blocks.air);
		}
	}

	protected boolean canPlaceBlockOn(Block block) {
		return block instanceof BlockSwampReedUW || block == BLBlockRegistry.mud;
	}
}

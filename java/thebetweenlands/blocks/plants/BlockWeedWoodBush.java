package thebetweenlands.blocks.plants;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.entities.WeedWoodBushUncollidableEntity;
import thebetweenlands.items.misc.ItemGeneric;
import thebetweenlands.items.misc.ItemGeneric.EnumItemGeneric;
import thebetweenlands.items.tools.ISickleHarvestable;
import thebetweenlands.items.tools.ISyrmoriteShearable;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;

public class BlockWeedWoodBush extends Block implements ISyrmoriteShearable, ISickleHarvestable {
	@SideOnly(Side.CLIENT)
	public IIcon iconFancy, iconFast, iconStick;

	public BlockWeedWoodBush() {
		super(Material.leaves);
		setHardness(0.35F);
		setStepSound(soundTypeGrass);
		setCreativeTab(BLCreativeTabs.blocks);
		setBlockName(ModInfo.ID + ".weedwoodbush");
		setBlockTextureName(ModInfo.ID + ":weedwoodLeavesFancy");
		setBlockBounds(0.1F, 0, 0.1F, 0.9F, 0.9F, 0.9F);
	}

	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB bounds, List boxes, Entity entity) {
		if (entity instanceof WeedWoodBushUncollidableEntity) {
			return;
		}
		super.addCollisionBoxesToList(world, x, y, z, bounds, boxes, entity);
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
	public int getRenderType() {
		return BlockRenderIDs.WEEDWOOD_BUSH.id();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconFancy = iconRegister.registerIcon(ModInfo.ID + ":weedwoodLeavesFancy");
		iconFast = iconRegister.registerIcon(ModInfo.ID + ":weedwoodLeavesFast");
		iconStick = iconRegister.registerIcon(ModInfo.ID + ":weedWoodStick");
		super.registerBlockIcons(iconRegister);
	}

	@Override
	public ItemStack getSyrmoriteShearableSpecialDrops(Block block, int x, int y, int z, int meta) {
		return null;
	}

	@Override
	public boolean isSyrmoriteShearable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public boolean isHarvestable(ItemStack item, IBlockAccess world, int x, int y, int z) {
		return true;
	}

	@Override
	public ArrayList<ItemStack> getHarvestableDrops(ItemStack item, IBlockAccess world, int x, int y, int z, int fortune) {
		ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
		ret.add(ItemGeneric.createStack(EnumItemGeneric.WEEDWOOD_STICK));
		return ret;
	}
}

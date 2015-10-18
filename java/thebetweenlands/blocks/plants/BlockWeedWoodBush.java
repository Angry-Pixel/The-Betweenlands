package thebetweenlands.blocks.plants;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.entities.WeedWoodBushUncollidableEntity;
import thebetweenlands.lib.ModInfo;
import thebetweenlands.proxy.ClientProxy.BlockRenderIDs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWeedWoodBush extends Block {
	@SideOnly(Side.CLIENT)
	public IIcon iconFancy, iconFast, iconStick;

	public BlockWeedWoodBush() {
		super(Material.leaves);
		setHardness(0.35F);
		setStepSound(soundTypeGrass);
		setCreativeTab(ModCreativeTabs.blocks);
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
}

package thebetweenlands.blocks.tree;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.ModCreativeTabs;
import thebetweenlands.entities.mobs.EntityTermite;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockBLLog extends BlockLog {

	private String type;
	@SideOnly(Side.CLIENT)
	private IIcon iconSide, iconTop, spoopyIconSide, spoopyIconTop;

	public BlockBLLog(String blockName) {
		setCreativeTab(ModCreativeTabs.plants);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
		if (type.equals("sapTreeLog")) {
			ArrayList<ItemStack> drops = new ArrayList<ItemStack>();;
			for (int i = 0; i < 1 + world.rand.nextInt(2 + fortune); i++)
				drops.add(new ItemStack(BLItemRegistry.sapBall));
			return drops;
		}
		return super.getDrops(world, x, y, z, metadata, fortune);
	}

	@Override
	public boolean canSilkHarvest(World world, EntityPlayer player, int x, int y, int z, int metadata) {
		return type.equals("sapTreeLog") ? true : super.canSilkHarvest(world, player, x, y, z, metadata);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getSideIcon(int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && (this.type.equals("weedwoodLog") || type.equals("sapTreeLog") || type.equals("rubberTreeLog") || type.equals("weedwoodBark"))) {
			return this.spoopyIconSide;
		}
		return iconSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getTopIcon(int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && (this.type.equals("weedwoodLog") || type.equals("sapTreeLog") || type.equals("rubberTreeLog") || type.equals("weedwoodBark"))) {
			return this.spoopyIconTop;
		}
		return iconTop;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconSide = iconRegister.registerIcon(getTextureName());
		if(type.equals("weedwood") || type.equals("weedwoodBark") || type.equals("rottenWeedwoodBark") || type.equals("portalBark"))
			iconTop = iconSide;
		else
			iconTop = iconRegister.registerIcon(getTextureName()+"Top");
		if(type.equals("weedwoodLog") || type.equals("sapTreeLog") || type.equals("rubberTreeLog") || type.equals("weedwoodBark")) {
			this.spoopyIconSide = iconRegister.registerIcon(getTextureName()+"Spoopy");
			if(type.equals("weedwoodBark")) {
				this.spoopyIconTop = this.spoopyIconSide;
			} else {
				this.spoopyIconTop = iconRegister.registerIcon(getTextureName()+"TopSpoopy");
			}
		}
	}

	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int meta) {
		if (!world.isRemote)
			if ("rottenWeedwoodBark".equals(type) && world.rand.nextInt(3) == 0) {
				EntityTermite entity = new EntityTermite(world);
				entity.setLocationAndAngles(x + 0.5D, y, z + 0.5D, 0.0F, 0.0F);
				world.spawnEntityInWorld(entity);
			}
		super.onBlockDestroyedByPlayer(world, x, y, z, meta);
	}	  

	public String getType(){
		return type;
	}
}
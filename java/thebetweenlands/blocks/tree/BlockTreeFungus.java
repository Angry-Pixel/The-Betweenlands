package thebetweenlands.blocks.tree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.world.events.impl.EventSpoopy;

import java.util.Random;

public class BlockTreeFungus extends Block {

	@SideOnly(Side.CLIENT)
	private IIcon topIcon, sideIcon, spoopyTopIcon, spoopySideIcon;

	public BlockTreeFungus() {
        super(Material.wood);
		setStepSound(Block.soundTypeCloth);
		setHardness(0.2F);
		setCreativeTab(BLCreativeTabs.plants);
		setBlockName("thebetweenlands.treeFungus");
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z) {
		return Item.getItemFromBlock(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld)) {
			if( side == 2 || side == 3 || side == 4 || side == 5 ) {
				return this.spoopySideIcon;
			} else if( side == 1 ) {
				return this.spoopyTopIcon;
			}
			
			return this.spoopyTopIcon;
		} else {
			if( side == 2 || side == 3 || side == 4 || side == 5 ) {
				return this.sideIcon;
			} else if( side == 1 ) {
				return this.topIcon;
			}
			
			return this.blockIcon;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		this.blockIcon = reg.registerIcon("thebetweenlands:treeFungus");
		this.sideIcon = reg.registerIcon("thebetweenlands:treeFungusSide");
		this.topIcon = reg.registerIcon("thebetweenlands:treeFungus");
		this.spoopySideIcon = reg.registerIcon("thebetweenlands:treeFungusSideSpoopy");
		this.spoopyTopIcon = reg.registerIcon("thebetweenlands:treeFungusSpoopy");
	}

}

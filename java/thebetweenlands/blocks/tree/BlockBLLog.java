package thebetweenlands.blocks.tree;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import thebetweenlands.creativetabs.BLCreativeTabs;
import thebetweenlands.world.events.impl.EventSpoopy;

public class BlockBLLog extends BlockLog {

	private String type;
	@SideOnly(Side.CLIENT)
	private IIcon iconSide, iconTop, spoopyIconSide, spoopyIconTop;

	private boolean hasSpoopyTexture = false;
	private boolean hasSeperateTopIcon = true;

	public BlockBLLog(String blockName) {
		setCreativeTab(BLCreativeTabs.plants);
		type = blockName;
		setBlockName("thebetweenlands." + type);
		setBlockTextureName("thebetweenlands:" + type);
	}

	public BlockBLLog setHasSpoopyTexture(boolean spoopyTexture) {
		this.hasSpoopyTexture = spoopyTexture;
		return this;
	}

	public BlockBLLog setHasSperateTopIcon(boolean seperateTopIcon) {
		this.hasSeperateTopIcon = seperateTopIcon;
		return this;
	}

	@Override
	public String getLocalizedName() {
		return String.format(StatCollector.translateToLocal("tile.thebetweenlands." + type));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getSideIcon(int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && this.hasSpoopyTexture) {
			return this.spoopyIconSide;
		}
		return iconSide;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getTopIcon(int meta) {
		if(EventSpoopy.isSpoopy(Minecraft.getMinecraft().theWorld) && this.hasSpoopyTexture) {
			return this.spoopyIconTop;
		}
		return iconTop;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		iconSide = iconRegister.registerIcon(getTextureName());
		if(!this.hasSeperateTopIcon)
			iconTop = iconSide;
		else
			iconTop = iconRegister.registerIcon(getTextureName() + "Top");
		if(this.hasSpoopyTexture) {
			this.spoopyIconSide = iconRegister.registerIcon(getTextureName() + "Spoopy");
			if(!this.hasSeperateTopIcon) {
				this.spoopyIconTop = this.spoopyIconSide;
			} else {
				this.spoopyIconTop = iconRegister.registerIcon(getTextureName() + "TopSpoopy");
			}
		}
	}

	public String getType(){
		return type;
	}
}
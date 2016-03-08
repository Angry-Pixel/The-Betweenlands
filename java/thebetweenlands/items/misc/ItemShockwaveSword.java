package thebetweenlands.items.misc;

import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShockwaveSword extends ItemSword {

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemShockwaveSword(ToolMaterial material) {
		super(material);
        setHasSubtypes(true);
        setUnlocalizedName("thebetweenlands.shockwaveSword");
        setTextureName("thebetweenlands:shockwaveSword");
	}
}

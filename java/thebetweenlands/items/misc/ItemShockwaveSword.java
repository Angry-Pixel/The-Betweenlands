package thebetweenlands.items.misc;

import net.minecraft.item.ItemSword;
import net.minecraft.util.IIcon;
import thebetweenlands.items.tools.ItemSwordBL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemShockwaveSword extends ItemSwordBL {

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	public ItemShockwaveSword(ToolMaterial material) {
		super(material);
        setHasSubtypes(true);
        setUnlocalizedName("thebetweenlands.shockwaveSword");
        setTextureName("thebetweenlands:shockwaveSword");
	}
}

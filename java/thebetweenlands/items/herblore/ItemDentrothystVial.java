package thebetweenlands.items.herblore;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemDentrothystVial extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon iconVialOrange, iconVialDirty;

	public ItemDentrothystVial() {
		this.setUnlocalizedName("item.thebetweenlands.dentrothystVial");

		this.setHasSubtypes(true);
		this.setMaxDamage(0);

		this.setTextureName("thebetweenlands:strictlyHerblore/misc/vialGreen");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		this.iconVialOrange = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialOrange");
		this.iconVialDirty = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialDirty");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		switch(damage) {
		case 0:
			return this.itemIcon;
		case 1:
			return this.iconVialDirty;
		case 2:
			return this.iconVialOrange;
		}
		return this.itemIcon;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item item, CreativeTabs tab, List list) {
		list.add(new ItemStack(item, 1, 0)); //green
		list.add(new ItemStack(item, 1, 1)); //green dirty
		list.add(new ItemStack(item, 1, 2)); //orange
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		try {
			switch(stack.getItemDamage()) {
			case 0:
				return "item.thebetweenlands.elixir.dentrothystVial.green";
			case 1:
				return "item.thebetweenlands.elixir.dentrothystVial.dirty";
			case 2:
				return "item.thebetweenlands.elixir.dentrothystVial.orange";
			}
		} catch (Exception e) { }
		return "item.thebetweenlands.unknown";
	}

	/**
	 * Creates an item stack of the specified vial type.
	 * Vial types: 0 = green, 1 = dirty, 2 = orange
	 * @param vialType
	 * @return
	 */
	public ItemStack createStack(int vialType) {
		return new ItemStack(this, 1, vialType);
	}
	
	/**
	 * Creates an item stack of the specified vial type.
	 * Vial types: 0 = green, 1 = dirty, 2 = orange
	 * @param vialType
	 * @param size
	 * @return
	 */
	public ItemStack createStack(int vialType, int size) {
		return new ItemStack(this, size, vialType);
	}
}

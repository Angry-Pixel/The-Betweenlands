package thebetweenlands.items.herblore;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import thebetweenlands.herblore.aspects.AspectRecipes;
import thebetweenlands.herblore.aspects.ItemAspect;

public class ItemAspectVial extends Item {
	@SideOnly(Side.CLIENT)
	private IIcon iconLiquid, iconVialOrange;

	public ItemAspectVial() {
		this.setUnlocalizedName("item.thebetweenlands.aspectVial");

		this.setMaxStackSize(1);

		this.setTextureName("thebetweenlands:strictlyHerblore/misc/vialGreen");
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		List<ItemAspect> itemAspects = AspectRecipes.REGISTRY.getItemAspects(stack);
		if(itemAspects.size() >= 1) {
			ItemAspect aspect = itemAspects.get(0);
			return super.getItemStackDisplayName(stack) + " - " + aspect.aspect.getName() + " (" + aspect.amount + ")";
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		this.iconLiquid = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialLiquid");
		this.iconVialOrange = reg.registerIcon("thebetweenlands:strictlyHerblore/misc/vialOrange");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if(pass == 0) {
			//Liquid color
		}
		return 0xFFFFFFFF;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
		return pass == 0 ? this.iconLiquid : super.getIconFromDamageForRenderPass(damage, pass);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return damage % 2 == 0 ? this.itemIcon : this.iconVialOrange;
	}
}

package thebetweenlands.common.item.herblore;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import thebetweenlands.api.rune.impl.AbstractRune;
import thebetweenlands.client.tab.BLCreativeTabs;

public class ItemRune extends Item {
	private AbstractRune.Blueprint<?> blueprint;

	public ItemRune(AbstractRune.Blueprint<?> blueprint) {
		this.setMaxStackSize(1);
		this.setMaxDamage(30);
		this.setCreativeTab(BLCreativeTabs.SPECIALS);
		this.blueprint = blueprint;
	}

	public AbstractRune.Blueprint<?> getRuneBlueprint(ItemStack stack) {
		return this.blueprint;
	}
	
	@Override
	public boolean hasEffect(ItemStack stack) {
		return true;
	}
	
	/*@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack) {
		return 0.5D;
	}*/
}

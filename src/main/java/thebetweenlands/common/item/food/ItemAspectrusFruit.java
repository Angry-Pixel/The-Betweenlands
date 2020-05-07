package thebetweenlands.common.item.food;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.translation.I18n;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.IAspectType;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.ITintedItem;
import thebetweenlands.common.registries.AspectRegistry;

public class ItemAspectrusFruit extends Item implements ITintedItem {
	public static final int DEFAULT_AMOUNT = 250;
	public static final float DEFAULT_AMOUNT_FLOAT = DEFAULT_AMOUNT / 1000.0f;
	
	public ItemAspectrusFruit() {
		this.setCreativeTab(BLCreativeTabs.HERBLORE);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
		if(this.isInCreativeTab(tab)) {
            items.add(new ItemStack(this));
            
            for(IAspectType aspect : AspectRegistry.ASPECT_TYPES) {
            	ItemStack stack = new ItemStack(this);
				ItemAspectContainer.fromItem(stack).set(aspect, DEFAULT_AMOUNT);
				items.add(stack);
            }
        }
	}
	
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();
		if(!itemAspects.isEmpty()) {
			Aspect aspect = itemAspects.get(0);
			if(Math.abs(aspect.getDisplayAmount() - DEFAULT_AMOUNT_FLOAT) > 0.01f) {
				//only show full name if amount is not the default amount on the aspectrus fruit (e.g. from older versions)
				return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".filled.name", aspect.type.getName(), aspect.getRoundedDisplayAmount()).trim();
			}
			return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".filled.default.name", aspect.type.getName(), aspect.getRoundedDisplayAmount()).trim();
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	public int getColorMultiplier(ItemStack stack, int tintIndex) {
		if(tintIndex == 1) {
			List<Aspect> aspects = ItemAspectContainer.fromItem(stack).getAspects();
			if(!aspects.isEmpty()) {
				return aspects.get(0).type.getColor();
			} else {
				return 0x00FFFFFF;
			}
		}
		return 0xFFFFFFFF;
	}
}

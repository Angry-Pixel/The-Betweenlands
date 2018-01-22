package thebetweenlands.common.item.food;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.client.handler.ScreenRenderHandler;

import javax.annotation.Nullable;

public class ItemAspectrusFruit extends Item {
	public ItemAspectrusFruit() {
		this.setCreativeTab(null);
	}

	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		List<Aspect> itemAspects = ItemAspectContainer.fromItem(stack).getAspects();
		if(!itemAspects.isEmpty()) {
			Aspect aspect = itemAspects.get(0);
			return I18n.translateToLocalFormatted(this.getUnlocalizedNameInefficiently(stack) + ".filled.name", aspect.type.getName(), aspect.getRoundedDisplayAmount()).trim();
		}
		return super.getItemStackDisplayName(stack);
	}
}

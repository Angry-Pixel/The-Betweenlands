package thebetweenlands.common.item.tool;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.datagen.tags.BLEntityTagProvider;
import thebetweenlands.common.registries.ToolMaterialRegistry;

public class HagHackerItem extends InstakillWeaponItem {
	public HagHackerItem(Properties properties) {
		super(ToolMaterialRegistry.WEEDWOOD, BLEntityTagProvider.HAG_HACKER_INSTAKILLS, properties);
	}

	@Override
	public boolean canDisableShield(ItemStack stack, ItemStack shield, LivingEntity entity, LivingEntity attacker) {
		return true;
	}
}

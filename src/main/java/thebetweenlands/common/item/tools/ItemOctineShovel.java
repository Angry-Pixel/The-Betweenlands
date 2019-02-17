package thebetweenlands.common.item.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemOctineShovel extends ItemBLShovel {
	public ItemOctineShovel() {
		super(BLMaterialRegistry.TOOL_OCTINE);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if(attacker.world.rand.nextFloat() < ItemOctineSword.getOctineToolFireChance(stack, target, attacker)) {
			target.setFire(5);
		}
		return super.hitEntity(stack, target, attacker);
	}
}

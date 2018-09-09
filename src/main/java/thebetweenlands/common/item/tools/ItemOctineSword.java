package thebetweenlands.common.item.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.capability.circlegem.CircleGemHelper;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemOctineSword extends ItemBLSword {
	public ItemOctineSword() {
		super(BLMaterialRegistry.TOOL_OCTINE);
		this.setCreativeTab(BLCreativeTabs.GEARS);
	}

	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		if(attacker.world.rand.nextFloat() < getOctineToolFireChance(stack, target, attacker)) {
			target.setFire(5);
		}
		return super.hitEntity(stack, target, attacker);
	}

	public static float getOctineToolFireChance(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		return CircleGemHelper.getGem(stack) == CircleGemType.CRIMSON ? 0.5F : 0.25F;
	}
}

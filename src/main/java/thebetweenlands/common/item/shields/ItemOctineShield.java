package thebetweenlands.common.item.shields;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import thebetweenlands.common.item.BLMaterialRegistry;
import thebetweenlands.common.item.tools.ItemBLShield;

public class ItemOctineShield extends ItemBLShield {
	public ItemOctineShield() {
		super(BLMaterialRegistry.TOOL_OCTINE);
	}

	@Override
	public void onAttackBlocked(ItemStack stack, EntityLivingBase attacked, float damage, DamageSource source) {
		if(source.getImmediateSource() != null) {
			source.getImmediateSource().setFire(4);
		}
		super.onAttackBlocked(stack, attacked, damage, source);
	}
}

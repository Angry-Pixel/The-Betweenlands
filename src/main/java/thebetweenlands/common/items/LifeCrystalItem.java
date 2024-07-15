package thebetweenlands.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class LifeCrystalItem extends Item {
	private final boolean isRechargeable;

	public LifeCrystalItem(boolean isRechargeable, Item.Properties properties) {
		super(properties);
		this.isRechargeable = isRechargeable;

		//TODO
//		this.addPropertyOverride(new ResourceLocation("remaining"), (stack, worldIn, entityIn) -> {
//			int damage = stack.getItemDamage();
//			if (damage >= stack.getMaxDamage())
//				return 4;
//			if (damage > stack.getMaxDamage() * 0.75f)
//				return 3;
//			if (damage > stack.getMaxDamage() * 0.5f)
//				return 2;
//			if (damage > stack.getMaxDamage() * 0.25f)
//				return 1;
//			return 0;
//		});
	}

	public boolean isRechargeable(ItemStack stack) {
		return this.isRechargeable;
	}

	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
		tooltip.add(Component.translatable("item.thebetweenlands.life_crystal.remaining", Math.round(100.0F - 100.0F / stack.getMaxDamage() * this.getDamage(stack)) + "%"));
	}

	@Override
	public boolean isEnchantable(ItemStack stack) {
		return false;
	}

	@Override
	public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
		return false;
	}

	@Override
	public boolean isBarVisible(ItemStack stack) {
		return false;
	}

	@Override
	public void setDamage(ItemStack stack, int damage) {
		int maxDamage = stack.getMaxDamage();
		if(damage > maxDamage && this.isRechargeable(stack)) {
			//Don't let the crystal break
			damage = maxDamage;
		}
		super.setDamage(stack, damage);
	}
}

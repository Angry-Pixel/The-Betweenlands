package thebetweenlands.common.item.equipment;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import thebetweenlands.common.TheBetweenlands;

public class RingOfPowerItem extends RingItem {

	private static final ResourceLocation RING_MODIFIER = TheBetweenlands.prefix("ring_of_power_bonus");

	public RingOfPowerItem(Properties properties) {
		super(properties);
	}

	@Override
	MutableComponent getUsageTooltip() {
		return Component.empty();
	}

	@Override
	public void onEquip(ItemStack stack, Entity entity, Container inventory) {
		if (entity instanceof LivingEntity living) {
			AttributeInstance speedAttrib = living.getAttribute(Attributes.MOVEMENT_SPEED);

			if (speedAttrib != null && !speedAttrib.hasModifier(RING_MODIFIER)) {
				speedAttrib.addOrReplacePermanentModifier(new AttributeModifier(RING_MODIFIER, 0.2D, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
			}
		}
	}

	@Override
	public void onUnequip(ItemStack stack, Entity entity, Container inventory) {
		if (entity instanceof LivingEntity living) {
			boolean hasOtherRing = false;
			for (int i = 0; i < inventory.getContainerSize(); i++) {
				ItemStack invStack = inventory.getItem(i);
				if (!invStack.isEmpty() && invStack.getItem() instanceof RingOfPowerItem && invStack != stack) {
					hasOtherRing = true;
					break;
				}
			}

			if (!hasOtherRing) {
				AttributeInstance speedAttrib = living.getAttribute(Attributes.MOVEMENT_SPEED);

				if (speedAttrib != null) {
					speedAttrib.removeModifier(RING_MODIFIER);
				}
			}
		}
	}

	@Override
	public void onKeybindState(Player player, ItemStack stack, Container inventory, boolean active) {

	}
}

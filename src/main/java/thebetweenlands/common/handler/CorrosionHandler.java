package thebetweenlands.common.handler;

import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import thebetweenlands.api.item.CorrosionHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.datagen.BetweenlandsItemTagProvider;

@EventBusSubscriber(modid = TheBetweenlands.ID, bus = Bus.GAME)
public class CorrosionHandler {

	// Trigger last
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public static void onItemModifiers(ItemAttributeModifierEvent event) {
		ItemStack stack = event.getItemStack();
		ItemAttributeModifiers modifiers = event.build();
		
		if(stack == null || stack.isEmpty()) return;
		
		if(CorrosionHelper.isCorrodible(stack)) {
			float corrosionModifier = CorrosionHelper.getModifier(stack);
			// parity with old mod behaviour
			// Replace non-multiplicitive modifiers
			modifiers.modifiers().forEach((entry) -> {
				// We only want to change attack damage
				if(!entry.attribute().is(Attributes.ATTACK_DAMAGE.getKey())) {
					return;
				}
				
				AttributeModifier originalModifier = entry.modifier();
				if(originalModifier.operation() == AttributeModifier.Operation.ADD_VALUE) {
					AttributeModifier newModifier = new AttributeModifier(originalModifier.id(), originalModifier.amount() * corrosionModifier, originalModifier.operation());
					event.replaceModifier(entry.attribute(), newModifier, entry.slot());
				}
			});
		}
	}
	
	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		if(CorrosionHelper.isCorrodible(stack)) {
			CorrosionHelper.addCorrosionTooltips(stack, event.getToolTip(), event.getFlags(), event.getContext());
		}
	}
	
	
}

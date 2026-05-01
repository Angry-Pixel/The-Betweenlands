package thebetweenlands.common.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.GunkData;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.common.registries.FluidTypeRegistry;

public class PlayerGunkHandler {

	public static final ResourceLocation GUNK_SLOWDOWN_MODIFIER = TheBetweenlands.prefix("gunk_penalty");
	
	public static void init() {
		NeoForge.EVENT_BUS.addListener(GunkData::onPlayerTick);
		NeoForge.EVENT_BUS.addListener(PlayerGunkHandler::modifyPlayerSwimSpeed);
	}
	
	public static void modifyPlayerSwimSpeed(PlayerTickEvent.Pre e) {
		Player player = e.getEntity();
		AttributeMap attributes = player.getAttributes();
		AttributeInstance swimSpeed = attributes.getInstance(NeoForgeMod.SWIM_SPEED);
		
		if(player.isInFluidType(FluidTypeRegistry.SWAMP_WATER.get()) && player.hasData(AttachmentRegistry.GUNK)) {
			GunkData gunkData = player.getData(AttachmentRegistry.GUNK);
			// Goes down to 0 as gunk reaches 100%
			// At 100% the player needs to be taken out of the swimming animation
			swimSpeed.addOrReplacePermanentModifier(new AttributeModifier(GUNK_SLOWDOWN_MODIFIER, -((double)gunkData.getGunk() / (double)GunkData.GUNK_MAX), Operation.ADD_MULTIPLIED_TOTAL));
		} else {
			swimSpeed.removeModifier(GUNK_SLOWDOWN_MODIFIER);
		}
		
	}
	
}

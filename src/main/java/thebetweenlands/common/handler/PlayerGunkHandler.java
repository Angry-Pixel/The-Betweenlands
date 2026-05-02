package thebetweenlands.common.handler;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
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
		
		if(shouldApplyGunkSlowdown(player) && player.hasData(AttachmentRegistry.GUNK)) {
			GunkData gunkData = player.getData(AttachmentRegistry.GUNK);
			// 0% slowdown for 0% to 20% gunk
			// 0%-80% slowdown for 20% to 80% gunk
			// 80% slowdown for 80% to 100% gunk
			final double slowdown = Mth.clampedMap(gunkData.getGunk(), GunkData.GUNK_MAX * 0.2, GunkData.GUNK_MAX * 0.8, 0.0, -0.8);
			
			swimSpeed.addOrReplacePermanentModifier(new AttributeModifier(GUNK_SLOWDOWN_MODIFIER, slowdown, Operation.ADD_MULTIPLIED_TOTAL));
		} else {
			swimSpeed.removeModifier(GUNK_SLOWDOWN_MODIFIER);
		}
	}
	
	public static boolean shouldApplyGunkSlowdown(Player player) {
		return GunkData.isGunkEnabled(player) && GunkData.isGunkActive(player) && player.isSwimming();
	}
	
}

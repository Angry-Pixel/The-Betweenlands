package thebetweenlands.common.handler;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.common.component.SimpleAttachmentHandler;
import thebetweenlands.common.component.SimpleAttachmentType;
import thebetweenlands.common.event.SimulacrumEvents;

public class HandlerEvents {

	public static void init(Dist dist) {
		NeoForge.EVENT_BUS.addListener(SimpleAttachmentHandler::onPlayerJoinWorld);

		//armor
		NeoForge.EVENT_BUS.addListener(ArmorHandler::ignoreDamageWhenStackingAmphibiousUpgrades);
		NeoForge.EVENT_BUS.addListener(ArmorHandler::modifyBreakSpeedWithLurkerArmor);
		NeoForge.EVENT_BUS.addListener(ArmorHandler::reduceFireDamageWithSyrmorite);

		//attacks
		NeoForge.EVENT_BUS.addListener(AttackDamageHandler::handleAttacks);
		NeoForge.EVENT_BUS.addListener(AttackDamageHandler::handleCircleGemDamageBlock);

		//corrosion
		NeoForge.EVENT_BUS.addListener(CorrosionHandler::addCorrosionTooltips);
		NeoForge.EVENT_BUS.addListener(CorrosionHandler::changeItemModifiers);
		NeoForge.EVENT_BUS.addListener(CorrosionHandler::updateCorrosionInPlayerInventory);

		//food sickness
		NeoForge.EVENT_BUS.addListener(FoodSicknessHandler::modifyEatingSpeed);
		NeoForge.EVENT_BUS.addListener(FoodSicknessHandler::modifyEatingStart);
		if (dist.isClient()) NeoForge.EVENT_BUS.addListener(FoodSicknessHandler::tickSicknessClient);

		//decay
		NeoForge.EVENT_BUS.addListener(PlayerDecayHandler::accelerateDecayOnDamage);
		NeoForge.EVENT_BUS.addListener(PlayerDecayHandler::tickDecay);
		NeoForge.EVENT_BUS.addListener(PlayerDecayHandler::syncDecayOnJoin);

		//extended reach
		if(dist.isClient()) {
			NeoForge.EVENT_BUS.addListener(ExtendedReachHandler::onAttackEvent);
			NeoForge.EVENT_BUS.addListener(ExtendedReachHandler::onCrosshairRenderPost);
		}

		//elixirs
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectArrowStrength);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectBreakSpeed);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectJumpingPower);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::changeItemUsageTime);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::changeMaskingTarget);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::tickEffects);

		//shields
		NeoForge.EVENT_BUS.addListener(ShieldHandler::applyShieldLogic);
	}
}

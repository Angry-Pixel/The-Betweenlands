package thebetweenlands.common.handler;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;

public class HandlerEvents {

	public static void init(Dist dist) {
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
		NeoForge.EVENT_BUS.addListener(ExtendedReachHandler::onAttackEvent);

		//elixirs
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectArrowStrength);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectBreakSpeed);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::affectJumpingPower);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::changeItemUsageTime);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::changeMaskingTarget);
		NeoForge.EVENT_BUS.addListener(ElixirCommonHandler::tickEffects);
	}
}

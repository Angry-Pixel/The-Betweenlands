package thebetweenlands.common.registries;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.runechain.rune.RuneCategory;
import thebetweenlands.api.runechain.rune.RuneTier;
import thebetweenlands.common.herblore.rune.ConductRuneFire;
import thebetweenlands.common.herblore.rune.ConductRuneInvoker;
import thebetweenlands.common.herblore.rune.InitiateRuneInteract;
import thebetweenlands.common.herblore.rune.InitiateRuneRightClick;
import thebetweenlands.common.herblore.rune.InitiateRuneWater;
import thebetweenlands.common.herblore.rune.RuneBounce;
import thebetweenlands.common.herblore.rune.RuneBranching;
import thebetweenlands.common.herblore.rune.RuneCollecting;
import thebetweenlands.common.herblore.rune.RuneDestroyBlock;
import thebetweenlands.common.herblore.rune.RuneEnforcing;
import thebetweenlands.common.herblore.rune.RuneMarkNearby;
import thebetweenlands.common.herblore.rune.RuneMerging;
import thebetweenlands.common.herblore.rune.RunePinpoint;
import thebetweenlands.common.herblore.rune.RuneProjectile;
import thebetweenlands.common.herblore.rune.RuneSelectGrass;
import thebetweenlands.common.herblore.rune.RuneSelectRandom;
import thebetweenlands.common.herblore.rune.RuneSelf;
import thebetweenlands.common.item.herblore.rune.ItemRune;
import thebetweenlands.common.item.herblore.rune.properties.BlockGatingRuneItemProperties;
import thebetweenlands.common.item.herblore.rune.properties.DefaultRuneItemProperties;
import thebetweenlands.common.item.herblore.rune.properties.FormationTokenRuneItemProperties;
import thebetweenlands.common.item.herblore.rune.properties.ItemTokenRuneItemProperties;
import thebetweenlands.common.item.herblore.rune.properties.PatternTokenRuneItemProperties;
import thebetweenlands.common.lib.ModInfo;

public class RuneRegistry {
	private RuneRegistry() {}

	public static void preInit() {
		ItemRune.register(RuneCategory.INITIATE, RuneTier.TIER_1, AspectRegistry.BYRGINAZ, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "initiate_water"), new InitiateRuneWater.Blueprint()));
		ItemRune.register(RuneCategory.INITIATE, RuneTier.TIER_1, AspectRegistry.FERGALAZ, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "initiate_right_click"), new InitiateRuneRightClick.Blueprint()));
		ItemRune.register(RuneCategory.INITIATE, RuneTier.TIER_2, AspectRegistry.WODREN, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "initiate_interact"), new InitiateRuneInteract.Blueprint()));
		ItemRune.register(RuneCategory.TOKEN, RuneTier.TIER_1, AspectRegistry.CELAWYNN, new FormationTokenRuneItemProperties(new ResourceLocation(ModInfo.ID, "token_formation")));
		ItemRune.register(RuneCategory.TOKEN, RuneTier.TIER_2, AspectRegistry.CELAWYNN, new PatternTokenRuneItemProperties(new ResourceLocation(ModInfo.ID, "token_pattern")));
		ItemRune.register(RuneCategory.TOKEN, RuneTier.TIER_3, AspectRegistry.CELAWYNN, new ItemTokenRuneItemProperties(new ResourceLocation(ModInfo.ID, "token_item")));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_1, AspectRegistry.FIRNALAZ, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "conduct_fire"), new ConductRuneFire.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_2, AspectRegistry.FIRNALAZ, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "conduct_invoker"), new ConductRuneInvoker.Blueprint()));
		ItemRune.register(RuneCategory.GATING, RuneTier.TIER_1, AspectRegistry.DAYUNIIS, new BlockGatingRuneItemProperties(new ResourceLocation(ModInfo.ID, "gating_block")));

		//Testing
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_1, AspectRegistry.BYARIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "branching"), new RuneBranching.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_2, AspectRegistry.BYARIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "destroy_block"), new RuneDestroyBlock.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_3, AspectRegistry.BYARIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "mark_nearby"), new RuneMarkNearby.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_1, AspectRegistry.ORDANIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "merging"), new RuneMerging.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_2, AspectRegistry.ORDANIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "pinpoint"), new RunePinpoint.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_3, AspectRegistry.ORDANIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "projectile"), new RuneProjectile.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_2, AspectRegistry.FERGALAZ, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "select_grass"), new RuneSelectGrass.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_3, AspectRegistry.FERGALAZ, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "select_random"), new RuneSelectRandom.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_1, AspectRegistry.UDURIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "self"), new RuneSelf.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_2, AspectRegistry.UDURIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "collecting"), new RuneCollecting.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_3, AspectRegistry.UDURIIS, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "enforcing"), new RuneEnforcing.Blueprint()));
		ItemRune.register(RuneCategory.CONDUCT, RuneTier.TIER_1, AspectRegistry.WODREN, new DefaultRuneItemProperties(new ResourceLocation(ModInfo.ID, "bounce"), new RuneBounce.Blueprint()));
	}
}

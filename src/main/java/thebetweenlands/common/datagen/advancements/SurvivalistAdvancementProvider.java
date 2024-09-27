package thebetweenlands.common.datagen.advancements;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.advancement.NoCriteriaTrigger;
import thebetweenlands.common.registries.*;

import java.util.Optional;
import java.util.function.Consumer;

public class SurvivalistAdvancementProvider implements AdvancementGenerator {
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper helper) {
		var root = Advancement.Builder.advancement().display(ItemRegistry.SAP_BALL,
				Component.translatable("advancement.thebetweenlands.survivalist.root"),
				Component.translatable("advancement.thebetweenlands.survivalist.root.desc"),
				TheBetweenlands.prefix("textures/block/swamp_dirt.png"),
				AdvancementType.TASK, false, false, false)
			.addCriterion("has_base_root", CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().checkAdvancementDone(TheBetweenlands.prefix("adventurer/dank_world"), true).build())).build())))))
			.save(consumer, "thebetweenlands:survivalist/root");

		var cure = Advancement.Builder.advancement().parent(root).display(ItemRegistry.SAP_BALL,
				Component.translatable("advancement.thebetweenlands.survivalist.a_cure"),
				Component.translatable("advancement.thebetweenlands.survivalist.a_cure.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("has_sap", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SAP_BALL))
			.save(consumer, "thebetweenlands:survivalist/a_cure");

		Advancement.Builder.advancement().parent(cure).display(ItemRegistry.WEEPING_BLUE_PETAL,
				Component.translatable("advancement.thebetweenlands.survivalist.say_nay_to_decay"),
				Component.translatable("advancement.thebetweenlands.survivalist.say_nay_to_decay.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("has_petal", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.WEEPING_BLUE_PETAL))
			.save(consumer, "thebetweenlands:survivalist/say_nay_to_decay");

		Advancement.Builder.advancement().parent(root).display(ItemRegistry.DAMP_TORCH,
				Component.translatable("advancement.thebetweenlands.survivalist.dimwitted"),
				Component.translatable("advancement.thebetweenlands.survivalist.dimwitted.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("place_torch", AdvancementCriteriaRegistry.DAMP_TORCH_PLACED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:survivalist/dimwitted");

		Advancement.Builder.advancement().parent(root).display(ItemRegistry.SHIMMER_STONE,
				Component.translatable("advancement.thebetweenlands.survivalist.fetch"),
				Component.translatable("advancement.thebetweenlands.survivalist.fetch.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("drop_stone", AdvancementCriteriaRegistry.DROP_SHIMMERSTONE.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:survivalist/fetch");

		Advancement.Builder.advancement().parent(root).display(ItemRegistry.SPIRIT_FRUIT,
				Component.translatable("advancement.thebetweenlands.survivalist.hashtag_blessed"),
				Component.translatable("advancement.thebetweenlands.survivalist.hashtag_blessed.desc"),
				null, AdvancementType.TASK, true, true, true)
			.addCriterion("revived", AdvancementCriteriaRegistry.REVIVED_BLESSED.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:survivalist/hashtag_blessed");

		var laundry = Advancement.Builder.advancement().parent(root).display(new ItemStack(ItemRegistry.WEEDWOOD_BUCKET, 1, DataComponentPatch.builder().set(DataComponentRegistry.STORED_FLUID.get(), SimpleFluidContent.copyOf(new FluidStack(FluidRegistry.SWAMP_WATER_STILL, 1000))).build()),
				Component.translatable("advancement.thebetweenlands.survivalist.hardware_laundry"),
				Component.translatable("advancement.thebetweenlands.survivalist.hardware_laundry.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("tool_purified", AdvancementCriteriaRegistry.PURIFY_TOOL.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:survivalist/hardware_laundry");

		Advancement.Builder.advancement().parent(laundry).display(ItemRegistry.SCABYST,
				Component.translatable("advancement.thebetweenlands.survivalist.onions_have_layers"),
				Component.translatable("advancement.thebetweenlands.survivalist.onions_have_layers.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("tool_coated", AdvancementCriteriaRegistry.COAT_TOOL.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:survivalist/onions_have_layers");

		var pyro = Advancement.Builder.advancement().parent(root).display(ItemRegistry.OCTINE_INGOT,
				Component.translatable("advancement.thebetweenlands.survivalist.pyromaniac"),
				Component.translatable("advancement.thebetweenlands.survivalist.pyromaniac.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("set_fire", AdvancementCriteriaRegistry.OCTINE_INGOT_FIRE.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:survivalist/pyromaniac");

		Advancement.Builder.advancement().parent(pyro).display(BlockRegistry.PEAT,
				Component.translatable("advancement.thebetweenlands.survivalist.quest_for_fire"),
				Component.translatable("advancement.thebetweenlands.survivalist.quest_for_fire.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("set_fire", AdvancementCriteriaRegistry.PEAT_FIRE.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:survivalist/quest_for_fire");

		var rotten = Advancement.Builder.advancement().parent(root).display(ItemRegistry.ROTTEN_FOOD,
				Component.translatable("advancement.thebetweenlands.survivalist.you_are_what_you_eat"),
				Component.translatable("advancement.thebetweenlands.survivalist.you_are_what_you_eat.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("eat_rotten_food", ConsumeItemTrigger.TriggerInstance.usedItem(ItemRegistry.ROTTEN_FOOD))
			.save(consumer, "thebetweenlands:survivalist/you_are_what_you_eat");

		var wing = Advancement.Builder.advancement().parent(rotten).display(ItemRegistry.CHIROMAW_WING,
				Component.translatable("advancement.thebetweenlands.survivalist.hardcore_munchies"),
				Component.translatable("advancement.thebetweenlands.survivalist.hardcore_munchies.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("eat_wing", ConsumeItemTrigger.TriggerInstance.usedItem(ItemRegistry.CHIROMAW_WING))
			.save(consumer, "thebetweenlands:survivalist/hardcore_munchies");

		Advancement.Builder.advancement().parent(wing).display(ItemRegistry.FUMIGANT,
				Component.translatable("advancement.thebetweenlands.survivalist.uh_oh_stinky"),
				Component.translatable("advancement.thebetweenlands.survivalist.uh_oh_stinky.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("use_fumigant", AdvancementCriteriaRegistry.USED_FUMIGANT.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:survivalist/uh_oh_stinky");

		Advancement.Builder.advancement().parent(wing).display(ItemRegistry.OLMLETTE,
				Component.translatable("advancement.thebetweenlands.survivalist.sacrifices_were_made"),
				Component.translatable("advancement.thebetweenlands.survivalist.sacrifices_were_made.desc"),
				null, AdvancementType.TASK, true, true, true)
			.addCriterion("has_olmlette", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.OLMLETTE))
			.save(consumer, "thebetweenlands:survivalist/sacrifices_were_made");
	}
}

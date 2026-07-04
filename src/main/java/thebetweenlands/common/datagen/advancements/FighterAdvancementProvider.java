package thebetweenlands.common.datagen.advancements;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.neoforged.neoforge.common.data.AdvancementProvider.AdvancementGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.advancement.EquipTrigger;
import thebetweenlands.common.advancement.NoCriteriaTrigger;
import thebetweenlands.common.advancement.ShockwaveKillTrigger;
import thebetweenlands.common.advancement.SwatShieldTrigger;
import thebetweenlands.common.component.entity.circlegem.CircleGemHelper;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.datagen.tags.BLEntityTagProvider;
import thebetweenlands.common.registries.*;

import java.util.Optional;
import java.util.function.Consumer;

public class FighterAdvancementProvider implements AdvancementGenerator {
	@Override
	public void generate(HolderLookup.Provider registries, Consumer<AdvancementHolder> consumer, ExistingFileHelper helper) {
		var root = Advancement.Builder.advancement().display(ItemRegistry.BONE_SWORD,
				Component.translatable("advancement.thebetweenlands.fighter.root"),
				Component.translatable("advancement.thebetweenlands.fighter.root.desc"),
				TheBetweenlands.prefix("textures/block/pitstone_bricks.png"),
				AdvancementType.TASK, false, false, false)
			.addCriterion("has_base_root", CriteriaTriggers.TICK.createCriterion(new PlayerTrigger.TriggerInstance(Optional.of(ContextAwarePredicate.create(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(PlayerPredicate.Builder.player().checkAdvancementDone(TheBetweenlands.prefix("adventurer/dank_world"), true).build())).build())))))
			.save(consumer, "thebetweenlands:fighter/root");

		var business = Advancement.Builder.advancement().parent(root).display(ItemRegistry.BONE_SWORD,
				Component.translatable("advancement.thebetweenlands.fighter.serious_business"),
				Component.translatable("advancement.thebetweenlands.fighter.serious_business.desc"),
				null, AdvancementType.TASK, true, false, false)
			.addCriterion("weedwood_sword", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.WEEDWOOD_SWORD))
			.addCriterion("bone_sword", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.BONE_SWORD))
			.addCriterion("octine_sword", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.OCTINE_SWORD))
			.addCriterion("valonite_sword", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.VALONITE_SWORD))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:fighter/serious_business");

		var chopping = Advancement.Builder.advancement().parent(business).display(ItemRegistry.OCTINE_SWORD,
				Component.translatable("advancement.thebetweenlands.fighter.chopping_boy"),
				Component.translatable("advancement.thebetweenlands.fighter.chopping_boy.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("weedwood_sword", KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(), DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemRegistry.WEEDWOOD_SWORD))))))
			.addCriterion("bone_sword", KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(), DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemRegistry.BONE_SWORD))))))
			.addCriterion("octine_sword", KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(), DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemRegistry.OCTINE_SWORD))))))
			.addCriterion("valonite_sword", KilledTrigger.TriggerInstance.playerKilledEntity(Optional.empty(), DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemRegistry.VALONITE_SWORD))))))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:fighter/chopping_boy");

		var fearless = Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.BONE_HELMET,
				Component.translatable("advancement.thebetweenlands.fighter.fearless_fighter"),
				Component.translatable("advancement.thebetweenlands.fighter.fearless_fighter.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("wight", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.WIGHT.get())))
			.addCriterion("hag", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.SWAMP_HAG.get())))
			.addCriterion("tar_beast", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.TAR_BEAST.get())))
			.addCriterion("sludge", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.SLUDGE.get())))
			.addCriterion("peat_mummy", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.PEAT_MUMMY.get())))
			.save(consumer, "thebetweenlands:fighter/fearless_fighter");

		Advancement.Builder.advancement().parent(fearless).display(ItemRegistry.MUMMY_BAIT,
				Component.translatable("advancement.thebetweenlands.fighter.beating_dead_meat"),
				Component.translatable("advancement.thebetweenlands.fighter.beating_dead_meat.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("dreadful_mummy", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.DREADFUL_PEAT_MUMMY.get())))
			.save(consumer, "thebetweenlands:fighter/beating_dead_meat");

		var toothpick = Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.SHOCKWAVE_SWORD_PIECE_2,
				Component.translatable("advancement.thebetweenlands.fighter.glorified_toothpick"),
				Component.translatable("advancement.thebetweenlands.fighter.glorified_toothpick.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("sword", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SHOCKWAVE_SWORD))
			.save(consumer, "thebetweenlands:fighter/glorified_toothpick");

		Advancement.Builder.advancement().parent(toothpick).display(ItemRegistry.SHOCKWAVE_SWORD,
				Component.translatable("advancement.thebetweenlands.fighter.do_the_wave"),
				Component.translatable("advancement.thebetweenlands.fighter.do_the_wave.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("wave1", ShockwaveKillTrigger.TriggerInstance.killAnyWithShockwave())
			.addCriterion("wave2", ShockwaveKillTrigger.TriggerInstance.killAnyWithShockwave())
			.addCriterion("wave3", ShockwaveKillTrigger.TriggerInstance.killAnyWithShockwave())
			.save(consumer, "thebetweenlands:fighter/do_the_wave");

		Advancement.Builder.advancement().parent(toothpick).display(ItemRegistry.AMULET_SLOT,
				Component.translatable("advancement.thebetweenlands.fighter.staring_contest"),
				Component.translatable("advancement.thebetweenlands.fighter.staring_contest.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("primordial_malevolence", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.PRIMORDIAL_MALEVOLENCE.get())))
			.save(consumer, "thebetweenlands:fighter/staring_contest");

		var secret = Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.ANCIENT_GREATSWORD,
				Component.translatable("advancement.thebetweenlands.fighter.secret_of_the_crypt"),
				Component.translatable("advancement.thebetweenlands.fighter.secret_of_the_crypt.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("greatsword", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.ANCIENT_GREATSWORD))
			.save(consumer, "thebetweenlands:fighter/secret_of_the_crypt");

		//TODO not a boss advancement

		Advancement.Builder.advancement().parent(chopping).display(BlockRegistry.SPIRIT_TREE_SAPLING,
				Component.translatable("advancement.thebetweenlands.fighter.illegal_logging"),
				Component.translatable("advancement.thebetweenlands.fighter.illegal_logging.desc"),
				null, AdvancementType.CHALLENGE, true, true, false)
			.addCriterion("spirit_tree", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.LARGE_SPIRIT_TREE_FACE.get())))
			.save(consumer, "thebetweenlands:fighter/illegal_logging");

		Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.CHIROBARB_ERUPTER,
				Component.translatable("advancement.thebetweenlands.fighter.spiky_matter"),
				Component.translatable("advancement.thebetweenlands.fighter.spiky_matter.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("matriarch", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.CHIROMAW_MATRIARCH.get())))
			.save(consumer, "thebetweenlands:fighter/spiky_matter");

		Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.WIGHTS_BANE,
				Component.translatable("advancement.thebetweenlands.fighter.whhyyy"),
				Component.translatable("advancement.thebetweenlands.fighter.whhyyy.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("wight_bane_kill", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(BLEntityTagProvider.WIGHTS_BANE_INSTAKILLS), DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemRegistry.WIGHTS_BANE))))))
			.save(consumer, "thebetweenlands:fighter/whhyyy");

		Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.CRITTER_CRUNCHER,
				Component.translatable("advancement.thebetweenlands.fighter.critical_hit"),
				Component.translatable("advancement.thebetweenlands.fighter.critical_hit.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("critter_cruncher_kill", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(BLEntityTagProvider.CRITTER_CRUNCHER_INSTAKILLS), DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemRegistry.CRITTER_CRUNCHER))))))
			.save(consumer, "thebetweenlands:fighter/critical_hit");

		Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.SLUDGE_SLICER,
				Component.translatable("advancement.thebetweenlands.fighter.sticky_situation"),
				Component.translatable("advancement.thebetweenlands.fighter.sticky_situation.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("sludge_slicer_kill", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(BLEntityTagProvider.SLUDGE_SLICER_INSTAKILLS), DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemRegistry.SLUDGE_SLICER))))))
			.save(consumer, "thebetweenlands:fighter/sticky_situation");

		Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.HAG_HACKER,
				Component.translatable("advancement.thebetweenlands.fighter.hag_hater"),
				Component.translatable("advancement.thebetweenlands.fighter.hag_hater.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("hag_hacker_kill", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(BLEntityTagProvider.HAG_HACKER_INSTAKILLS), DamageSourcePredicate.Builder.damageType().source(EntityPredicate.Builder.entity().equipment(EntityEquipmentPredicate.Builder.equipment().mainhand(ItemPredicate.Builder.item().of(ItemRegistry.HAG_HACKER))))))
			.save(consumer, "thebetweenlands:fighter/hag_hater");

		Advancement.Builder.advancement().parent(chopping).display(ItemRegistry.RUNE_DOOR_KEY,
				Component.translatable("advancement.thebetweenlands.fighter.banshees_in_the_barriers"),
				Component.translatable("advancement.thebetweenlands.fighter.banshees_in_the_barriers.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("kill_barrishee", KilledTrigger.TriggerInstance.playerKilledEntity(EntityPredicate.Builder.entity().of(EntityRegistry.BARRISHEE.get())))
			.save(consumer, "thebetweenlands:fighter/banshees_in_the_barriers");

		var wall = Advancement.Builder.advancement().parent(business).display(ItemRegistry.BONE_SHIELD,
				Component.translatable("advancement.thebetweenlands.fighter.shield_wall"),
				Component.translatable("advancement.thebetweenlands.fighter.shield_wall.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("weedwood_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.WEEDWOOD_SHIELD))
			.addCriterion("living_weedwood_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.LIVING_WEEDWOOD_SHIELD))
			.addCriterion("lurker_skin_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.LURKER_SKIN_SHIELD))
			.addCriterion("bone_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.BONE_SHIELD))
			.addCriterion("syrmorite_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.SYRMORITE_SHIELD))
			.addCriterion("octine_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.OCTINE_SHIELD))
			.addCriterion("valonite_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.VALONITE_SHIELD))
			.addCriterion("orange_dentrothyst_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.ORANGE_DENTROTHYST_SHIELD))
			.addCriterion("green_dentrothyst_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.GREEN_DENTROTHYST_SHIELD))
			.addCriterion("polished_orange_dentrothyst_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.POLISHED_ORANGE_DENTROTHYST_SHIELD))
			.addCriterion("polished_green_dentrothyst_shield", InventoryChangeTrigger.TriggerInstance.hasItems(ItemRegistry.POLISHED_GREEN_DENTROTHYST_SHIELD))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:fighter/shield_wall");

		Advancement.Builder.advancement().parent(wall).display(ItemRegistry.SYRMORITE_SHIELD,
				Component.translatable("advancement.thebetweenlands.fighter.crowd_control"),
				Component.translatable("advancement.thebetweenlands.fighter.crowd_control.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("knockback_hag_1", SwatShieldTrigger.TriggerInstance.knockbackWithShield(EntityRegistry.SWAMP_HAG.get()))
			.addCriterion("knockback_hag_2", SwatShieldTrigger.TriggerInstance.knockbackWithShield(EntityRegistry.SWAMP_HAG.get()))
			.addCriterion("knockback_hag_3", SwatShieldTrigger.TriggerInstance.knockbackWithShield(EntityRegistry.SWAMP_HAG.get()))
			.save(consumer, "thebetweenlands:fighter/crowd_control");

		var fashion = Advancement.Builder.advancement().parent(business).display(this.applyGemTo(ItemRegistry.VALONITE_HELMET, CircleGemType.AQUA),
				Component.translatable("advancement.thebetweenlands.fighter.fashionable_warrior"),
				Component.translatable("advancement.thebetweenlands.fighter.fashionable_warrior.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("add_gem", AdvancementCriteriaRegistry.MIDDLE_GEM_UPGRADE.get().createCriterion(new NoCriteriaTrigger.TriggerInstance(Optional.empty())))
			.save(consumer, "thebetweenlands:fighter/fashionable_warrior");

		Advancement.Builder.advancement().parent(fashion).display(ItemRegistry.CRIMSON_MIDDLE_GEM,
				Component.translatable("advancement.thebetweenlands.fighter.cling_that_bling"),
				Component.translatable("advancement.thebetweenlands.fighter.cling_that_bling.desc"),
				null, AdvancementType.TASK, true, true, false)
			.addCriterion("wear_crimson_amulet", EquipTrigger.TriggerInstance.equipItem(ItemRegistry.CRIMSON_AMULET))
			.addCriterion("wear_green_amulet", EquipTrigger.TriggerInstance.equipItem(ItemRegistry.GREEN_AMULET))
			.addCriterion("wear_aqua_amulet", EquipTrigger.TriggerInstance.equipItem(ItemRegistry.AQUA_AMULET))
			.requirements(AdvancementRequirements.Strategy.OR)
			.save(consumer, "thebetweenlands:fighter/cling_that_bling");

		Advancement.Builder.advancement().parent(business).display(ItemRegistry.BASILISK_ARROW,
				Component.translatable("advancement.thebetweenlands.fighter.arrow_arsenal"),
				Component.translatable("advancement.thebetweenlands.fighter.arrow_arsenal.desc"),
				null, AdvancementType.GOAL, true, true, false)
			.addCriterion("default", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().direct(EntityPredicate.Builder.entity().of(EntityRegistry.ANGLER_TOOTH_ARROW.get())))))
			.addCriterion("poison", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().direct(EntityPredicate.Builder.entity().of(EntityRegistry.POISON_ANGLER_TOOTH_ARROW.get())))))
			.addCriterion("octine", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().direct(EntityPredicate.Builder.entity().of(EntityRegistry.OCTINE_ARROW.get())))))
			.addCriterion("shock", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().direct(EntityPredicate.Builder.entity().of(EntityRegistry.SHOCK_ARROW.get())))))
			.addCriterion("basilisk", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().direct(EntityPredicate.Builder.entity().of(EntityRegistry.BASILISK_ARROW.get())))))
			.addCriterion("worm", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().direct(EntityPredicate.Builder.entity().of(EntityRegistry.SLUDGE_WORM_ARROW.get())))))
			.addCriterion("barb", PlayerHurtEntityTrigger.TriggerInstance.playerHurtEntityWithDamage(DamagePredicate.Builder.damageInstance().type(DamageSourcePredicate.Builder.damageType().direct(EntityPredicate.Builder.entity().of(EntityRegistry.CHIROMAW_BARB.get())))))
			.save(consumer, "thebetweenlands:fighter/arrow_arsenal");
	}

	private ItemStack applyGemTo(Holder<Item> armor, CircleGemType gem) {
		ItemStack stack = new ItemStack(armor);
		CircleGemHelper.setGem(stack, gem);
		return stack;
	}
}

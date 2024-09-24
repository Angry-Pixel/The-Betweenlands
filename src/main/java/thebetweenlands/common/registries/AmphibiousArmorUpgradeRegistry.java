package thebetweenlands.common.registries;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.entity.EquipmentSlot;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.api.BLRegistries;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.circlegem.CircleGemType;
import thebetweenlands.common.item.armor.amphibious.upgrades.*;

public class AmphibiousArmorUpgradeRegistry {

	public static final DeferredRegister<AmphibiousArmorUpgrade> UPGRADES = DeferredRegister.create(BLRegistries.Keys.AMPHIBIOUS_ARMOR_UPGRADES, TheBetweenlands.ID);

	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> NONE = UPGRADES.register("none", () -> new SimpleAmphibiousArmorUpgrade(0, AmphibiousArmorUpgrade.DamageEvent.NONE, stack -> false));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> VISIBILITY = UPGRADES.register("visibility", () -> new VisibilityArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.ANADIA_EYE), EquipmentSlot.HEAD));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> THORNS = UPGRADES.register("thorns", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.URCHIN_SPIKE), EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> DECAY_DECREASE = UPGRADES.register("decay_decrease", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.SAP_SPIT), EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> MINING_SPEED = UPGRADES.register("mining_speed", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.SNOT), EquipmentSlot.CHEST));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> BREATHING = UPGRADES.register("breathing", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.ANADIA_GILLS), EquipmentSlot.HEAD, EquipmentSlot.CHEST));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> MOVEMENT_SPEED = UPGRADES.register("movement_speed", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.ANADIA_FINS), AdditiveAttributeUpgrade.MOVEMENT_SPEED, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> TOUGHNESS = UPGRADES.register("toughness", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.ANADIA_BONES), AdditiveAttributeUpgrade.TOUGHNESS, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> KNOCKBACK_RESISTANCE = UPGRADES.register("knockback_resistance", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.LURKER_SKIN), AdditiveAttributeUpgrade.KNOCKBACK_RESISTANCE, EquipmentSlot.CHEST, EquipmentSlot.LEGS));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> BUOYANCY = UPGRADES.register("buoyancy", () -> new BuoyancyArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.ANADIA_SWIM_BLADDER), EquipmentSlot.CHEST, EquipmentSlot.LEGS));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> FISH_SIGHT = UPGRADES.register("fish_sight", () -> new FishSightArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(BlockRegistry.GLOWING_GOOP.asItem()), EquipmentSlot.HEAD));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> ARMOR = UPGRADES.register("armor", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.ANADIA_SCALES), AdditiveAttributeUpgrade.ARMOR, EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));

	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> ASCENT_BOOST = UPGRADES.register("ascent_boost", () -> new AscentArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_USE, stack -> stack.is(ItemRegistry.ASCENT_UPGRADE), EquipmentSlot.LEGS));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> FISH_VORTEX = UPGRADES.register("fish_vortex", () -> new FishVortexArmorUpgrade(256, AmphibiousArmorUpgrade.DamageEvent.ON_USE, stack -> stack.is(ItemRegistry.FISH_VORTEX_UPGRADE), EquipmentSlot.CHEST));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> ELECTRIC = UPGRADES.register("electric", () -> new ElectricArmorUpgrade(256, AmphibiousArmorUpgrade.DamageEvent.ON_USE, stack -> stack.is(ItemRegistry.ELECTRIC_UPGRADE), EquipmentSlot.CHEST));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> URCHIN_SPIKE = UPGRADES.register("urchin_spike", () -> new UrchinSpikeArmorUpgrade(256, AmphibiousArmorUpgrade.DamageEvent.ON_USE, stack -> stack.is(ItemRegistry.URCHIN_SPIKE_UPGRADE), EquipmentSlot.CHEST));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> GLIDE = UPGRADES.register("glide", () -> new GlideArmorUpgrade(256, AmphibiousArmorUpgrade.DamageEvent.ON_USE, stack -> stack.is(ItemRegistry.GLIDE_UPGRADE), EquipmentSlot.CHEST));

	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> AQUA_GEM = UPGRADES.register("aqua_gem", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.AQUA_MIDDLE_GEM), null, CircleGemType.AQUA.getAmphibiousArmorOnChangedHandler(), ImmutableSet.of(AmphibiousArmorUpgradeRegistry.GREEN_GEM, AmphibiousArmorUpgradeRegistry.CRIMSON_GEM), EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> GREEN_GEM = UPGRADES.register("green_gem", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.GREEN_MIDDLE_GEM), null, CircleGemType.GREEN.getAmphibiousArmorOnChangedHandler(), ImmutableSet.of(AmphibiousArmorUpgradeRegistry.AQUA_GEM, AmphibiousArmorUpgradeRegistry.CRIMSON_GEM), EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
	public static final DeferredHolder<AmphibiousArmorUpgrade, SimpleAmphibiousArmorUpgrade> CRIMSON_GEM = UPGRADES.register("crimson_gem", () -> new SimpleAmphibiousArmorUpgrade(64, AmphibiousArmorUpgrade.DamageEvent.ON_DAMAGE, stack -> stack.is(ItemRegistry.CRIMSON_MIDDLE_GEM), null, CircleGemType.CRIMSON.getAmphibiousArmorOnChangedHandler(), ImmutableSet.of(AmphibiousArmorUpgradeRegistry.GREEN_GEM, AmphibiousArmorUpgradeRegistry.AQUA_GEM), EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET));
}

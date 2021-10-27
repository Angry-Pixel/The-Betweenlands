package thebetweenlands.common.item.armor.amphibious;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import net.minecraft.item.Item;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.api.item.IAmphibiousArmorAttributeUpgrade;
import thebetweenlands.api.item.IAmphibiousArmorUpgrade;
import thebetweenlands.common.capability.circlegem.CircleGemType;
import thebetweenlands.common.item.misc.ItemGlowingGoop;
import thebetweenlands.common.item.misc.ItemMisc;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.ItemRegistry;

public enum AmphibiousArmorUpgrades implements IAmphibiousArmorUpgrade {
	// gives night vision if you have at least 1 eye
	VISIBILITY(new ResourceLocation(ModInfo.ID, "visibility"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.ANADIA_EYE::isItemOf, EntityEquipmentSlot.HEAD),
	// each piece of armor that has a thorns upgrades increases thorns damage
	THORNS(new ResourceLocation(ModInfo.ID, "thorns"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.URCHIN_SPIKE::isItemOf, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	// each piece of armor that has decay decrease, reduces the decay rate by 25%
	DECAY_DECREASE(new ResourceLocation(ModInfo.ID, "decay_decrease"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.ANADIA_SCALES::isItemOf, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	// having 1 of the upgrade item gives full mining speed underwater, similar to how owning all 4
	MINING_SPEED(new ResourceLocation(ModInfo.ID, "mining_speed"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.SNOT::isItemOf, EntityEquipmentSlot.CHEST),
	// if wearing full armor, 2 or more gills give full water breathing. otherwise it just improves water breathing
	BREATHING(new ResourceLocation(ModInfo.ID, "breathing"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.ANADIA_GILLS::isItemOf, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST),
	// each piece gives 0.125 speed increase up to 4 times, speed is halfed on ground. 4 pieces lets player walk on mud
	MOVEMENT_SPEED(new ResourceLocation(ModInfo.ID, "movement_speed"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.ANADIA_FINS::isItemOf, AdditiveAttributeUpgrade.MOVEMENT_SPEED, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	TOUGHNESS(new ResourceLocation(ModInfo.ID, "toughness"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.ANADIA_BONES::isItemOf, AdditiveAttributeUpgrade.TOUGHNESS, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	KNOCKBACK_RESISTANCE(new ResourceLocation(ModInfo.ID, "knockback_resistance"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.LURKER_SKIN::isItemOf, AdditiveAttributeUpgrade.KNOCKBACK_RESISTANCE, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS),
	BUOYANCY(new ResourceLocation(ModInfo.ID, "buoyancy"), 64, DamageEvent.ON_DAMAGE, ItemMisc.EnumItemMisc.ANADIA_SWIM_BLADDER::isItemOf, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS),
	FISH_SIGHT(new ResourceLocation(ModInfo.ID, "fish_sight"), 64, DamageEvent.ON_USE, s -> s.getItem() == Item.getItemFromBlock(BlockRegistry.GLOWING_GOOP), EntityEquipmentSlot.HEAD),

	ASCENT_BOOST(new ResourceLocation(ModInfo.ID, "ascent_boost"), 64, DamageEvent.ON_USE,  s -> s.getItem() == ItemRegistry.AA_UPGRADE_LEAP, EntityEquipmentSlot.LEGS),
	FISH_VORTEX(new ResourceLocation(ModInfo.ID, "fish_vortex"), 256, DamageEvent.ON_USE, s -> s.getItem() == ItemRegistry.AA_UPGRADE_VORTEX, EntityEquipmentSlot.CHEST),
	ELECTRIC(new ResourceLocation(ModInfo.ID, "electric"), 256, DamageEvent.ON_USE, s -> s.getItem() == ItemRegistry.AA_UPGRADE_ELECTRIC, EntityEquipmentSlot.CHEST),
	URCHIN(new ResourceLocation(ModInfo.ID, "urchin"), 256, DamageEvent.ON_USE, s -> s.getItem() == ItemRegistry.AA_UPGRADE_URCHIN, EntityEquipmentSlot.CHEST),
	GLIDE(new ResourceLocation(ModInfo.ID, "glide"), 256, DamageEvent.ON_USE, s -> s.getItem() == ItemRegistry.AA_UPGRADE_GLIDE, EntityEquipmentSlot.CHEST),

	AQUA_GEM(new ResourceLocation(ModInfo.ID, "aqua_gem"), 64, DamageEvent.ON_DAMAGE, s -> s.getItem() == ItemRegistry.AQUA_MIDDLE_GEM, null, CircleGemType.AQUA.getAmphibiousArmorOnChangedHandler(), ImmutableSet.of(new ResourceLocation(ModInfo.ID, "green_gem"), new ResourceLocation(ModInfo.ID, "crimson_gem")), EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	GREEN_GEM(new ResourceLocation(ModInfo.ID, "green_gem"), 64, DamageEvent.ON_DAMAGE, s -> s.getItem() == ItemRegistry.GREEN_MIDDLE_GEM, null, CircleGemType.GREEN.getAmphibiousArmorOnChangedHandler(), ImmutableSet.of(new ResourceLocation(ModInfo.ID, "aqua_gem"), new ResourceLocation(ModInfo.ID, "crimson_gem")), EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	CRIMSON_GEM(new ResourceLocation(ModInfo.ID, "crimson_gem"), 64, DamageEvent.ON_DAMAGE, s -> s.getItem() == ItemRegistry.CRIMSON_MIDDLE_GEM, null, CircleGemType.CRIMSON.getAmphibiousArmorOnChangedHandler(), ImmutableSet.of(new ResourceLocation(ModInfo.ID, "aqua_gem"), new ResourceLocation(ModInfo.ID, "green_gem")), EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET);
	
	private static final Map<ResourceLocation, IAmphibiousArmorUpgrade> ID_TO_UPGRADE = new HashMap<>();
	private static final Multimap<EntityEquipmentSlot, IAmphibiousArmorUpgrade> TYPE_TO_UPGRADES = MultimapBuilder.enumKeys(EntityEquipmentSlot.class).arrayListValues().build();

	public static boolean register(IAmphibiousArmorUpgrade upgrade) {
		if(ID_TO_UPGRADE.put(upgrade.getId(), upgrade) == null) {
			for(EntityEquipmentSlot armorType : upgrade.getArmorTypes()) {
				TYPE_TO_UPGRADES.put(armorType, upgrade);
			}
			return true;
		}
		return false;
	}

	public static Collection<IAmphibiousArmorUpgrade> getUpgrades(EntityEquipmentSlot armorType) {
		return TYPE_TO_UPGRADES.get(armorType);
	}

	@Nullable
	public static IAmphibiousArmorUpgrade getUpgrade(EntityEquipmentSlot armorType, ItemStack stack) {
		for(IAmphibiousArmorUpgrade upgrade : getUpgrades(armorType)) {
			if(upgrade.matches(armorType, stack)) {
				return upgrade;
			}
		}
		return null;
	}

	@Nullable
	public static IAmphibiousArmorUpgrade getUpgrade(ResourceLocation id) {
		return ID_TO_UPGRADE.get(id);
	}

	static {
		for(AmphibiousArmorUpgrades upgrade : values()) {
			register(upgrade);
		}
	}

	private final ResourceLocation id;
	private final int maxDamage;
	private final DamageEvent damageEvent;
	private final Predicate<ItemStack> matcher;
	private final IAmphibiousArmorAttributeUpgrade attributeUpgrade;
	private final Set<EntityEquipmentSlot> armorTypes;
	private final Consumer<ItemStack> onChanged;
	private final Set<ResourceLocation> blacklist;

	private AmphibiousArmorUpgrades(ResourceLocation id, int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EntityEquipmentSlot... armorTypes) {
		this(id, maxDamage, damageEvent, matcher, null, armorTypes);
	}

	private AmphibiousArmorUpgrades(ResourceLocation id, int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, @Nullable IAmphibiousArmorAttributeUpgrade attributeUpgrade, EntityEquipmentSlot... armorTypes) {
		this(id, maxDamage, damageEvent, matcher, attributeUpgrade, null, ImmutableSet.of(), armorTypes);
	}

	private AmphibiousArmorUpgrades(ResourceLocation id, int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, @Nullable IAmphibiousArmorAttributeUpgrade attributeUpgrade, Consumer<ItemStack> onChanged, Set<ResourceLocation> blacklist, EntityEquipmentSlot... armorTypes) {
		this.id = id;
		this.maxDamage = maxDamage;
		this.damageEvent = damageEvent;
		this.matcher = matcher;
		this.attributeUpgrade = attributeUpgrade;
		this.onChanged = onChanged;
		this.blacklist = blacklist;
		this.armorTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(armorTypes)));
	}

	@Override
	public boolean matches(EntityEquipmentSlot armorType, ItemStack stack) {
		return !stack.isEmpty() && this.armorTypes.contains(armorType) && this.matcher.test(stack);
	}

	@Override
	public Set<EntityEquipmentSlot> getArmorTypes() {
		return this.armorTypes;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}

	@Override
	public void applyAttributeModifiers(EntityEquipmentSlot armorType, ItemStack stack, int count, Map<IAmphibiousArmorUpgrade, Map<EntityEquipmentSlot, Integer>> counts, Multimap<String, AttributeModifier> modifiers) {
		if(this.attributeUpgrade != null) {
			this.attributeUpgrade.applyAttributeModifiers(armorType, stack, count, counts, modifiers);
		}
	}

	@Override
	public void onChanged(EntityEquipmentSlot armorType, ItemStack armor, ItemStack stack) {
		if(this.onChanged != null) {
			this.onChanged.accept(armor);
		}
	}
	
	@Override
	public boolean isBlacklisted(IAmphibiousArmorUpgrade other) {
		return this.blacklist.contains(other.getId());
	}

	@Override
	public int getMaxDamage() {
		return this.maxDamage;
	}

	@Override
	public boolean isApplicableDamageEvent(DamageEvent event) {
		return this.damageEvent != DamageEvent.NONE && (this.damageEvent == DamageEvent.ALL || event == this.damageEvent);
	}

	@Override
	public boolean canBreak() {
		return true;
	}
}

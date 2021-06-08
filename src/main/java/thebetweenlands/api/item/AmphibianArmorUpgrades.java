package thebetweenlands.api.item;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.item.misc.ItemMisc.EnumItemMisc;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public enum AmphibianArmorUpgrades implements IAmphibianArmorUpgrade {
	VISIBILITY(new ResourceLocation(ModInfo.ID, "visibility"), EnumItemMisc.ANADIA_EYE::isItemOf, EntityEquipmentSlot.HEAD),
	BREATHING(new ResourceLocation(ModInfo.ID, "breathing"), EnumItemMisc.ANADIA_GILLS::isItemOf, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST),
	TOUGHNESS(new ResourceLocation(ModInfo.ID, "toughness"), EnumItemMisc.SLIMY_BONE::isItemOf, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	THORNS(new ResourceLocation(ModInfo.ID, "thorns"), EnumItemMisc.URCHIN_SPIKE::isItemOf, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	DECAY_DECREASE(new ResourceLocation(ModInfo.ID, "decay_decrease"), EnumItemMisc.ANADIA_SCALES::isItemOf, EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	MINING_SPEED(new ResourceLocation(ModInfo.ID, "mining_speed"), EnumItemMisc.SNOT::isItemOf, EntityEquipmentSlot.CHEST),
	MOVEMENT_SPEED(new ResourceLocation(ModInfo.ID, "movement_speed"), EnumItemMisc.ANADIA_FINS::isItemOf, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET),
	BUOYANCY(new ResourceLocation(ModInfo.ID, "buoyancy"), EnumItemMisc.ANADIA_SWIM_BLADDER::isItemOf, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS),
	KNOCKBACK_RESISTANCE(new ResourceLocation(ModInfo.ID, "knockback_resistance"), EnumItemMisc.LURKER_SKIN::isItemOf, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS),
	MUD_WALKING(new ResourceLocation(ModInfo.ID, "mud_walking"), s -> s.getItem() == ItemRegistry.RUBBER_BOOTS, EntityEquipmentSlot.FEET);

	private static final Map<ResourceLocation, IAmphibianArmorUpgrade> ID_TO_UPGRADE = new HashMap<>();
	private static final Multimap<EntityEquipmentSlot, IAmphibianArmorUpgrade> TYPE_TO_UPGRADES = MultimapBuilder.enumKeys(EntityEquipmentSlot.class).arrayListValues().build();

	public static boolean register(IAmphibianArmorUpgrade upgrade) {
		if(ID_TO_UPGRADE.put(upgrade.getId(), upgrade) == null) {
			for(EntityEquipmentSlot armorType : upgrade.getArmorTypes()) {
				TYPE_TO_UPGRADES.put(armorType, upgrade);
			}
			return true;
		}
		return false;
	}

	public static Collection<IAmphibianArmorUpgrade> getUpgrades(EntityEquipmentSlot armorType) {
		return TYPE_TO_UPGRADES.get(armorType);
	}

	@Nullable
	public static IAmphibianArmorUpgrade getUpgrade(EntityEquipmentSlot armorType, ItemStack stack) {
		for(IAmphibianArmorUpgrade upgrade : getUpgrades(armorType)) {
			if(upgrade.matches(stack)) {
				return upgrade;
			}
		}
		return null;
	}

	@Nullable
	public static IAmphibianArmorUpgrade getUpgrade(ResourceLocation id) {
		return ID_TO_UPGRADE.get(id);
	}

	static {
		for(AmphibianArmorUpgrades upgrade : values()) {
			register(upgrade);
		}
	}

	private final ResourceLocation id;
	private final Predicate<ItemStack> matcher;
	private final Set<EntityEquipmentSlot> armorTypes;

	private AmphibianArmorUpgrades(ResourceLocation id, Predicate<ItemStack> matcher, EntityEquipmentSlot... armorTypes) {
		this.id = id;
		this.matcher = matcher;
		this.armorTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(armorTypes)));
	}

	@Override
	public boolean matches(ItemStack stack) {
		return !stack.isEmpty() && this.matcher.test(stack);
	}

	@Override
	public Set<EntityEquipmentSlot> getArmorTypes() {
		return this.armorTypes;
	}

	@Override
	public ResourceLocation getId() {
		return this.id;
	}
}

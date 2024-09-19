package thebetweenlands.common.items.amphibious.upgrades;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import thebetweenlands.api.item.amphibious.AmphibiousArmorAttributeUpgrade;
import thebetweenlands.api.item.amphibious.AmphibiousArmorUpgrade;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class SimpleAmphibiousArmorUpgrade implements AmphibiousArmorUpgrade {

	private final int maxDamage;
	private final DamageEvent damageEvent;
	private final Predicate<ItemStack> matcher;
	private final AmphibiousArmorAttributeUpgrade attributeUpgrade;
	private final Set<EquipmentSlot> armorTypes;
	private final Consumer<ItemStack> onChanged;
	private final Set<Holder<AmphibiousArmorUpgrade>> blacklist;

	public SimpleAmphibiousArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, EquipmentSlot... armorTypes) {
		this(maxDamage, damageEvent, matcher, null, armorTypes);
	}

	public SimpleAmphibiousArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, @Nullable AmphibiousArmorAttributeUpgrade attributeUpgrade, EquipmentSlot... armorTypes) {
		this(maxDamage, damageEvent, matcher, attributeUpgrade, null, ImmutableSet.of(), armorTypes);
	}

	public SimpleAmphibiousArmorUpgrade(int maxDamage, DamageEvent damageEvent, Predicate<ItemStack> matcher, @Nullable AmphibiousArmorAttributeUpgrade attributeUpgrade, Consumer<ItemStack> onChanged, Set<Holder<AmphibiousArmorUpgrade>> blacklist, EquipmentSlot... armorTypes) {
		this.maxDamage = maxDamage;
		this.damageEvent = damageEvent;
		this.matcher = matcher;
		this.attributeUpgrade = attributeUpgrade;
		this.onChanged = onChanged;
		this.blacklist = blacklist;
		this.armorTypes = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(armorTypes)));
	}

	@Override
	public boolean matches(EquipmentSlot armorType, ItemStack stack) {
		return !stack.isEmpty() && this.armorTypes.contains(armorType) && this.matcher.test(stack);
	}

	@Override
	public Set<EquipmentSlot> getArmorTypes() {
		return this.armorTypes;
	}

	@Override
	public void applyAttributeModifiers(ArmorItem.Type armorType, ItemStack stack, int count, List<ItemAttributeModifiers.Entry> modifiers) {
		if(this.attributeUpgrade != null) {
			this.attributeUpgrade.applyAttributeModifiers(armorType, stack, count, modifiers);
		}
	}

	@Override
	public void onChanged(EquipmentSlot armorType, ItemStack armor, ItemStack stack) {
		if(this.onChanged != null) {
			this.onChanged.accept(armor);
		}
	}

	@Override
	public boolean isBlacklisted(Holder<AmphibiousArmorUpgrade> other) {
		return this.blacklist.contains(other);
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

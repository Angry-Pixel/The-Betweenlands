package thebetweenlands.common.registries;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import thebetweenlands.common.TheBetweenlands;

import java.util.EnumMap;
import java.util.List;

public class ArmorMaterialRegistry {

	public static final DeferredRegister<ArmorMaterial> MATERIALS = DeferredRegister.create(Registries.ARMOR_MATERIAL, TheBetweenlands.ID);

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> LURKER_SKIN = MATERIALS.register("lurker_skin", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 1);
		map.put(ArmorItem.Type.LEGGINGS, 2);
		map.put(ArmorItem.Type.CHESTPLATE, 3);
		map.put(ArmorItem.Type.HELMET, 1);
		map.put(ArmorItem.Type.BODY, 3);
	}), 0, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ItemRegistry.LURKER_SKIN), List.of(new ArmorMaterial.Layer(TheBetweenlands.prefix("lurker_skin"))), 0.0F, 0.0F));

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> BONE = MATERIALS.register("slimy_bone", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 1);
		map.put(ArmorItem.Type.LEGGINGS, 3);
		map.put(ArmorItem.Type.CHESTPLATE, 5);
		map.put(ArmorItem.Type.HELMET, 2);
		map.put(ArmorItem.Type.BODY, 5);
	}), 0, SoundEvents.ARMOR_EQUIP_CHAIN, () -> Ingredient.of(ItemRegistry.SLIMY_BONE), List.of(new ArmorMaterial.Layer(TheBetweenlands.prefix("bone"))), 0.0F, 0.0F));

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> SYRMORITE = MATERIALS.register("syrmorite", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 2);
		map.put(ArmorItem.Type.LEGGINGS, 5);
		map.put(ArmorItem.Type.CHESTPLATE, 6);
		map.put(ArmorItem.Type.HELMET, 2);
		map.put(ArmorItem.Type.BODY, 6);
	}), 0, SoundEvents.ARMOR_EQUIP_IRON, () -> Ingredient.of(ItemRegistry.SYRMORITE_INGOT), List.of(new ArmorMaterial.Layer(TheBetweenlands.prefix("syrmorite"))), 0.0F, 0.0F));

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> VALONITE = MATERIALS.register("valonite", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 8);
	}), 0, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(ItemRegistry.VALONITE_SHARD), List.of(new ArmorMaterial.Layer(TheBetweenlands.prefix("valonite"))), 2.0F, 0.0F));

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBBER = MATERIALS.register("rubber", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 1);
		map.put(ArmorItem.Type.LEGGINGS, 0);
		map.put(ArmorItem.Type.CHESTPLATE, 0);
		map.put(ArmorItem.Type.HELMET, 0);
		map.put(ArmorItem.Type.BODY, 0);
	}), 10, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(ItemRegistry.RUBBER_BALL), List.of(new ArmorMaterial.Layer(TheBetweenlands.prefix("rubber"))), 0.0F, 0.0F));

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> ANCIENT = MATERIALS.register("ancient", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 3);
		map.put(ArmorItem.Type.LEGGINGS, 6);
		map.put(ArmorItem.Type.CHESTPLATE, 8);
		map.put(ArmorItem.Type.HELMET, 3);
		map.put(ArmorItem.Type.BODY, 8);
	}), 35, SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(ItemRegistry.ANCIENT_REMNANT), List.of(new ArmorMaterial.Layer(TheBetweenlands.prefix("ancient"))), 3.0F, 0.0F));

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> AMPHIBIOUS = MATERIALS.register("amphibious", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 1);
		map.put(ArmorItem.Type.LEGGINGS, 2);
		map.put(ArmorItem.Type.CHESTPLATE, 3);
		map.put(ArmorItem.Type.HELMET, 1);
		map.put(ArmorItem.Type.BODY, 3);
	}), 12, SoundEvents.ARMOR_EQUIP_LEATHER, () -> Ingredient.of(ItemRegistry.LURKER_SKIN), List.of(new ArmorMaterial.Layer(TheBetweenlands.prefix("amphibious"))), 0.0F, 0.0F));
}

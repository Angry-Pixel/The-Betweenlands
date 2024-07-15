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

	public static final DeferredHolder<ArmorMaterial, ArmorMaterial> RUBBER = MATERIALS.register("rubber", () -> new ArmorMaterial(Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
		map.put(ArmorItem.Type.BOOTS, 1);
		map.put(ArmorItem.Type.LEGGINGS, 0);
		map.put(ArmorItem.Type.CHESTPLATE, 0);
		map.put(ArmorItem.Type.HELMET, 0);
		map.put(ArmorItem.Type.BODY, 0);
	}), 10, SoundEvents.ARMOR_EQUIP_GENERIC, () -> Ingredient.of(), List.of(new ArmorMaterial.Layer(TheBetweenlands.prefix("rubber"))), 0.0F, 0.0F));
}

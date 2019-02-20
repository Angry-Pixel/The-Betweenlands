package thebetweenlands.common.item;

import java.util.function.Supplier;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyLoadBase;

//TODO 1.13 I assume ItemTier will be extensible in the future so this is temporary for now
public enum BLItemTier implements IItemTier {
	WEEDWOOD(0, 80, 2.0F, 0.0F, 0, () -> Ingredient.EMPTY),
	BONE(1, 320, 4.0F, 1.0F, 0, () -> Ingredient.EMPTY),
	LURKER_SKIN(1, 600, 5.0F, 1.0F, 0, () -> Ingredient.EMPTY),
	DENTROTHYST(1, 600, 7.0F, 1.0F, 0, () -> Ingredient.EMPTY),
	OCTINE(2, 900, 6.0F, 2.0F, 0, () -> Ingredient.EMPTY),
	SYRMORITE(2, 900, 6.0F, 2.0F, 0, () -> Ingredient.EMPTY),
	VALONITE(3, 2500, 8.0F, 3.0F, 0, () -> Ingredient.EMPTY),
	LOOT(3, 7500, 2.0F, 0.5F, 0, () -> Ingredient.EMPTY),
	LEGEND(6, 10000, 16.0F, 6.0F, 0, () -> Ingredient.EMPTY);

	public static int getMinRepairFuelCost(IItemTier tier) {
		if(tier == WEEDWOOD) {
			return 2;
		} else if(tier == BONE) {
			return 3;
		} else if(tier == LURKER_SKIN) {
			return 4;
		} else if(tier == DENTROTHYST) {
			return 5;
		} else if(tier == OCTINE) {
			return 5;
		} else if(tier == SYRMORITE) {
			return 5;
		} else if(tier == VALONITE) {
			return 6;
		} else if(tier == LOOT) {
			return 16;
		} else if(tier == LEGEND) {
			return 24;
		}
		return 4;
	}

	public static int getFullRepairFuelCost(IItemTier tier) {
		if(tier == WEEDWOOD) {
			return 6;
		} else if(tier == BONE) {
			return 8;
		} else if(tier == LURKER_SKIN) {
			return 10;
		} else if(tier == DENTROTHYST) {
			return 10;
		} else if(tier == OCTINE) {
			return 12;
		} else if(tier == SYRMORITE) {
			return 12;
		} else if(tier == VALONITE) {
			return 16;
		} else if(tier == LOOT) {
			return 32;
		} else if(tier == LEGEND) {
			return 48;
		}
		return 8;
	}

	public static int getMinRepairLifeCost(IItemTier tier) {
		if(tier == WEEDWOOD) {
			return 4;
		} else if(tier == BONE) {
			return 4;
		} else if(tier == LURKER_SKIN) {
			return 4;
		} else if(tier == DENTROTHYST) {
			return 4;
		} else if(tier == OCTINE) {
			return 5;
		} else if(tier == SYRMORITE) {
			return 5;
		} else if(tier == VALONITE) {
			return 12;
		} else if(tier == LOOT) {
			return 32;
		} else if(tier == LEGEND) {
			return 48;
		}
		return 4;
	}

	public static int getFullRepairLifeCost(IItemTier tier) {
		if(tier == WEEDWOOD) {
			return 16;
		} else if(tier == BONE) {
			return 16;
		} else if(tier == LURKER_SKIN) {
			return 16;
		} else if(tier == DENTROTHYST) {
			return 16;
		} else if(tier == OCTINE) {
			return 32;
		} else if(tier == SYRMORITE) {
			return 32;
		} else if(tier == VALONITE) {
			return 48;
		} else if(tier == LOOT) {
			return 64;
		} else if(tier == LEGEND) {
			return 110;
		}
		return 8;
	}

	private final int harvestLevel;
	private final int maxUses;
	private final float efficiency;
	private final float attackDamage;
	private final int enchantability;
	private final LazyLoadBase<Ingredient> repairMaterial;

	private BLItemTier(int harvestLevel, int maxUses, float efficiency, float attackDamage, int enchantability, Supplier<Ingredient> repairMaterial) {
		this.harvestLevel = harvestLevel;
		this.maxUses = maxUses;
		this.efficiency = efficiency;
		this.attackDamage = attackDamage;
		this.enchantability = enchantability;
		this.repairMaterial = new LazyLoadBase<>(repairMaterial);
	}

	@Override
	public int getMaxUses() {
		return this.maxUses;
	}

	@Override
	public float getEfficiency() {
		return this.efficiency;
	}

	@Override
	public float getAttackDamage() {
		return this.attackDamage;
	}

	@Override
	public int getHarvestLevel() {
		return this.harvestLevel;
	}

	@Override
	public int getEnchantability() {
		return this.enchantability;
	}

	@Override
	public Ingredient getRepairMaterial() {
		return this.repairMaterial.getValue();
	}
}
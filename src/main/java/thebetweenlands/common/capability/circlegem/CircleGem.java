package thebetweenlands.common.capability.circlegem;

import net.minecraft.nbt.NBTTagCompound;

public class CircleGem {
	public static enum CombatType {
		OFFENSIVE, DEFENSIVE, BOTH;
	}

	private final CircleGemType gemType;
	private final CombatType combatType;

	public CircleGem(CircleGemType gemType, CombatType combatType) {
		this.gemType = gemType;
		this.combatType = combatType;
	}

	/**
	 * Returns the gem type
	 * @return
	 */
	public CircleGemType getGemType() {
		return this.gemType;
	}

	/**
	 * Returns the combat type
	 * @return
	 */
	public CombatType getCombatType() {
		return this.combatType;
	}

	/**
	 * Returns whether the specified combat type matches
	 * @param type
	 * @return
	 */
	public boolean matchCombatType(CombatType type) {
		return this.combatType == CombatType.BOTH || type == this.combatType;
	}

	/**
	 * Writes this circle gem to the specified NBT
	 * @param nbt
	 * @return
	 */
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		nbt.setString("gem", this.gemType.name);
		nbt.setInteger("type", this.combatType.ordinal());
		return nbt;
	}

	/**
	 * Reads a circle gem from the specified NBT
	 * @param nbt
	 * @return
	 */
	public static CircleGem readFromNBT(NBTTagCompound nbt) {
		CircleGemType gem = CircleGemType.fromName(nbt.getString("gem"));
		int typeOrdinal = nbt.getInteger("type");
		if(CombatType.values().length > typeOrdinal) {
			return new CircleGem(gem, CombatType.values()[typeOrdinal]);
		}
		return null;
	}
}
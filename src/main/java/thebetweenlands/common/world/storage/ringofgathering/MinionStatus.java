package thebetweenlands.common.world.storage.ringofgathering;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

public enum MinionStatus implements StringRepresentable {
	/**
	 * Minion is currently alive (set on world autosave and minion dimension change)
	 */
	ALIVE("alive"),
	/**
	 * Minion is currently dead
	 */
	DEAD("dead"),
	/**
	 * Minion went into unloaded chunks
	 */
	UNLOADED("unloaded"),
	/**
	 * Minion could not be found when we looked up their last chunk and dimension.
	 * Also: the fallback when we can't parse a {@link MinionStatus} from name.
	 */
	MISSING("missing");
	
	public static final Codec<MinionStatus> CODEC = StringRepresentable.fromEnum(MinionStatus::values).orElse(MISSING);
	
	private final String name;
	
	private MinionStatus(String name) {
		this.name = name;
	}
	
	@Override
	public String getSerializedName() {
		return this.name;
	}
	
	public static MinionStatus byNameOrElse(String name, MinionStatus fallback) {
		for (MinionStatus minionStatus : MinionStatus.values()) {
			if (minionStatus.getSerializedName().equals(name)) {
				return minionStatus;
			}
		}
		
		return fallback;
	}
}

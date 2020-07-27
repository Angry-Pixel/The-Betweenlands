package thebetweenlands.common.item.herblore.rune;

public enum RuneTier {
	TIER_1(0, "tier_1"), TIER_2(1, "tier_2"), TIER_3(2, "tier_3");

	public static final RuneTier[] VALUES = RuneTier.values();
	public static final int COUNT = RuneTier.values().length;

	public final int id;
	public final String name;

	private RuneTier(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static RuneTier fromId(int id) {
		for(RuneTier tier : VALUES) {
			if(id == tier.id) {
				return tier;
			}
		}
		return null;
	}
}
package thebetweenlands.common.item.herblore.rune;

public enum RuneCategory {
	INITIATE(0, "initiate"), TOKEN(1, "token"), GATE(2, "gate"), CONDUCT(3, "conduct");

	public static final RuneCategory[] VALUES = RuneCategory.values();
	public static final int COUNT = RuneCategory.values().length;

	public final int id;
	public final String name;

	private RuneCategory(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static RuneCategory fromId(int id) {
		for(RuneCategory category : VALUES) {
			if(id == category.id) {
				return category;
			}
		}
		return null;
	}
}
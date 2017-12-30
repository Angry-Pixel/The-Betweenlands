package thebetweenlands.common.block.property;

public class PropertyIntegerUnlisted extends UnlistedPropertyHelper<Integer> {
	public PropertyIntegerUnlisted(String name) {
		super(name);
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}
}
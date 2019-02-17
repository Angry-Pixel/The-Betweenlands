package thebetweenlands.common.block.property;

public class IntegerPropertyUnlisted extends AbstractUnlistedProperty<Integer> {
	public IntegerPropertyUnlisted(String name) {
		super(name);
	}

	@Override
	public Class<Integer> getType() {
		return Integer.class;
	}
}
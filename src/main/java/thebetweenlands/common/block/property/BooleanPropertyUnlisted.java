package thebetweenlands.common.block.property;

public class BooleanPropertyUnlisted extends AbstractUnlistedProperty<Boolean> {
	public BooleanPropertyUnlisted(String name) {
		super(name);
	}

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
}
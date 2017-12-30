package thebetweenlands.common.block.property;

public class PropertyBoolUnlisted extends UnlistedPropertyHelper<Boolean> {
	public PropertyBoolUnlisted(String name) {
		super(name);
	}

	@Override
	public Class<Boolean> getType() {
		return Boolean.class;
	}
}
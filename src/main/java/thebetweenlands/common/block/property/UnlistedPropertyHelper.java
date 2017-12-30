package thebetweenlands.common.block.property;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class UnlistedPropertyHelper<T> implements IUnlistedProperty<T> {
	private final String name;
	private final Predicate<T> validator;

	public UnlistedPropertyHelper(String name) {
		this(name, Predicates.alwaysTrue());
	}

	public UnlistedPropertyHelper(String name, Predicate<T> validator) {
		this.name = name;
		this.validator = validator;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean isValid(T value) {
		return this.validator.apply(value);
	}

	@Override
	public String valueToString(T value) {
		return value.toString();
	}
}

package thebetweenlands.common.block.property;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import net.minecraftforge.common.property.IUnlistedProperty;

public abstract class AbstractUnlistedProperty<T> implements IUnlistedProperty<T> {
	private final String name;
	private final Predicate<T> validator;

	public AbstractUnlistedProperty(String name) {
		this(name, Predicates.alwaysTrue());
	}

	public AbstractUnlistedProperty(String name, Predicate<T> validator) {
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

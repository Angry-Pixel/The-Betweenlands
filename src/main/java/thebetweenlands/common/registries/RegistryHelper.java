package thebetweenlands.common.registries;

import java.util.function.Consumer;

import javax.annotation.Nullable;

public interface RegistryHelper<T> {
	<F extends T> F reg(String regName, F obj, @Nullable Consumer<F> callback);

	default <B extends T> B reg(String regName, B obj) {
		return reg(regName, obj, null);
	}
}

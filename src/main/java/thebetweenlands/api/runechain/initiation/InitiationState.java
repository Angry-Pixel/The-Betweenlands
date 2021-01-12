package thebetweenlands.api.runechain.initiation;

import java.util.function.Consumer;

import javax.annotation.Nullable;

import thebetweenlands.api.runechain.rune.AbstractRune;

public class InitiationState<T extends AbstractRune<T>> {
	protected boolean success;
	protected Consumer<T> initializer;

	protected InitiationState() {
		this.success = false;
		this.initializer = null;
	}

	protected InitiationState(boolean success, @Nullable Consumer<T> initializer) {
		this.success = success;
		this.initializer = initializer;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public void initiate(T state) {
		if(this.initializer != null) {
			this.initializer.accept(state);
		}
	}

	public static <T extends AbstractRune<T>> InitiationState<T> success(Consumer<T> initializer) {
		return new InitiationState<T>(true, initializer);
	}

	public static <T extends AbstractRune<T>> InitiationState<T> success() {
		return new InitiationState<T>(true, null);
	}
}
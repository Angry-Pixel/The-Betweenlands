package thebetweenlands.util;

import java.util.List;

import net.minecraft.util.NonNullList;

public class NonNullDelegateList<E> extends NonNullList<E> {
	public NonNullDelegateList(List<E> delegate, E defaultElement) {
		super(delegate, defaultElement);
	}
}

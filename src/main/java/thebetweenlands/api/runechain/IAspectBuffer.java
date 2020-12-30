package thebetweenlands.api.runechain;

import thebetweenlands.api.aspect.AspectContainer;
import thebetweenlands.api.aspect.IAspectType;

@FunctionalInterface
public interface IAspectBuffer {
	/**
	 * Gets an aspect container for the specified aspect type that
	 * can be modified. The container may be empty if no aspect container
	 * with the requested aspect type is available. All runes require and
	 * drain aspects from this container. This container could be used
	 * to provide an aspect buffer that is continuously refilled to
	 * restrict how often or how quickly the runes can be activated before
	 * they fail. Also keep in mind that this aspect container may be drained
	 * many times very quickly so it should be optimized for that (e.g. don't save
	 * on change but instead only after updating the rune chain).
	 * @param type the requested type of the aspect
	 */
	public AspectContainer get(IAspectType type);
}
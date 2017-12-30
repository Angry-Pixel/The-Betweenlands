package thebetweenlands.client.audio.ambience;

import net.minecraft.util.ResourceLocation;

public class AmbienceLayer {
	private final ResourceLocation name;
	private final int maxTracks;

	/**
	 * Creates a new ambience layer with a limited amount of tracks
	 * @param name
	 * @param maxTracks
	 */
	public AmbienceLayer(ResourceLocation name, int maxTracks) {
		this.name = name;
		this.maxTracks = maxTracks;
	}

	/**
	 * Creates a new ambience layer with no track limitations
	 * @param name
	 */
	public AmbienceLayer(ResourceLocation name) {
		this.name = name;
		this.maxTracks = Integer.MAX_VALUE;
	}

	/**
	 * Returns the name of this layer
	 * @return
	 */
	public ResourceLocation getName() {
		return this.name;
	}

	/**
	 * Returns how many tracks can play at once on this layer
	 * @return
	 */
	public int getMaxTracks() {
		return this.maxTracks;
	}
}

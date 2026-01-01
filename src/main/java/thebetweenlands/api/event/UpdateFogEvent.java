package thebetweenlands.api.event;

import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import thebetweenlands.api.misc.Fog;
import thebetweenlands.api.misc.FogState;

public class UpdateFogEvent extends Event {
	private FogState state;
	private Fog biomeFog;
	private Fog ambientFog;
	private Level world;
	private Vec3 position;
	private float farPlaneDistance;

	public UpdateFogEvent(FogState state, Fog biomeFog, Fog ambientFog, Vec3 position, Level world, float farPlaneDistance) {
		this.state = state;
		this.biomeFog = biomeFog;
		this.ambientFog = ambientFog;
		this.position = position;
		this.world = world;
		this.farPlaneDistance = farPlaneDistance;
	}

	/**
	 * Returns the far plane distance
	 * @return
	 */
	public float getFarPlaneDistance() {
		return this.farPlaneDistance;
	}

	/**
	 * Returns the position
	 * @return
	 */
	public Vec3 getPosition() {
		return this.position;
	}

	/**
	 * Returns the world
	 * @return
	 */
	public Level getWorld() {
		return this.world;
	}

	/**
	 * Returns the fog state
	 * @return
	 */
	public FogState getFogState() {
		return this.state;
	}

	/**
	 * Returns the biome fog
	 * @return
	 */
	public Fog getBiomeFog() {
		return this.biomeFog;
	}

	/**
	 * Returns the ambient fog (biome fog + ambient cave fog)
	 * @return
	 */
	public Fog getAmbientFog() {
		return this.ambientFog;
	}
}

package thebetweenlands.client.particle;

import javax.annotation.Nullable;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.world.World;
import thebetweenlands.client.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.particle.entity.ParticleAltarCrafting;
import thebetweenlands.client.particle.entity.ParticleBug;
import thebetweenlands.client.particle.entity.ParticlePortalBL;
import thebetweenlands.client.particle.entity.ParticleWisp;

public enum BLParticles {

	PORTAL(new ParticlePortalBL.Factory()),
	ALTAR_CRAFTING(new ParticleAltarCrafting.Factory()),
	SMOKE(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory())),
	SWAMP_SMOKE(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory()).getBaseArgsBuilder().withColor(0xFF2D4231).build()),
	FLAME(VanillaParticleFactory.create(ParticleFlame.class, new ParticleFlame.Factory())),
	GREEN_FLAME(VanillaParticleFactory.create(ParticleFlame.class, new ParticleFlame.Factory()).getBaseArgsBuilder().withColor(0xFF2C4231).build()),
	SULFUR_TORCH(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory()).getBaseArgsBuilder().withColor(0xFFE7f70E).build()),
	PURIFIER_STEAM(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory()).getBaseArgsBuilder().withColor(0xFFFFFFFF).build()),
	SULFUR_ORE(VanillaParticleFactory.create(ParticleSpell.class, new ParticleSpell.Factory()).getBaseArgsBuilder().withColor(0xFFE7f70E).build()),
	FLY(new ParticleBug.FlyFactory()),
	WISP(new ParticleWisp.Factory());





	private ParticleFactory factory;

	private BLParticles(ParticleFactory factory) {
		this.factory = factory;
	}

	public Class<? extends Particle> getType() {
		return this.factory.getType();
	}

	public ParticleFactory getFactory() {
		return this.factory;
	}

	/**
	 * Creates a new instance of this particle
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 * @return
	 */
	public Particle create(World world, double x, double y, double z, @Nullable ParticleArgs args) {
		return this.getFactory().create(world, x, y, z, args);
	}

	/**
	 * Spawns this particle
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 * @return
	 */
	public Particle spawn(World world, double x, double y, double z, @Nullable ParticleArgs args) {
		return this.getFactory().spawn(world, x, y, z, args);
	}

	/**
	 * Creates a new instance of this particle
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Particle create(World world, double x, double y, double z) {
		return this.getFactory().create(world, x, y, z, null);
	}

	/**
	 * Spawns this particle
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 */
	public Particle spawn(World world, double x, double y, double z) {
		return this.getFactory().spawn(world, x, y, z, null);
	}
}

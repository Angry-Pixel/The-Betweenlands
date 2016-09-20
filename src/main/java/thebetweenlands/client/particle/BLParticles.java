package thebetweenlands.client.particle;

import javax.annotation.Nullable;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.world.World;
import thebetweenlands.client.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.particle.entity.ParticleAltarCrafting;
import thebetweenlands.client.particle.entity.ParticleBreakingBL;
import thebetweenlands.client.particle.entity.ParticleBubbleBL;
import thebetweenlands.client.particle.entity.ParticleBug;
import thebetweenlands.client.particle.entity.ParticleCaveWaterDrip;
import thebetweenlands.client.particle.entity.ParticleDruidCasting;
import thebetweenlands.client.particle.entity.ParticleFish;
import thebetweenlands.client.particle.entity.ParticleMoth;
import thebetweenlands.client.particle.entity.ParticlePortalBL;
import thebetweenlands.client.particle.entity.ParticleWisp;

public enum BLParticles {

	PORTAL(new ParticlePortalBL.Factory()),
	ALTAR_CRAFTING(new ParticleAltarCrafting.Factory()),
	SMOKE(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory())),
	SWAMP_SMOKE(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory())
			.getBaseArgsBuilder()
			.withColor(0xFF2D4231)
			.buildBaseArgs()
			),
	FLAME(VanillaParticleFactory.create(ParticleFlame.class, new ParticleFlame.Factory())),
	GREEN_FLAME(VanillaParticleFactory.create(ParticleFlame.class, new ParticleFlame.Factory())
			.getBaseArgsBuilder()
			.withColor(0xFF2C4231)
			.buildBaseArgs()),
	SULFUR_TORCH(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory())
			.getBaseArgsBuilder()
			.withColor(0xFFE7f70E)
			.buildBaseArgs()),
	SULFUR_ORE(VanillaParticleFactory.create(ParticleSpell.class, new ParticleSpell.Factory())
			.getBaseArgsBuilder()
			.withColor(0xFFE7f70E)
			.buildBaseArgs()),
	SNAIL_POISON(VanillaParticleFactory.create(ParticleSpell.class, new ParticleSpell.Factory())
			.getBaseArgsBuilder()
			.withColor(0xFFFF0000)
			.buildBaseArgs()),
	PURIFIER_STEAM(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory())
			.getBaseArgsBuilder()
			.withColor(0xFFFFFFFF)
			.buildBaseArgs()),
	FLY(new ParticleBug.FlyFactory()),
	MOTH(new ParticleMoth.Factory()),
	MOSQUITO(new ParticleBug.MosquitoFactory()),
	WATER_BUG(new ParticleBug.WaterBugFactory()),
	FISH(new ParticleFish.Factory()),
	WISP(new ParticleWisp.Factory()),
	DRUID_CASTING(new ParticleDruidCasting.Factory()),
	DRUID_CASTING_BIG(new ParticleDruidCasting.Factory()
			.getBaseArgsBuilder()
			.withColor(0, 1, 1, 1)
			.buildBaseArgs()),
	DIRT_DECAY(VanillaParticleFactory.create(ParticleSpell.class, new ParticleSpell.Factory())
			.getBaseArgsBuilder()
			.withColor(0.306F, 0.576F, 0.192F, 1.0F)
			.buildBaseArgs()),
	BUBBLE_PURIFIER(new ParticleBubbleBL.Factory()
			.getBaseArgsBuilder()
			.withColor(0.306F, 0.576F, 0.192F, 1.0F)
			.buildBaseArgs()),
	BUBBLE_INFUSION(new ParticleBubbleBL.Factory()
			.getBaseArgsBuilder()
			.withColor(0.5F, 0F, 0.125F, 1.0F)
			.buildBaseArgs()),
	BUBBLE_TAR(new ParticleBubbleBL.Factory()
			.getBaseArgsBuilder()
			.withColor(0, 0, 0, 1.0F)
			.buildBaseArgs()),
	SPLASH_TAR(new ParticleBreakingBL.Factory()
			.getBaseArgsBuilder()
			.withColor(0, 0, 0, 1.0F)
			.buildBaseArgs()),
	CAVE_WATER_DRIP(new ParticleCaveWaterDrip.Factory()),
	STEAM_PURIFIER(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory()));





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

package thebetweenlands.client.render.particle;

import javax.annotation.Nullable;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleCrit;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleRedstone;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.client.particle.ParticleSplash;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleFactory.ParticleArgs;
import thebetweenlands.client.render.particle.entity.ParticleAltarCrafting;
import thebetweenlands.client.render.particle.entity.ParticleAnimated;
import thebetweenlands.client.render.particle.entity.ParticleAnimator;
import thebetweenlands.client.render.particle.entity.ParticleBLRain;
import thebetweenlands.client.render.particle.entity.ParticleBlockProtection;
import thebetweenlands.client.render.particle.entity.ParticleBreakingBL;
import thebetweenlands.client.render.particle.entity.ParticleBubbleBL;
import thebetweenlands.client.render.particle.entity.ParticleBug;
import thebetweenlands.client.render.particle.entity.ParticleCaveWaterDrip;
import thebetweenlands.client.render.particle.entity.ParticleChiromawDroppings;
import thebetweenlands.client.render.particle.entity.ParticleChiromawFeathers;
import thebetweenlands.client.render.particle.entity.ParticleDamageReduction;
import thebetweenlands.client.render.particle.entity.ParticleDraetonBurnerFlame;
import thebetweenlands.client.render.particle.entity.ParticleDruidCasting;
import thebetweenlands.client.render.particle.entity.ParticleEmissiveBug;
import thebetweenlands.client.render.particle.entity.ParticleEmissiveSwarm;
import thebetweenlands.client.render.particle.entity.ParticleEntitySwirl;
import thebetweenlands.client.render.particle.entity.ParticleFancyBubble;
import thebetweenlands.client.render.particle.entity.ParticleFancyDrip;
import thebetweenlands.client.render.particle.entity.ParticleFish;
import thebetweenlands.client.render.particle.entity.ParticleFishVortex;
import thebetweenlands.client.render.particle.entity.ParticleGasCloud;
import thebetweenlands.client.render.particle.entity.ParticleLifeEssence;
import thebetweenlands.client.render.particle.entity.ParticleLightningArc;
import thebetweenlands.client.render.particle.entity.ParticleMoth;
import thebetweenlands.client.render.particle.entity.ParticlePuzzleBeam;
import thebetweenlands.client.render.particle.entity.ParticlePuzzleBeam2;
import thebetweenlands.client.render.particle.entity.ParticleRingOfRecruitmentState;
import thebetweenlands.client.render.particle.entity.ParticleRootSpike;
import thebetweenlands.client.render.particle.entity.ParticleSimple;
import thebetweenlands.client.render.particle.entity.ParticleSonicScream;
import thebetweenlands.client.render.particle.entity.ParticleSoundRipple;
import thebetweenlands.client.render.particle.entity.ParticleSpiritButterfly;
import thebetweenlands.client.render.particle.entity.ParticleSwarm;
import thebetweenlands.client.render.particle.entity.ParticleTarBeastDrip;
import thebetweenlands.client.render.particle.entity.ParticleThem;
import thebetweenlands.client.render.particle.entity.ParticleUrchinSpike;
import thebetweenlands.client.render.particle.entity.ParticleVisionOrb;
import thebetweenlands.client.render.particle.entity.ParticleWaterRipple;
import thebetweenlands.client.render.particle.entity.ParticleWeedwoodLeaf;
import thebetweenlands.client.render.particle.entity.ParticleWisp;
import thebetweenlands.client.render.particle.entity.ParticleXPPieces;


public enum BLParticles {

	PORTAL(new ParticleAnimated.PortalFactory()),
	SPAWNER(new ParticleAnimated.SpawnerFactory()),
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
	SPIRIT_BUTTERFLY(new ParticleSpiritButterfly.Factory()),
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
	BUBBLE_WATER(new ParticleBubbleBL.WaterFactory()),
	BUBBLE_PURIFIER(new ParticleBubbleBL.InfuserFactory()
			.getBaseArgsBuilder()
			.withColor(0.306F, 0.576F, 0.192F, 1.0F)
			.buildBaseArgs()),
	BUBBLE_INFUSION(new ParticleBubbleBL.InfuserFactory()
			.getBaseArgsBuilder()
			.withColor(0.5F, 0F, 0.125F, 1.0F)
			.buildBaseArgs()),
	BUBBLE_TAR(new ParticleBubbleBL.InfuserFactory()
			.getBaseArgsBuilder()
			.withColor(0, 0, 0, 1.0F)
			.buildBaseArgs()),
	SPLASH_TAR(new ParticleBreakingBL.Factory()
			.getBaseArgsBuilder()
			.withColor(0, 0, 0, 1.0F)
			.buildBaseArgs()),
	MOTION_ITEM_BREAKING(new ParticleBreakingBL.MotionFactory()),
	ITEM_BREAKING(new ParticleBreakingBL.DefaultFactory()),
	TAR_BEAST_DRIP(new ParticleTarBeastDrip.Factory().getBaseArgsBuilder().withColor(0, 0, 0, 1).buildBaseArgs()),
	CAVE_WATER_DRIP(new ParticleCaveWaterDrip.Factory()),
	STEAM_PURIFIER(VanillaParticleFactory.create(ParticleSmokeNormal.class, new ParticleSmokeNormal.Factory())),
	GAS_CLOUD(new ParticleGasCloud.Factory()),
	WEEDWOOD_LEAF(new ParticleWeedwoodLeaf.Factory()),
	LEAF_SWIRL(new ParticleEntitySwirl.Factory()),
	REDSTONE_DUST(VanillaParticleFactory.create(ParticleRedstone.class, new ParticleRedstone.Factory())),
	THEM(new ParticleThem.Factory()),
	GEM_PROC(VanillaParticleFactory.create(ParticleCrit.class, new ParticleCrit.MagicFactory())),
	ANIMATOR(new ParticleAnimator.Factory()),
	SPLASH(VanillaParticleFactory.create(ParticleSplash.class, new ParticleSplash.Factory())),
	BLOCK_PROTECTION(new ParticleBlockProtection.Factory()),
	EMBER_1(new ParticleSimple.GenericFactory(new ResourceLocation("thebetweenlands:particle/ember_1"))),
	EMBER_2(new ParticleSimple.GenericFactory(new ResourceLocation("thebetweenlands:particle/ember_2"))),
	EMBER_3(new ParticleSimple.GenericFactory(new ResourceLocation("thebetweenlands:particle/ember_3"))),
	XP_PIECES(new ParticleXPPieces.Factory()),
	DAMAGE_REDUCTION(new ParticleDamageReduction.Factory()),
	ROOT_SPIKE(new ParticleRootSpike.Factory()),
	CORRUPTED(new ParticleSimple.GenericFactory(new ResourceLocation("thebetweenlands:particle/corrupted")).getBaseArgsBuilder().withDataBuilder().setData(2, 1.0F).buildData().buildBaseArgs()),
	SOUND_RIPPLE(new ParticleSoundRipple.Factory()),
	LIFE_ESSENCE(new ParticleLifeEssence.Factory()),
	PUZZLE_BEAM(new ParticlePuzzleBeam.Factory()),
	PUZZLE_BEAM_2(new ParticlePuzzleBeam2.Factory()),
	SMOOTH_SMOKE(new ParticleSimple.GenericFactory(new ResourceLocation("thebetweenlands:particle/smooth_smoke"))),
	SONIC_SCREAM(new ParticleSonicScream.Factory()),
	EMBER_SWIRL(new ParticleEntitySwirl.FactoryEmberSwirl()),
	SLUDGE_SWIRL(new ParticleEntitySwirl.FactorySludgeSwirl()),
	RING_OF_RECRUITMENT_STAY(new ParticleRingOfRecruitmentState.FactoryStay()),
	RING_OF_RECRUITMENT_FOLLOW(new ParticleRingOfRecruitmentState.FactoryFollow()),
	RING_OF_RECRUITMENT_GUARD(new ParticleRingOfRecruitmentState.FactoryGuard()),
	DRAETON_BURNER_FLAME(new ParticleDraetonBurnerFlame.Factory()),
	CHIROMAW_DROPPINGS(new ParticleChiromawDroppings.Factory().getBaseArgsBuilder().withColor(0, 0, 0, 1).buildBaseArgs()),
	LIGHTNING_ARC(new ParticleLightningArc.Factory()),
	CHIROMAW_TRANSFORM(new ParticleChiromawFeathers.Factory().getBaseArgsBuilder().withColor(0.227F, 0.317F, 0.294F, 1).buildBaseArgs()),
	CHIROMAW_TRANSFORM_LIGHTNING(new ParticleChiromawFeathers.Factory().getBaseArgsBuilder().withColor(0.420F, 0.565F, 0.553F, 1).buildBaseArgs()),
	CHIROMAW_TRANSFORM_SWIRL(new ParticleEntitySwirl.FactoryChiromawSwirl()),
	WATER_RIPPLE(new ParticleWaterRipple.Factory()),
	RAIN(new ParticleBLRain.Factory()),
	SWARM(new ParticleSwarm.Factory()),
	SWARM_EMISSIVE(new ParticleEmissiveSwarm.Factory()),
	FLYING_SWARM_EMISSIVE(new ParticleEmissiveBug.Swarm()),
	WIND_CHIME_VISION(new ParticleVisionOrb.Factory(new ResourceLocation("thebetweenlands:particle/wind_chime_vision_mask"))),
	URCHIN_SPIKE(new ParticleUrchinSpike.Factory()),
	FANCY_DRIP(new ParticleFancyDrip.Factory()),
	FANCY_BUBBLE(new ParticleFancyBubble.Factory()),
	FISH_VORTEX(new ParticleFishVortex.Factory()),
	SHADOW_GHOSTS(new ParticleAnimated.ShadowGhostFactory());
	
	private ParticleFactory<?, ?> factory;

	private BLParticles(ParticleFactory<?, ?> factory) {
		this.factory = factory;
	}

	public Class<? extends Particle> getType() {
		return this.factory.getType();
	}

	public ParticleFactory<?, ?> getFactory() {
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
	public Particle create(World world, double x, double y, double z, @Nullable ParticleArgs<?> args) {
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
	public Particle spawn(World world, double x, double y, double z, @Nullable ParticleArgs<?> args) {
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

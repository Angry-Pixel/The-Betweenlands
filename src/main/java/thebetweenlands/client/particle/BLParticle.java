package thebetweenlands.client.particle;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFlame;
import net.minecraft.client.particle.ParticleSmokeNormal;
import net.minecraft.client.particle.ParticleSpell;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.client.particle.entity.ParticleBug;
import thebetweenlands.client.particle.entity.ParticlePortal;
import thebetweenlands.common.tile.TileEntityDruidAltar;

@SideOnly(Side.CLIENT)
public enum BLParticle {
	ALTAR_CRAFTING(ParticleAltarCrafting.class, ParticleArgs.SCALE, TileEntityDruidAltar.class) {
		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { data[0] };
		}
	},
	SMOKE(ParticleSmokeNormal.class),
	SWAMP_SMOKE(ParticleSmokeNormal.class, 45, 66, 49),
	FLAME(ParticleFlame.class),
	GREEN_FLAME(ParticleFlame.class, 0.176f, 0.259f, 0.192f),
	SULFUR_TORCH(ParticleSmokeNormal.class, 1, 0.9294F, 0, ParticleArgs.V0_V0_V0),
	STEAM_PURIFIER(ParticleSmokeNormal.class, 1, 1, 1, ParticleArgs.V0_V0_V0),
	FLY(ParticleBug.class, ParticleArgs.NONE, int.class, float.class, float.class, float.class, int.class, boolean.class, ResourceLocation.class, int.class) {
		private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/particle/fly.png");

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[]{400, 0.05F, 0.025F, 0.06F * world.rand.nextFloat(), 0xFFFFFFFF, false, texture, 2};
		}
	},
	PORTAL(ParticlePortal.class, ParticleArgs.VX_VY_VZ, int.class, float.class, int.class, ResourceLocation.class, int.class) {
		private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/particle/portal.png");

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 20, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, texture, 6 };
		}
	},
	SULFUR_ORE(ParticleSpell.class, 1, 0.9294F, 0);

	private static final int REGULAR_ARG_NUM = 4;

	private Constructor<? extends Particle> constructor;

	private ParticleArgs args;

	private Class<?>[] additionalArgTypes;

	private boolean shouldAssignColor = false;

	private float r;

	private float g;

	private float b;

	BLParticle(Class<? extends Particle> particleClass) {
		this(particleClass, -1, 0xDEAD, 0xC0DE);
	}

	BLParticle(Class<? extends Particle> particleClass, float r, float g, float b) {
		this(particleClass, r, g, b, ParticleArgs.VX_VY_VZ);
	}

	BLParticle(Class<? extends Particle> particleClass, ParticleArgs args, Class<?>... additionalArgTypes) {
		this(particleClass, -1, 0xDEAD, 0xC0DE, args, additionalArgTypes);
	}

	BLParticle(Class<? extends Particle> particleClass, float r, float g, float b, ParticleArgs args, Class<?>... additionalArgTypes) {
		if (r != -1) {
			shouldAssignColor = true;
			this.r = r;
			this.g = g;
			this.b = b;
		}
		try {
			particleClass.getDeclaredConstructor(getArgumentTypes(args, additionalArgTypes)).setAccessible(true);
			constructor = particleClass.getDeclaredConstructor(getArgumentTypes(args, additionalArgTypes));
			constructor.setAccessible(true);
		} catch (Exception e) {
			CrashReport crash = CrashReport.makeCrashReport(e, "Constructing BLParticle");
			CrashReportCategory categoryArguments = crash.makeCategory("Arguments");
			categoryArguments.addCrashSection("Class", particleClass);
			categoryArguments.addCrashSection("Particle Arg Types", args);
			categoryArguments.addCrashSection("Additional Arg Types", Arrays.toString(additionalArgTypes));
			throw new ReportedException(crash);
		}
		this.args = args;
		this.additionalArgTypes = additionalArgTypes;
	}

	private static Class<?>[] getArgumentTypes(ParticleArgs args, Class<?>[] additionalArgTypes) {
		Class<?>[] argumentTypes = new Class<?>[REGULAR_ARG_NUM + args.getArgumentCount() + additionalArgTypes.length];
		argumentTypes[0] = World.class;
		argumentTypes[1] = double.class;
		argumentTypes[2] = double.class;
		argumentTypes[3] = double.class;
		System.arraycopy(args.getArgumentTypes(), 0, argumentTypes, REGULAR_ARG_NUM, args.getArgumentCount());
		System.arraycopy(additionalArgTypes, 0, argumentTypes, REGULAR_ARG_NUM + args.getArgumentCount(), additionalArgTypes.length);
		return argumentTypes;
	}

	protected Object[] getAdditionalArgs(World world, Object... data) {
		return new Object[0];
	}

	protected void onSpawn(Particle particle) {}

	public final void spawn(World world, double x, double y, double z) {
		spawn(world, x, y, z, 0, 0, 0, 1);
	}

	public final void spawn(World world, BlockPos pos){ spawn(world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());}

	public final void spawn(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale, Object... data) {
		Object[] arguments = getArguments(world, x, y, z, motionX, motionY, motionZ, scale, data);
		try {
			Particle particle = constructor.newInstance(arguments);
			if (shouldAssignColor) {
				particle.setRBGColorF(r, g, b);
			}
			onSpawn(particle);
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		} catch (Exception e) {
			CrashReport crash = CrashReport.makeCrashReport(e, "Constructing EntityFX");
			CrashReportCategory categorySpawnArguments = crash.makeCategory("Spawn Arguments");
			categorySpawnArguments.addCrashSection("World", world);
			categorySpawnArguments.addCrashSection("X", x);
			categorySpawnArguments.addCrashSection("Y", y);
			categorySpawnArguments.addCrashSection("Z", z);
			categorySpawnArguments.addCrashSection("Motion X", motionX);
			categorySpawnArguments.addCrashSection("Motion Y", motionY);
			categorySpawnArguments.addCrashSection("Motion Z", motionZ);
			categorySpawnArguments.addCrashSection("Scale", scale);
			categorySpawnArguments.addCrashSection("Data", Arrays.deepToString(data));
			CrashReportCategory categoryBLParticle = crash.makeCategory("BLParticle");
			categoryBLParticle.addCrashSection("Constructor", constructor);
			categoryBLParticle.addCrashSection("Particle Arg Types", args);
			categoryBLParticle.addCrashSection("Additional Arg Types", Arrays.toString(additionalArgTypes));
			CrashReportCategory categoryArguments = crash.makeCategory("Arguments");
			categoryArguments.addCrashSection("Arguments", Arrays.deepToString(arguments));
			throw new ReportedException(crash);
		}
	}

	private Object[] getArguments(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale, Object... data) {
		Object[] particleArgs = args.getArguments(motionX, motionY, motionZ, scale);
		Object[] additionalArgs = getAdditionalArgs(world, data);
		Object[] arguments = new Object[REGULAR_ARG_NUM + args.getArgumentCount() + additionalArgs.length];
		arguments[0] = world;
		arguments[1] = x;
		arguments[2] = y;
		arguments[3] = z;
		System.arraycopy(particleArgs, 0, arguments, REGULAR_ARG_NUM, args.getArgumentCount());
		System.arraycopy(additionalArgs, 0, arguments, REGULAR_ARG_NUM + args.getArgumentCount(), additionalArgs.length);
		return arguments;
	}



}

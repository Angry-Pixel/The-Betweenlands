package thebetweenlands.client.particle;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import thebetweenlands.entities.particles.EntityAltarCraftingFX;
import thebetweenlands.entities.particles.EntityBLBubbleFX;
import thebetweenlands.entities.particles.EntityBugFX;
import thebetweenlands.entities.particles.EntityCaveWaterDripFX;
import thebetweenlands.entities.particles.EntityDruidCastingFX;
import thebetweenlands.entities.particles.EntityGasCloudFX;
import thebetweenlands.entities.particles.EntityLeafFX;
import thebetweenlands.entities.particles.EntityLeafSwirlFX;
import thebetweenlands.entities.particles.EntityPortalFX;
import thebetweenlands.entities.particles.EntitySplashFX;
import thebetweenlands.entities.particles.EntityTarBeastDrip;
import thebetweenlands.entities.particles.EntityWeedWoodRustleFX;
import thebetweenlands.tileentities.TileEntityDruidAltar;

@SideOnly(Side.CLIENT)
public enum BLParticle {
	DRUID_MAGIC(EntityDruidCastingFX.class, ParticleArgs.VX_VY_VZ_SCALE),
	DRUID_MAGIC_BIG(EntityDruidCastingFX.class, 0, 1, 1, ParticleArgs.VX_VY_VZ_SCALE),
	ALTAR_CRAFTING(EntityAltarCraftingFX.class, ParticleArgs.SCALE, TileEntityDruidAltar.class) {
		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { data[0] };
		}
	},
	SMOKE(EntitySmokeFX.class),
	SWAMP_SMOKE(EntitySmokeFX.class, 45, 66, 49),
	FLAME(EntityFlameFX.class),
	GREEN_FLAME(EntityFlameFX.class, 0.176f, 0.259f, 0.192f),
	SULFUR_TORCH(EntitySmokeFX.class, 1, 0.9294F, 0, ParticleArgs.V0_V0_V0),
	SULFUR_ORE(EntitySpellParticleFX.class, 1, 0.9294F, 0),
	SNAIL_POSION(EntitySpellParticleFX.class, 1, 0, 0),
	DIRT_DECAY(EntitySpellParticleFX.class, 0.306F, 0.576F, 0.192F),
	BUBBLE_PRUIFIER(EntityBLBubbleFX.class, 0.306F, 0.576F, 0.192F),
	BUBBLE_INFUSION(EntityBLBubbleFX.class, 0.5F, 0F, 0.125F) {
		@Override
		protected void onSpawn(EntityFX entityFX) {
			entityFX.setAlphaF(0.5F);
		}
	},
	BUBBLE_TAR_BEAST(EntityBLBubbleFX.class, 0, 0, 0),
	SPLASH_TAR_BEAST(EntityBreakingFX.class, 0, 0, 0, ParticleArgs.VX_VY_VZ, Item.class, int.class) {
		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { Items.slime_ball, 0 };
		}
	},
	DRIP_TAR_BEAST(EntityTarBeastDrip.class, 0, 0, 0),
	STEAM_PURIFIER(EntitySmokeFX.class, 1, 1, 1, ParticleArgs.V0_V0_V0),
	PORTAL(EntityPortalFX.class, ParticleArgs.VX_VY_VZ, int.class, float.class, int.class, ResourceLocation.class, int.class) {
		private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/particle/portal.png");

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 20, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, texture, 6 };
		}
	},
	MOTH(EntityBugFX.class, ParticleArgs.NONE, int.class, float.class, float.class, float.class, int.class, boolean.class, ResourceLocation.class, int.class) {
		private final ResourceLocation[] textures = new ResourceLocation[] {
				new ResourceLocation("thebetweenlands:textures/particle/moth1.png"),
				new ResourceLocation("thebetweenlands:textures/particle/moth2.png")
		};

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 400, 0.02F, 0.005F, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, false, textures[world.rand.nextInt(textures.length)], 2 };
		}
	},
	FISH(EntityBugFX.class, ParticleArgs.NONE, int.class, float.class, float.class, float.class, int.class, boolean.class, ResourceLocation.class, int.class) {
		private final ResourceLocation[] textures = new ResourceLocation[] {
				new ResourceLocation("thebetweenlands:textures/particle/fish1.png"),
				new ResourceLocation("thebetweenlands:textures/particle/fish2.png"),
				new ResourceLocation("thebetweenlands:textures/particle/fish3.png")
		};

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 400, 0.02F, 0.005F, 0.18F * world.rand.nextFloat(), 0xFFFFFFFF, true, textures[world.rand.nextInt(textures.length)], 1 };
		}
	},
	FLY(EntityBugFX.class, ParticleArgs.NONE, int.class, float.class, float.class, float.class, int.class, boolean.class, ResourceLocation.class, int.class) {
		private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/particle/fly.png");

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 400, 0.05F, 0.025F, 0.06F * world.rand.nextFloat(), 0xFFFFFFFF, false, texture, 2 };
		}
	},
	MOSQUITO(EntityBugFX.class, ParticleArgs.NONE, int.class, float.class, float.class, float.class, int.class, boolean.class, ResourceLocation.class, int.class) {
		private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/particle/mosquito.png");

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 400, 0.05F, 0.025F, 0.1F * world.rand.nextFloat(), 0xFFFFFFFF, false, texture, 2 };
		}
	},
	WATER_BUG(EntityBugFX.class, ParticleArgs.NONE, int.class, float.class, float.class, float.class, int.class, boolean.class, ResourceLocation.class, int.class) {
		private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/particle/waterbug.png");

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 400, 0.03F, 0.002F, 0.2F * world.rand.nextFloat(), 0xFFFFFFFF, true, texture, 2 };
		}
	},
	LEAF(EntityLeafFX.class, ParticleArgs.NONE, int.class, float.class, int.class, ResourceLocation.class, int.class) {
		private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/particle/leaf.png");

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 400, 0.12F * world.rand.nextFloat() + 0.03F, 0xFFFFFFFF, texture, 5 };
		}
	},
	LEAF_SWIRL(EntityLeafSwirlFX.class, ParticleArgs.NONE, int.class, float.class, int.class, ResourceLocation.class, int.class, Entity.class, float.class) {
		private final ResourceLocation texture = new ResourceLocation("thebetweenlands:textures/particle/leaf.png");

		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { 400, 0.12F * world.rand.nextFloat() + 0.03F, 0xFFFFFFFF, texture, 5, (Entity)data[0], (float)data[1]};
		}
	},
	SPLASH(EntitySplashFX.class, ParticleArgs.VX_VY_VZ, int.class) {
		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { data.length == 0 || !(data[0] instanceof Integer) ? 0xFFFFFF : (int) data[0] };
		}
	},
	CAVE_WATER_DRIP(EntityCaveWaterDripFX.class, ParticleArgs.NONE),
	RUSTLE_LEAF(EntityWeedWoodRustleFX.class, ParticleArgs.NONE),
	/*GAS_CLOUD(EntityGasCloudFX.class, ParticleArgs.VX_VY_VZ, int.class) {
		@Override
		protected Object[] getAdditionalArgs(World world, Object... data) {
			return new Object[] { data.length == 0 || !(data[0] instanceof Integer) ? 0xFFFFFFFF : (int) data[0] };
		}
	}*/;

	private static final int REGULAR_ARG_NUM = 4;

	private Constructor<? extends EntityFX> constructor;

	private ParticleArgs args;

	private Class<?>[] additionalArgTypes;

	private boolean shouldAssignColor = false;

	private float r;

	private float g;

	private float b;

	BLParticle(Class<? extends EntityFX> fxClass) {
		this(fxClass, -1, 0xDEAD, 0xC0DE);
	}

	BLParticle(Class<? extends EntityFX> fxClass, float r, float g, float b) {
		this(fxClass, r, g, b, ParticleArgs.VX_VY_VZ);
	}

	BLParticle(Class<? extends EntityFX> fxClass, ParticleArgs args, Class<?>... additionalArgTypes) {
		this(fxClass, -1, 0xDEAD, 0xC0DE, args, additionalArgTypes);
	}

	BLParticle(Class<? extends EntityFX> fxClass, float r, float g, float b, ParticleArgs args, Class<?>... additionalArgTypes) {
		if (r != -1) {
			shouldAssignColor = true;
			this.r = r;
			this.g = g;
			this.b = b;
		}
		try {
			constructor = fxClass.getConstructor(getArgumentTypes(args, additionalArgTypes));
		} catch (Exception e) {
			CrashReport crash = CrashReport.makeCrashReport(e, "Constructing BLParticle");
			CrashReportCategory categoryArguments = crash.makeCategory("Arguments");
			categoryArguments.addCrashSection("Class", fxClass);
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

	protected void onSpawn(EntityFX entityFX) {}

	public final void spawn(World world, double x, double y, double z) {
		spawn(world, x, y, z, 0, 0, 0, 1);
	}

	public final void spawn(World world, double x, double y, double z, double motionX, double motionY, double motionZ, float scale, Object... data) {
		Object[] arguments = getArguments(world, x, y, z, motionX, motionY, motionZ, scale, data);
		try {
			EntityFX entityFX = constructor.newInstance(arguments);
			if (shouldAssignColor) {
				entityFX.setRBGColorF(r, g, b);
			}
			onSpawn(entityFX);
			Minecraft.getMinecraft().effectRenderer.addEffect(entityFX);
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

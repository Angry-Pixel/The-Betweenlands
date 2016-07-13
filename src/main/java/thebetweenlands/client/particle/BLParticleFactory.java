package thebetweenlands.client.particle;

import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import thebetweenlands.client.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public abstract class BLParticleFactory<T extends Particle> {
	/**
	 * Immutable particle arguments
	 */
	public static class ImmutableParticleArgs {
		public final World world;
		public final double x, y, z, motionX, motionY, motionZ;
		public final float scale;
		public final int color;
		public final Object[] data;

		public ImmutableParticleArgs(World world, double x, double y, double z, ParticleArgs builder) {
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.motionX = builder.motionX;
			this.motionY = builder.motionY;
			this.motionZ = builder.motionZ;
			this.scale = builder.scale;
			this.color = builder.color;
			this.data = builder.data;
			builder.reset();
		}
	}

	/**
	 * Particle arguments
	 */
	public static final class ParticleArgs {
		private static final ParticleArgs BUILDER = new ParticleArgs();

		private static final Object[] NO_DATA = new Object[0];

		private boolean motionSet = false;
		private double motionX, motionY, motionZ;
		private boolean scaleSet = false;
		private float scale;
		private boolean colorSet = false;
		private int color;
		private Object[] data;
		private boolean dataSet = false;

		private ParticleArgs() {
			this.reset();
		}

		private void reset() {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.scale = 1;
			this.color = 0xFFFFFFFF;
			this.data = NO_DATA;
			this.dataSet = false;
			this.motionSet = false;
			this.scaleSet = false;
			this.colorSet = false;
		}

		/**
		 * Sets the motion values
		 * @param motionX
		 * @param motionY
		 * @param motionZ
		 * @return
		 */
		public ParticleArgs withMotion(double motionX, double motionY, double motionZ) {
			this.motionX = motionX;
			this.motionY = motionY;
			this.motionZ = motionZ;
			this.motionSet = true;
			return this;
		}

		/**
		 * Sets the scale
		 * @param scale
		 * @return
		 */
		public ParticleArgs withScale(float scale) {
			this.scale = scale;
			this.scaleSet = true;
			return this;
		}

		/**
		 * Sets the color
		 * @param color
		 * @return
		 */
		public ParticleArgs withColor(int color) {
			this.color = color;
			this.colorSet = true;
			return this;
		}

		/**
		 * Sets the additional data
		 * @param data
		 * @return
		 */
		public ParticleArgs withData(Object... data) {
			if(data == null) 
				data = NO_DATA;
			this.data = data;
			this.dataSet = true;
			return this;
		}

		/**
		 * Returns the motion X
		 * @return
		 */
		public double getMotionX() {
			return this.motionX;
		}

		/**
		 * Returns the motion Y
		 * @return
		 */
		public double getMotionY() {
			return this.motionY;
		}

		/**
		 * Returns the motion Z
		 * @return
		 */
		public double getMotionZ() {
			return this.motionZ;
		}

		/**
		 * Returns whether the motion was set
		 * @return
		 */
		public boolean isMotionSet() {
			return this.motionSet;
		}

		/**
		 * Returns the scale
		 * @return
		 */
		public float getScale() {
			return this.scale;
		}

		/**
		 * Returns whether the scale was set
		 * @return
		 */
		public boolean isScaleSet() {
			return this.scaleSet;
		}

		/**
		 * Returns the color
		 * @return
		 */
		public int getColor() {
			return this.color;
		}

		/**
		 * Returns whether the color was set
		 * @return
		 */
		public boolean isColorSet() {
			return this.colorSet;
		}

		/**
		 * Returns the additional data
		 * @return
		 */
		public Object[] getData() {
			return this.data;
		}

		/**
		 * Returns whether the additional data was set
		 * @return
		 */
		public boolean isDataSet() {
			return this.dataSet;
		}

		/**
		 * Returns a singleton instance of ParticleArgs
		 * @return
		 */
		public static ParticleArgs get() {
			BUILDER.reset();
			return BUILDER;
		}
	}

	private final ParticleTextureStitcher<T> stitcher;
	private final Class<T> type;
	private ParticleArgs defaults;

	/**
	 * Creates a new particle factory for the specified particle type
	 * @param type
	 */
	public BLParticleFactory(Class<T> type) {
		this(type, null);
	}

	/**
	 * Creates a new particle factory for the specified particle and adds a particle texture stitcher
	 * @param type
	 * @param stitcher
	 */
	public BLParticleFactory(Class<T> type, ParticleTextureStitcher<T> stitcher) {
		this.stitcher = stitcher;
		this.type = type;
	}

	/**
	 * Returns the particle texture stitcher
	 * @return
	 */
	public ParticleTextureStitcher<? extends Particle> getStitcher() {
		return this.stitcher;
	}

	/**
	 * Returns the particle type
	 * @return
	 */
	public Class<T> getType() {
		return this.type;
	}

	/**
	 * Creates a new particle from the immutable particle args and sets the sprites
	 * @param args
	 * @return
	 */
	public final Particle getParticle(ImmutableParticleArgs args) {
		Particle particle = this.createParticle(args);
		if(IParticleSpriteReceiver.class.isAssignableFrom(particle.getClass())) {
			((IParticleSpriteReceiver)particle).setStitchedSprites(this.getStitcher().getSprites());
		}
		return particle;
	}

	/**
	 * Creates a new particle from the immutable particle args
	 * @param args
	 * @return
	 */
	protected abstract Particle createParticle(ImmutableParticleArgs args);

	/**
	 * Specifies an additional argument as empty. Any empty argument will be replaced by the 
	 * default argument or null if a default argument is not available.
	 */
	public static final Object EMPTY_ARG = new Object();

	/**
	 * Sets the default arguments
	 * @param args
	 */
	protected void setDefaultArguments(ParticleArgs args) { }

	/**
	 * Returns the composed particle arguments
	 * @param custom
	 * @return
	 */
	public final ParticleArgs getArguments(ParticleArgs custom) {
		if(this.defaults == null) {
			this.defaults = new ParticleArgs();
			this.setDefaultArguments(this.defaults);
		}

		if(!custom.isMotionSet()) {
			custom.withMotion(this.defaults.getMotionX(), this.defaults.getMotionY(), this.defaults.getMotionZ());
		}
		if(!custom.isColorSet()) {
			custom.withColor(this.defaults.getColor());
		}
		if(!custom.isScaleSet()) {
			custom.withScale(this.defaults.getScale());
		}
		if(this.defaults.isDataSet()) {
			Object[] customAdditionalArgs = custom.getData();
			Object[] defaultArgs = this.defaults.getData();
			if(defaultArgs.length > customAdditionalArgs.length) {
				custom.withData(new Object[defaultArgs.length]);
			}
			Object[] additionalArgs = custom.getData();
			if(additionalArgs.length == this.defaults.getData().length) {
				for(int i = 0; i < additionalArgs.length; i++) {
					if(i < customAdditionalArgs.length) {
						if(customAdditionalArgs[i] == EMPTY_ARG) {
							additionalArgs[i] = defaultArgs[i];
						} else {
							additionalArgs[i] = customAdditionalArgs[i];
						}
					} else if(i < defaultArgs.length) {
						additionalArgs[i] = defaultArgs[i];
					}
				}
			}
		}
		return custom;
	}
}

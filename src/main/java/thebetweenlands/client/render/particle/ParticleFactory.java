package thebetweenlands.client.render.particle;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;
import thebetweenlands.client.render.particle.ParticleTextureStitcher.IParticleSpriteReceiver;

public abstract class ParticleFactory<F extends ParticleFactory<?, T>, T extends Particle> {
	/**
	 * Data helper for additional data of Particles
	 */
	public static final class DataHelper {
		private final Object[] data;

		private DataHelper(Object[] data) {
			this.data = data;
		}

		/**
		 * Returns all data in a copy of the array
		 * @return
		 */
		public Object[] getAll() {
			Object[] arr = new Object[this.data.length];
			for(int i = 0; i < arr.length; i++)
				arr[i] = this.data[i];
			return arr;
		}

		/**
		 * Returns the object at the specified index.
		 * <p><b>Do note that using primitives will not work.
		 * Use their respective helper methods instead.</b>
		 * @param type Type of the object
		 * @param index
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <T> T getObject(Class<T> type, int index) {
			return (T)this.data[index];
		}

		/**
		 * Returns the object array at the specified index.
		 * <p><b>Do note that using primitives will not work. 
		 * Use their respective helper methods instead.</b>
		 * @param type Type of the object array
		 * @param index
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public <T> T[] getObjectArray(Class<T> type, int index) {
			return (T[])this.data[index];
		}

		/**
		 * Returns the object at the specified index
		 * @param index
		 * @return
		 */
		public Object getObject(int index) {
			return this.getObject(Object.class, index);
		}

		/**
		 * Returns the object array at the specified index
		 * @param index
		 * @return
		 */
		public Object[] getObjectArray(int index) {
			return this.getObjectArray(Object.class, index);
		}

		/**
		 * Returns the byte at the specified index
		 * @param index
		 * @return
		 */
		public byte getByte(int index) {
			return this.getObject(Number.class, index).byteValue();
		}

		/**
		 * Returns the short at the specified index
		 * @param index
		 * @return
		 */
		public short getShort(int index) {
			return this.getObject(Number.class, index).shortValue();
		}

		/**
		 * Returns the int at the specified index
		 * @param index
		 * @return
		 */
		public int getInt(int index) {
			return this.getObject(Number.class, index).intValue();
		}

		/**
		 * Returns the long at the specified index
		 * @param index
		 * @return
		 */
		public long getLong(int index) {
			return this.getObject(Number.class, index).longValue();
		}

		/**
		 * Returns the float at the specified index
		 * @param index
		 * @return
		 */
		public float getFloat(int index) {
			return this.getObject(Number.class, index).floatValue();
		}

		/**
		 * Returns the double at the specified index
		 * @param index
		 * @return
		 */
		public double getDouble(int index) {
			return this.getObject(Number.class, index).doubleValue();
		}

		/**
		 * Returns the boolean at the specified index
		 * @param index
		 * @return
		 */
		public boolean getBool(int index) {
			return this.getObject(Boolean.class, index).booleanValue();
		}

		/**
		 * Returns the char at the specified index
		 * @param index
		 * @return
		 */
		public char getChar(int index) {
			return this.getObject(Character.class, index).charValue();
		}

		/**
		 * Returns the byte array at the specified index
		 * @param index
		 * @return
		 */
		public byte[] getByteArray(int index) {
			return (byte[])this.getObject(index);
		}

		/**
		 * Returns the short array at the specified index
		 * @param index
		 * @return
		 */
		public short[] getShortArray(int index) {
			return (short[])this.getObject(index);
		}

		/**
		 * Returns the int array at the specified index
		 * @param index
		 * @return
		 */
		public int[] getIntArray(int index) {
			return (int[])this.getObject(index);
		}

		/**
		 * Returns the long array at the specified index
		 * @param index
		 * @return
		 */
		public long[] getLongArray(int index) {
			return (long[])this.getObject(index);
		}

		/**
		 * Returns the float array at the specified index
		 * @param index
		 * @return
		 */
		public float[] getFloatArray(int index) {
			return (float[])this.getObject(index);
		}

		/**
		 * Returns the double array at the specified index
		 * @param index
		 * @return
		 */
		public double[] getDoubleArray(int index) {
			return (double[])this.getObject(index);
		}

		/**
		 * Returns the boolean array at the specified index
		 * @param index
		 * @return
		 */
		public boolean[] getBoolArray(int index) {
			return (boolean[])this.getObject(index);
		}

		/**
		 * Returns the char array at the specified index
		 * @param index
		 * @return
		 */
		public char[] getCharArray(int index) {
			return (char[])this.getObject(index);
		}
	}

	/**
	 * Immutable particle arguments
	 */
	public static class ImmutableParticleArgs {
		public final World world;
		public final double x, y, z, motionX, motionY, motionZ;
		public final float scale;
		public final float r, g, b, a;
		public final DataHelper data;

		public ImmutableParticleArgs(World world, double x, double y, double z, ParticleArgs<?> builder) {
			this.world = world;
			this.x = x;
			this.y = y;
			this.z = z;
			this.motionX = builder.motionX;
			this.motionY = builder.motionY;
			this.motionZ = builder.motionZ;
			this.scale = builder.scale;
			this.r = builder.r;
			this.g = builder.g;
			this.b = builder.b;
			this.a = builder.a;
			this.data = new DataHelper(builder.data);
		}
	}

	/**
	 * Particle arguments
	 */
	public static class ParticleArgs<T extends ParticleArgs<?>> implements Consumer<ParticleArgs<?>> {
		public static final class ArgumentDataBuilder<T extends ParticleArgs<?>> {
			private int highestIndex = 0;
			private final Map<Integer, Object> setArgs;
			private final T args;

			/**
			 * Creates a new argument data builder.
			 * @param args
			 */
			private ArgumentDataBuilder(T args) {
				this.setArgs = new HashMap<Integer, Object>();
				this.args = args;
			}

			/**
			 * Sets the data at the specified index
			 * @param index
			 * @param data
			 * @return
			 */
			public ArgumentDataBuilder<T> setData(int index, Object... data) {
				if(index + data.length - 1 > this.highestIndex)
					this.highestIndex = index + data.length - 1;
				for(int i = 0; i < data.length; i++)
					this.setArgs.put(index + i, data[i]);
				return this;
			}

			/**
			 * Sets the argument at the specified index to {@link ParticleFactory#EMPTY_ARG}
			 * @param index
			 * @return
			 */
			public ArgumentDataBuilder<T> setEmpty(int index) {
				return this.setData(index, EMPTY_ARG);
			}

			/**
			 * Builds the additional arguments and adds them
			 * @return
			 */
			public T buildData() {
				Object[] data = new Object[this.highestIndex + 1];
				for(int i = 0; i < data.length; i++) {
					data[i] = EMPTY_ARG;
				}
				Iterator<Entry<Integer, Object>> dataIT = this.setArgs.entrySet().iterator();
				Entry<Integer, Object> entry;
				while(dataIT.hasNext()) {
					entry = dataIT.next();
					data[entry.getKey()] = entry.getValue();
				}
				this.args.withData(data);
				return (T) this.args;
			}
		}

		private static final ThreadLocal<ParticleArgs<?>> BUILDERS = ThreadLocal.withInitial(() -> ParticleArgs.create());

		private static final Object[] NO_DATA = new Object[0];

		private boolean motionSet = false;
		private double motionX, motionY, motionZ;
		private boolean scaleSet = false;
		private float scale;
		private boolean colorSet = false;
		private float r, g, b, a;
		private Object[] data;
		private boolean dataSet = false;

		private ParticleArgs<?> container;

		private ParticleArgs() {
			this.reset();
		}

		private static <F extends ParticleArgs<?>> ParticleArgs<F> create() {
			return new ParticleArgs<F>();
		}

		/**
		 * Creates a copy of the specified particle args
		 * @param args
		 */
		private ParticleArgs(ParticleArgs<?> args) {
			if(args == null)
				throw new NullPointerException("Particle args to copy must not be null");
			this.motionSet = args.motionSet;
			this.motionX = args.motionX;
			this.motionY = args.motionY;
			this.motionZ = args.motionZ;
			this.scaleSet = args.scaleSet;
			this.scale = args.scale;
			this.colorSet = args.colorSet;
			this.r = args.r;
			this.g = args.g;
			this.b = args.b;
			this.a = args.a;
			if(args.data == NO_DATA || args.data.length == 0) {
				this.data = NO_DATA;
			} else {
				this.data = new Object[args.data.length];
				for(int i = 0; i < this.data.length; i++)
					this.data[i] = args.data[i];
			}
			this.dataSet = args.dataSet;
		}

		/**
		 * Creates a copy of the specified particle args
		 * @param args
		 */
		public static <F extends ParticleArgs<?>> ParticleArgs<F> copy(ParticleArgs<?> args) {
			return new ParticleArgs<F>(args);
		}

		/**
		 * Resets all values
		 */
		public final void reset() {
			this.motionX = 0;
			this.motionY = 0;
			this.motionZ = 0;
			this.scale = 1;
			this.r = this.g = this.b = this.a = 1.0F;
			this.data = NO_DATA;
			this.dataSet = false;
			this.motionSet = false;
			this.scaleSet = false;
			this.colorSet = false;
			this.resetContainer();
		}

		/**
		 * Resets the container values
		 */
		protected final void resetContainer() {
			if(this.container != null) {
				this.container.motionX = this.motionX;
				this.container.motionY = this.motionY;
				this.container.motionZ = this.motionZ;
				this.container.scale = this.scale;
				this.container.r = this.r;
				this.container.g = this.g;
				this.container.b = this.b;
				this.container.a = this.a;
				this.container.data = this.data;
				this.container.dataSet = this.dataSet;
				this.container.motionSet = this.motionSet;
				this.container.scaleSet = this.scaleSet;
				this.container.colorSet = this.colorSet;
			}
		}

		/**
		 * Runs a {@link Consumer} over this {@link ParticleArgs}
		 * @param consumer
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final T accept(Consumer<ParticleArgs<?>> consumer) {
			consumer.accept(this);
			return (T) this;
		}

		/**
		 * Sets the motion values
		 * @param motionX
		 * @param motionY
		 * @param motionZ
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final T withMotion(double motionX, double motionY, double motionZ) {
			this.motionX = motionX;
			this.motionY = motionY;
			this.motionZ = motionZ;
			this.motionSet = true;
			return (T) this;
		}

		/**
		 * Sets the scale
		 * @param scale
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final T withScale(float scale) {
			this.scale = scale;
			this.scaleSet = true;
			return (T) this;
		}

		/**
		 * Sets the RGBA color
		 * @param r
		 * @param g
		 * @param b
		 * @param a
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final T withColor(float r, float g, float b, float a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
			this.colorSet = true;
			return (T) this;
		}

		/**
		 * Sets the RGBA color
		 * @param color
		 * @return
		 */
		public final T withColor(int color) {
			return this.withColor((color >> 16 & 0xff) / 255F, (color >> 8 & 0xff) / 255F, (color & 0xff) / 255F, (color >> 24 & 0xff) / 255F);
		}

		/**
		 * Sets the RGBA color
		 * @param color
		 * @return
		 */
		public final T withColor(float[] color) {
			return this.withColor(color[0], color[1], color[2], color[3]);
		}

		/**
		 * Sets the additional data.
		 * Must not be longer than the additional arguments of the underlying default.
		 * @param data
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final T withData(Object... data) {
			if(data == null || data.length == 0) 
				data = NO_DATA;
			this.data = data;
			this.dataSet = true;
			return (T) this;
		}

		/**
		 * Returns an arguments builder
		 * @param size
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final ArgumentDataBuilder<T> withDataBuilder() {
			return new ArgumentDataBuilder<T>((T) this);
		}

		/**
		 * Returns the motion X
		 * @return
		 */
		public final double getMotionX() {
			return this.motionX;
		}

		/**
		 * Returns the motion Y
		 * @return
		 */
		public final double getMotionY() {
			return this.motionY;
		}

		/**
		 * Returns the motion Z
		 * @return
		 */
		public final double getMotionZ() {
			return this.motionZ;
		}

		/**
		 * Returns whether the motion was set
		 * @return
		 */
		public final boolean isMotionSet() {
			return this.motionSet;
		}

		/**
		 * Returns the scale
		 * @return
		 */
		public final float getScale() {
			return this.scale;
		}

		/**
		 * Returns whether the scale was set
		 * @return
		 */
		public final boolean isScaleSet() {
			return this.scaleSet;
		}

		/**
		 * Returns the RGBA color
		 * @return
		 */
		public final float[] getColor() {
			return new float[]{this.r, this.g, this.b, this.a};
		}

		/**
		 * Returns whether the color was set
		 * @return
		 */
		public final boolean isColorSet() {
			return this.colorSet;
		}

		/**
		 * Returns the additional data
		 * @return
		 */
		public final DataHelper getData() {
			return new DataHelper(this.data);
		}

		/**
		 * Returns whether the additional data was set
		 * @return
		 */
		public final boolean isDataSet() {
			return this.dataSet;
		}

		/**
		 * Returns a singleton instance of ParticleArgs
		 * @return
		 */
		public static ParticleArgs<?> get() {
			ParticleArgs<?> args = BUILDERS.get();
			args.reset();
			return args;
		}

		/**
		 * Populates the container of the specified {@link ParticleArgs} with the composed particle arguments where this {@link ParticleArgs} is the underlying default.
		 * The additional data of the specified {@link ParticleArgs} must have a length equal or smaller than the length of the additional data of this {@link ParticleArgs}.
		 * @param container Arguments to combine with
		 * @return The container with the combined args
		 */
		protected final ParticleArgs<?> combineArgs(ParticleArgs<?> args) {
			ParticleArgs<?> container = args.container;
			if(container == null)
				container = args.container = ParticleArgs.copy(args);
			if(!container.isMotionSet()) {
				container.withMotion(this.getMotionX(), this.getMotionY(), this.getMotionZ());
			}
			if(!container.isColorSet()) {
				container.withColor(this.getColor());
			}
			if(!container.isScaleSet()) {
				container.withScale(this.getScale());
			}
			if(this.isDataSet()) {
				Object[] initialAdditionalArgs = container.data;
				Object[] defaultArgs = this.data;
				Object[] additionalArgs = container.data;
				if(defaultArgs.length > initialAdditionalArgs.length) {
					container.withData(additionalArgs = new Object[defaultArgs.length]);
				}
				for(int i = 0; i < additionalArgs.length; i++) {
					if(i < initialAdditionalArgs.length) {
						if(initialAdditionalArgs[i] == EMPTY_ARG) {
							if(i >= defaultArgs.length)
								additionalArgs[i] = null;
							else
								additionalArgs[i] = defaultArgs[i];
						} else
							additionalArgs[i] = initialAdditionalArgs[i];
					} else if(i < defaultArgs.length) {
						additionalArgs[i] = defaultArgs[i];
					}
				}
			}
			return container;
		}

		/**
		 * Populates the container of this {@link ParticleArgs} with the specified default arguments.
		 * See {@link #combineArgs(ParticleArgs)}.
		 */
		@Override
		public final void accept(ParticleArgs<?> defaultArgs) {
			defaultArgs.combineArgs(this);
		}
	}

	private final ParticleTextureStitcher<T> stitcher;
	private final Class<T> type;
	private final ParticleArgs<?> baseArgs;
	private final ParticleArgs<?> defaultArgs;

	/**
	 * Creates a new particle factory for the specified particle type
	 * @param type
	 */
	public ParticleFactory(Class<T> type) {
		this(type, null);
	}

	/**
	 * Creates a new particle factory for the specified particle and adds a particle texture stitcher
	 * @param type
	 * @param stitcher
	 */
	public ParticleFactory(Class<T> type, @Nullable ParticleTextureStitcher<T> stitcher) {
		this.stitcher = stitcher;
		this.type = type;
		this.baseArgs = ParticleArgs.create();
		this.setBaseArguments(this.baseArgs);
		this.defaultArgs = ParticleArgs.create();
	}

	/**
	 * Returns the particle texture stitcher
	 * @return
	 */
	public final ParticleTextureStitcher<? extends Particle> getStitcher() {
		return this.stitcher;
	}

	/**
	 * Returns the particle type
	 * @return
	 */
	public final Class<T> getType() {
		return this.type;
	}

	/**
	 * Creates a new particle from the immutable particle args and sets the sprites
	 * @param args
	 * @return
	 */
	protected final T getParticle(ImmutableParticleArgs args) {
		T particle = this.createParticle(args);
		if(this.getStitcher() != null) {
			((IParticleSpriteReceiver)particle).setStitchedSprites(this.getStitcher().getSprites());
		}
		particle.setRBGColorF(args.r, args.g, args.b);
		particle.setAlphaF(args.a);
		return particle;
	}

	/**
	 * Creates a new particle from the immutable particle args
	 * @param args
	 * @return
	 */
	protected abstract T createParticle(ImmutableParticleArgs args);

	/**
	 * Specifies an additional argument as empty. Any empty argument will be replaced by the 
	 * underlying default argument or null if an underlying default argument is not available. Using this in 
	 * {@link ParticleFactory#setBaseArguments(ParticleArgs)} has no effect. If this is used in 
	 * {@link ParticleFactory#setDefaultArguments(World, ParticleArgs)} the underlying default is
	 * from {@link ParticleFactory#setBaseArguments(ParticleArgs)}.
	 */
	public static final Object EMPTY_ARG = new Object();

	/**
	 * Sets the base arguments.
	 * Using {@link ParticleFactory#EMPTY_ARG} has no effect.
	 * @param args
	 */
	protected void setBaseArguments(ParticleArgs<?> args) { }

	/**
	 * Sets the default arguments based on the world and position the particle is being spawned at.
	 * The underlying default arguments are set by {@link ParticleFactory#setBaseArguments(ParticleArgs)}.
	 * Overrides arguments set by {@link ParticleFactory#setBaseArguments(ParticleArgs)}.
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 */
	protected void setDefaultArguments(World world, double x, double y, double z, ParticleArgs<?> args) { }

	public static final class BaseArgsBuilder<F extends ParticleFactory<?, ?>, B extends ParticleArgs<?>, C extends Particle> extends ParticleArgs<B> {
		private final ParticleFactory<?, ?> factory;

		private BaseArgsBuilder(ParticleFactory<?, ?> factory) {
			super();
			this.factory = factory;
		}

		private static <F extends ParticleFactory<?, ?>, B extends ParticleArgs<?>, C extends Particle> BaseArgsBuilder<F, B, C> create(ParticleFactory<?, ?> factory) {
			return new BaseArgsBuilder<F, B, C>(factory);
		}

		/**
		 * Builds the arguments and sets the base arguments of the factory if overwritten
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final F buildBaseArgs() {
			ParticleArgs<?> container = ParticleArgs.create();
			//Fill with arguments from the builder
			container = this.combineArgs(container);
			//Fill with base arguments from factory
			container = this.factory.baseArgs.combineArgs(container);
			if(container.isMotionSet())
				this.factory.baseArgs.withMotion(container.getMotionX(), container.getMotionY(), container.getMotionZ());
			if(container.isColorSet())
				this.factory.baseArgs.withColor(container.getColor());
			if(container.isScaleSet())
				this.factory.baseArgs.withScale(container.getScale());
			if(container.isDataSet()) {
				this.factory.baseArgs.withData(container.data);
			}
			return (F) this.factory;
		}
	}

	private final BaseArgsBuilder<F, BaseArgsBuilder<F, ?, T>, T> baseArgsBuilder = BaseArgsBuilder.create(this);

	/**
	 * Returns the base arguments builder.
	 * Used to override base arguments set by {@link ParticleFactory#setBaseArguments(ParticleArgs)}.
	 * @return
	 */
	public BaseArgsBuilder<F, BaseArgsBuilder<F, ?, T>, T> getBaseArgsBuilder(){
		this.baseArgsBuilder.reset();
		return this.baseArgsBuilder;
	}

	/**
	 * Creates an instance of a particle.
	 * The specified {@link ParticleArgs} overrides the default arguments set by {@link ParticleFactory#setDefaultArguments(World, ParticleArgs)}
	 * @param type
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 * @return
	 */
	public final T create(World world, double x, double y, double z, @Nullable ParticleArgs<?> args) {
		if(args == null)
			args = ParticleArgs.get();
		this.defaultArgs.reset();
		this.setDefaultArguments(world, x, y, z, this.defaultArgs);
		args.resetContainer();
		this.defaultArgs.resetContainer();
		boolean hasActualDefaults = this.defaultArgs.isColorSet() || this.defaultArgs.isMotionSet() || this.defaultArgs.isScaleSet() || this.defaultArgs.isDataSet();
		if(hasActualDefaults) {
			args = this.baseArgs.combineArgs(this.defaultArgs).combineArgs(args);
		} else {
			args = this.baseArgs.combineArgs(args);
		}
		return this.getParticle(new ImmutableParticleArgs(world, x, y, z, args));
	}

	/**
	 * Spawns a particle.
	 * The specified {@link ParticleArgs} overrides the default arguments set by {@link ParticleFactory#setDefaultArguments(World, ParticleArgs)}
	 * @param type
	 * @param world
	 * @param x
	 * @param y
	 * @param z
	 * @param args
	 * @return
	 */
	public final T spawn(World world, double x, double y, double z, @Nullable ParticleArgs<?> args) {
		T particle = this.create(world, x, y, z, args);
		if(particle != null)
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		return particle;
	}
}

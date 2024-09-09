package thebetweenlands.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class ParticleFactory<F extends ParticleFactory<?, T>, T extends ParticleOptions> implements ParticleProvider<T> {

	public static final class DataHelper {
		private final Object[] data;

		private DataHelper(Object[] data) {
			this.data = data;
		}

		public Object[] getAll() {
			Object[] arr = new Object[this.data.length];
			System.arraycopy(this.data, 0, arr, 0, arr.length);
			return arr;
		}

		@SuppressWarnings("unchecked")
		public <T> T getObject(Class<T> type, int index) {
			return (T) this.data[index];
		}

		@SuppressWarnings("unchecked")
		public <T> T[] getObjectArray(Class<T> type, int index) {
			return (T[]) this.data[index];
		}

		public Object getObject(int index) {
			return this.getObject(Object.class, index);
		}

		public Object[] getObjectArray(int index) {
			return this.getObjectArray(Object.class, index);
		}

		public byte getByte(int index) {
			return this.getObject(Number.class, index).byteValue();
		}

		public short getShort(int index) {
			return this.getObject(Number.class, index).shortValue();
		}

		public int getInt(int index) {
			return this.getObject(Number.class, index).intValue();
		}

		public long getLong(int index) {
			return this.getObject(Number.class, index).longValue();
		}

		public float getFloat(int index) {
			return this.getObject(Number.class, index).floatValue();
		}

		public double getDouble(int index) {
			return this.getObject(Number.class, index).doubleValue();
		}

		public boolean getBool(int index) {
			return this.getObject(Boolean.class, index);
		}

		public char getChar(int index) {
			return this.getObject(Character.class, index);
		}

		public byte[] getByteArray(int index) {
			return (byte[]) this.getObject(index);
		}

		public short[] getShortArray(int index) {
			return (short[]) this.getObject(index);
		}

		public int[] getIntArray(int index) {
			return (int[]) this.getObject(index);
		}

		public long[] getLongArray(int index) {
			return (long[]) this.getObject(index);
		}

		public float[] getFloatArray(int index) {
			return (float[]) this.getObject(index);
		}

		public double[] getDoubleArray(int index) {
			return (double[]) this.getObject(index);
		}

		public boolean[] getBoolArray(int index) {
			return (boolean[]) this.getObject(index);
		}

		public char[] getCharArray(int index) {
			return (char[]) this.getObject(index);
		}
	}

	public static class ImmutableParticleArgs {
		public final ClientLevel level;
		public final double x, y, z, motionX, motionY, motionZ;
		public final float scale;
		public final float r, g, b, a;
		public final DataHelper data;

		public ImmutableParticleArgs(ClientLevel level, double x, double y, double z, ParticleArgs<?> builder) {
			this.level = level;
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

	public static class ParticleArgs<T extends ParticleArgs<?>> implements Consumer<ParticleArgs<?>> {
		public static final class ArgumentDataBuilder<T extends ParticleArgs<?>> {
			private int highestIndex = 0;
			private final Map<Integer, Object> setArgs;
			private final T args;

			private ArgumentDataBuilder(T args) {
				this.setArgs = new HashMap<>();
				this.args = args;
			}

			public ArgumentDataBuilder<T> setData(int index, Object... data) {
				if (index + data.length - 1 > this.highestIndex)
					this.highestIndex = index + data.length - 1;
				for (int i = 0; i < data.length; i++)
					this.setArgs.put(index + i, data[i]);
				return this;
			}

			public ArgumentDataBuilder<T> setEmpty(int index) {
				return this.setData(index, EMPTY_ARG);
			}

			public T buildData() {
				Object[] data = new Object[this.highestIndex + 1];
				Arrays.fill(data, EMPTY_ARG);
				Iterator<Map.Entry<Integer, Object>> dataIT = this.setArgs.entrySet().iterator();
				Map.Entry<Integer, Object> entry;
				while (dataIT.hasNext()) {
					entry = dataIT.next();
					data[entry.getKey()] = entry.getValue();
				}
				this.args.withData(data);
				return this.args;
			}
		}

		private static final ThreadLocal<ParticleArgs<?>> BUILDERS = ThreadLocal.withInitial(ParticleArgs::create);

		private static final Object[] NO_DATA = new Object[0];

		private boolean motionSet = false;
		private double motionX, motionY, motionZ;
		private boolean scaleSet = false;
		private float scale;
		private boolean colorSet = false;
		private float r, g, b, a;
		private Object[] data;
		private boolean dataSet = false;

		@Nullable
		private ParticleArgs<?> container;

		private ParticleArgs() {
			this.reset();
		}

		private static <F extends ParticleArgs<?>> ParticleArgs<F> create() {
			return new ParticleArgs<>();
		}

		private ParticleArgs(ParticleArgs<?> args) {
			if (args == null)
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
			if (args.data == NO_DATA || args.data.length == 0) {
				this.data = NO_DATA;
			} else {
				this.data = new Object[args.data.length];
				for (int i = 0; i < this.data.length; i++)
					this.data[i] = args.data[i];
			}
			this.dataSet = args.dataSet;
		}

		public static <F extends ParticleArgs<?>> ParticleArgs<F> copy(ParticleArgs<?> args) {
			return new ParticleArgs<F>(args);
		}

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

		protected final void resetContainer() {
			if (this.container != null) {
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

		@SuppressWarnings("unchecked")
		public final T accept(Consumer<ParticleArgs<?>> consumer) {
			consumer.accept(this);
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public final T withMotion(double motionX, double motionY, double motionZ) {
			this.motionX = motionX;
			this.motionY = motionY;
			this.motionZ = motionZ;
			this.motionSet = true;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public final T withScale(float scale) {
			this.scale = scale;
			this.scaleSet = true;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public final T withColor(float r, float g, float b, float a) {
			this.r = r;
			this.g = g;
			this.b = b;
			this.a = a;
			this.colorSet = true;
			return (T) this;
		}

		public final T withColor(int color) {
			return this.withColor((color >> 16 & 0xff) / 255F, (color >> 8 & 0xff) / 255F, (color & 0xff) / 255F, (color >> 24 & 0xff) / 255F);
		}

		public final T withColor(float[] color) {
			return this.withColor(color[0], color[1], color[2], color[3]);
		}

		@SuppressWarnings("unchecked")
		public final T withData(Object... data) {
			if (data == null || data.length == 0)
				data = NO_DATA;
			this.data = data;
			this.dataSet = true;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		public final ArgumentDataBuilder<T> withDataBuilder() {
			return new ArgumentDataBuilder<T>((T) this);
		}

		public final double getMotionX() {
			return this.motionX;
		}

		public final double getMotionY() {
			return this.motionY;
		}

		public final double getMotionZ() {
			return this.motionZ;
		}

		public final boolean isMotionSet() {
			return this.motionSet;
		}

		public final float getScale() {
			return this.scale;
		}

		public final boolean isScaleSet() {
			return this.scaleSet;
		}

		public final float[] getColor() {
			return new float[]{this.r, this.g, this.b, this.a};
		}

		public final boolean isColorSet() {
			return this.colorSet;
		}

		public final DataHelper getData() {
			return new DataHelper(this.data);
		}

		public final boolean isDataSet() {
			return this.dataSet;
		}

		public static ParticleArgs<?> get() {
			ParticleArgs<?> args = BUILDERS.get();
			args.reset();
			return args;
		}

		protected final ParticleArgs<?> combineArgs(ParticleArgs<?> args) {
			ParticleArgs<?> container = args.container;
			if (container == null)
				container = args.container = ParticleArgs.copy(args);
			if (!container.isMotionSet()) {
				container.withMotion(this.getMotionX(), this.getMotionY(), this.getMotionZ());
			}
			if (!container.isColorSet()) {
				container.withColor(this.getColor());
			}
			if (!container.isScaleSet()) {
				container.withScale(this.getScale());
			}
			if (this.isDataSet()) {
				Object[] initialAdditionalArgs = container.data;
				Object[] defaultArgs = this.data;
				Object[] additionalArgs = container.data;
				if (defaultArgs.length > initialAdditionalArgs.length) {
					container.withData(additionalArgs = new Object[defaultArgs.length]);
				}
				for (int i = 0; i < additionalArgs.length; i++) {
					if (i < initialAdditionalArgs.length) {
						if (initialAdditionalArgs[i] == EMPTY_ARG) {
							if (i >= defaultArgs.length)
								additionalArgs[i] = null;
							else
								additionalArgs[i] = defaultArgs[i];
						} else
							additionalArgs[i] = initialAdditionalArgs[i];
					} else {
						additionalArgs[i] = defaultArgs[i];
					}
				}
			}
			return container;
		}

		@Override
		public final void accept(ParticleArgs<?> defaultArgs) {
			defaultArgs.combineArgs(this);
		}
	}

	private final ParticleArgs<?> baseArgs;
	private final ParticleArgs<?> defaultArgs;

	public ParticleFactory() {
		this.baseArgs = ParticleArgs.create();
		this.setBaseArguments(this.baseArgs);
		this.defaultArgs = ParticleArgs.create();
	}

	protected final Particle getParticle(ImmutableParticleArgs args) {
		Particle particle = this.createParticle(args);
		particle.setColor(args.r, args.g, args.b);
		particle.setAlpha(args.a);
		return particle;
	}

	@Nullable
	@Override
	public Particle createParticle(ParticleOptions type, ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
		return this.createParticle(new ImmutableParticleArgs(level, x, y, z, ParticleArgs.create()));
	}

	protected abstract Particle createParticle(ImmutableParticleArgs args);

	public static final Object EMPTY_ARG = new Object();

	protected void setBaseArguments(ParticleArgs<?> args) {
	}

	protected void setDefaultArguments(ClientLevel level, double x, double y, double z, ParticleArgs<?> args) {
	}

	public static final class BaseArgsBuilder<F extends ParticleFactory<?, ?>, B extends ParticleArgs<?>, C extends ParticleOptions> extends ParticleArgs<B> {
		private final ParticleFactory<?, ?> factory;

		private BaseArgsBuilder(ParticleFactory<?, ?> factory) {
			super();
			this.factory = factory;
		}

		private static <F extends ParticleFactory<?, ?>, B extends ParticleArgs<?>, C extends ParticleOptions> BaseArgsBuilder<F, B, C> create(ParticleFactory<?, ?> factory) {
			return new BaseArgsBuilder<F, B, C>(factory);
		}

		@SuppressWarnings("unchecked")
		public final F buildBaseArgs() {
			ParticleArgs<?> container = ParticleArgs.create();
			//Fill with arguments from the builder
			container = this.combineArgs(container);
			//Fill with base arguments from factory
			container = this.factory.baseArgs.combineArgs(container);
			if (container.isMotionSet())
				this.factory.baseArgs.withMotion(container.getMotionX(), container.getMotionY(), container.getMotionZ());
			if (container.isColorSet())
				this.factory.baseArgs.withColor(container.getColor());
			if (container.isScaleSet())
				this.factory.baseArgs.withScale(container.getScale());
			if (container.isDataSet()) {
				this.factory.baseArgs.withData(container.data);
			}
			return (F) this.factory;
		}
	}

	private final BaseArgsBuilder<F, BaseArgsBuilder<F, ?, T>, T> baseArgsBuilder = BaseArgsBuilder.create(this);

	public BaseArgsBuilder<F, BaseArgsBuilder<F, ?, T>, T> getBaseArgsBuilder() {
		this.baseArgsBuilder.reset();
		return this.baseArgsBuilder;
	}

	public final Particle create(ClientLevel level, double x, double y, double z, @Nullable ParticleArgs<?> args) {
		if (args == null)
			args = ParticleArgs.get();
		this.defaultArgs.reset();
		this.setDefaultArguments(level, x, y, z, this.defaultArgs);
		args.resetContainer();
		this.defaultArgs.resetContainer();
		boolean hasActualDefaults = this.defaultArgs.isColorSet() || this.defaultArgs.isMotionSet() || this.defaultArgs.isScaleSet() || this.defaultArgs.isDataSet();
		if (hasActualDefaults) {
			args = this.baseArgs.combineArgs(this.defaultArgs).combineArgs(args);
		} else {
			args = this.baseArgs.combineArgs(args);
		}
		return this.getParticle(new ImmutableParticleArgs(level, x, y, z, args));
	}
}

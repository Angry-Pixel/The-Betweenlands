package thebetweenlands.common.world.gen.layer.old.util;

import java.util.concurrent.atomic.AtomicLong;

import com.google.common.annotations.VisibleForTesting;

import net.minecraft.util.LinearCongruentialGenerator;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.ThreadingDetector;
import net.minecraft.world.level.levelgen.BitRandomSource;
import net.minecraft.world.level.levelgen.MarsagliaPolarGaussian;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;

/**
 * A {@linkplain BitRandomSource} implementation that uses {@linkplain LinearCongruentialGenerator} to calculate its next values
 */
public class LinearCongruentialRandomSource implements BitRandomSource {
	// Used as a constant modifier applied to the random value
	public final AtomicLong modifier = new AtomicLong();
	// The seed
	public final AtomicLong seed = new AtomicLong();
	// Gaussian source
	private final MarsagliaPolarGaussian gaussianSource = new MarsagliaPolarGaussian(this);

	public LinearCongruentialRandomSource(long seed) {
		this.setSeed(seed);
	}

	public LinearCongruentialRandomSource(long seed, long modifier) {
		this.setSeed(seed, modifier);
	}
	
	@Override
	public RandomSource fork() {
		return new LinearCongruentialRandomSource(this.nextLong());
	}

	@Override
	public PositionalRandomFactory forkPositional() {
		return new LinearCongruentialPositionalRandomFactory(this.nextLong(), this.modifier.get());
	}

	@Override
	public int next(int size) {
		final long originalSeed = this.seed.get();
		
		final long nextSeed = LinearCongruentialGenerator.next(originalSeed, this.modifier.get());
		
		if (!this.seed.compareAndSet(originalSeed, nextSeed)) {
			throw ThreadingDetector.makeThreadingException("LinearCongruentialRandomSource", null);
		} else {
			return (int)(originalSeed >> 56 - size);
		}
	}
	
	@Override
	public void setSeed(long seed) {
		final long originalSeed = this.seed.get();
		final long originalModifier = this.modifier.get();

		// TODO fix threadedness - we need to swap out both the new seed and new modifier in one operation
		if(!this.seed.compareAndSet(originalSeed, seed * 6364136223846793005L + 1442695040888963407L) || !this.modifier.compareAndSet(originalModifier, seed)) {
			throw ThreadingDetector.makeThreadingException("LinearCongruentialRandomSource", null);
		} else {
			this.gaussianSource.reset();
		}
	}

	public void setSeed(long seed, long modifier) {
		final long originalSeed = this.seed.get();
		final long originalModifier = this.modifier.get();

		// TODO fix threadedness - we need to swap out both the new seed and new modifier in one operation
		if(!this.seed.compareAndSet(originalSeed, seed) || !this.modifier.compareAndSet(originalModifier, modifier)) {
			throw ThreadingDetector.makeThreadingException("LinearCongruentialRandomSource", null);
		} else {
			this.gaussianSource.reset();
		}
	}
	
	public void setSeed(long seed, long modifier, long pX, long pZ) {
		final long originalSeed = this.seed.get();
		final long originalModifier = this.modifier.get();
		
		long fiddledSeed = seed;
		fiddledSeed = LinearCongruentialGenerator.next(fiddledSeed, pX);
		fiddledSeed = LinearCongruentialGenerator.next(fiddledSeed, pZ);
		fiddledSeed = LinearCongruentialGenerator.next(fiddledSeed, pX);
		fiddledSeed = LinearCongruentialGenerator.next(fiddledSeed, pZ);

		// TODO fix threadedness - we need to swap out both the new seed and new modifier in one operation
		if(!this.seed.compareAndSet(originalSeed, fiddledSeed) || !this.modifier.compareAndSet(originalModifier, modifier)) {
			throw ThreadingDetector.makeThreadingException("LinearCongruentialRandomSource", null);
		} else {
			this.gaussianSource.reset();
		}
	}

	@Override
	public double nextGaussian() {
		return this.gaussianSource.nextGaussian();
	}
	
	public static class LinearCongruentialPositionalRandomFactory implements PositionalRandomFactory {
		private final long modifier;
		private final long seed;

		public LinearCongruentialPositionalRandomFactory(long seed, long modifier) {
			this.seed = seed;
			this.modifier = modifier;
		}

		@Override
		public RandomSource at(int x, int y, int z) {
			@SuppressWarnings("deprecation")
			long positionalModifier = LinearCongruentialGenerator.next(Mth.getSeed(x, y, z), this.modifier);
			long seed = LinearCongruentialGenerator.next(this.seed, positionalModifier);
			return new LinearCongruentialRandomSource(seed, this.modifier);
		}

		@Override
		public RandomSource fromHashOf(String name) {
			long positionalModifier = LinearCongruentialGenerator.next(name.hashCode(), this.modifier);
			long seed = LinearCongruentialGenerator.next(this.seed, positionalModifier);
			return new LinearCongruentialRandomSource(seed, this.modifier);
		}

		@Override
		public RandomSource fromSeed(long seed) {
			return new LinearCongruentialRandomSource(seed, this.modifier);
		}

		@VisibleForTesting
		@Override
		public void parityConfigString(StringBuilder builder) {
			builder.append("LinearCongruentialPositionalRandomFactory{").append(this.seed).append(",").append(this.modifier).append("}");
		}
	}
}

package thebetweenlands.compat;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import com.google.common.base.Predicates;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import thebetweenlands.common.TheBetweenlands;

public interface IBetweenlandsModCompat {
	VersionRange ALL_VERSIONS = createVersionRangeFromSpec("*");

	static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	static Optional<ArtifactVersion> getModVersion(String modid) {
		if(!isModLoaded(modid)) {
			return Optional.empty();
		} else {
			return ModList.get()
					.getModContainerById(modid)
					.map(container -> container.getModInfo().getVersion());
		}
	}

	static VersionRange createVersionRangeFromSpec(String spec) {
		try {
			return VersionRange.createFromVersionSpec(spec);
		} catch (InvalidVersionSpecificationException e) {
			throw new RuntimeException(e);
		}
	}

	static boolean testVersionRange(VersionRange range, ArtifactVersion version) {
		if(version == null) return false;
		if(range == null) {
			TheBetweenlands.LOGGER.warn("Unexpected null range in testVersionRange(range, version)!");
			Thread.dumpStack(); // Print stacktrace
			return false;
		}
		return range == ALL_VERSIONS || range.containsVersion(version);
	}

	static <T extends IBetweenlandsModCompat> Optional<T> getService(Class<T> compatibilityClass, String modid) {
		return getModVersion(modid)
				.map(
						version -> getService(compatibilityClass, version)
					)
				.orElseGet(
						() -> getService(compatibilityClass)
					);
	}

	static <T extends IBetweenlandsModCompat> Optional<T> getService(Class<T> compatibilityClass, ArtifactVersion version) {
		return getService(compatibilityClass, instance -> testVersionRange(instance.supportedModVersions(), version));
	}

	static <T extends IBetweenlandsModCompat> Optional<T> getService(Class<T> compatibilityClass) {
		return getService(compatibilityClass, Predicates.alwaysTrue());
	}

	static <T extends IBetweenlandsModCompat> Optional<T> getService(Class<T> compatibilityClass, Predicate<T> filter) {
		final ServiceLoader<T> loader = ServiceLoader.load(compatibilityClass);

		int priority = Integer.MIN_VALUE;
		T service = null;

		for(T instance : loader) {
			if(filter.test(instance) && (instance.handlerPriority() > priority || service == null)) {
				service = instance;
				priority = instance.handlerPriority();
			}
		}

		return Optional.ofNullable(service);
	}

	String getModId();

	default ResourceLocation location(String path) {
		return ResourceLocation.fromNamespaceAndPath(this.getModId(), path);
	}

	default boolean isModLoaded() {
		return isModLoaded(this.getModId());
	}

	default Optional<ArtifactVersion> getModVersion() {
		return getModVersion(this.getModId());
	}

	@Nonnull
	VersionRange supportedModVersions();

	default int handlerPriority() {
		return 0;
	}

	default boolean isFallbackHandler() {
		return false;
	}

	interface IFallbackModCompat extends IBetweenlandsModCompat {
		@Override
		default boolean isModLoaded() {
			return false;
		}

		@Override
        default Optional<ArtifactVersion> getModVersion() {
			return Optional.empty();
		}

		@Override
		default VersionRange supportedModVersions() {
			return ALL_VERSIONS;
		}

		@Override
		default boolean isFallbackHandler() {
			return true;
		}
	}
}

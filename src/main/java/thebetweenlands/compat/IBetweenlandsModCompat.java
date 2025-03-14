package thebetweenlands.compat;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Predicate;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import com.google.common.base.Predicates;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;

public interface IBetweenlandsModCompat {
	public static boolean isModLoaded(String modid) {
		return ModList.get().isLoaded(modid);
	}

	public static Optional<ArtifactVersion> getModVersion(String modid) {
		if(!isModLoaded(modid)) {
			return Optional.empty();
		} else {
			return ModList.get()
					.getModContainerById(modid)
					.map(container -> container.getModInfo().getVersion());
		}
	}
	
	public static VersionRange createVersionRangeFromSpec(String spec) {
		try {
			return VersionRange.createFromVersionSpec(spec);
		} catch (InvalidVersionSpecificationException e) {
			throw new RuntimeException(e);
		}
	}

	public static <T extends IBetweenlandsModCompat> Optional<T> getService(Class<T> compatibilityClass, String modid) {
		return getModVersion(modid)
				.map(
						version -> getService(compatibilityClass, version)
					)
				.orElseGet(
						() -> getService(compatibilityClass)
					);
	}

	public static <T extends IBetweenlandsModCompat> Optional<T> getService(Class<T> compatibilityClass, ArtifactVersion version) {
		return getService(compatibilityClass, instance -> instance.supportedModVersions().containsVersion(version));
	}

	public static <T extends IBetweenlandsModCompat> Optional<T> getService(Class<T> compatibilityClass) {
		return getService(compatibilityClass, Predicates.alwaysTrue());
	}
	
	public static <T extends IBetweenlandsModCompat> Optional<T> getService(Class<T> compatibilityClass, Predicate<T> filter) {
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
	
	public String getModId();
	
	public default ResourceLocation location(String path) {
		return ResourceLocation.fromNamespaceAndPath(this.getModId(), path);
	}
	
	public default boolean isModLoaded() {
		return isModLoaded(this.getModId());
	}
	
	public default Optional<ArtifactVersion> getModVersion() {
		return getModVersion(this.getModId());
	}
	
	public VersionRange supportedModVersions();
	
	public default int handlerPriority() {
		return 0;
	}
	
	public static interface Unloaded extends IBetweenlandsModCompat {
		@Override
		public default boolean isModLoaded() {
			return false;
		}

		@Override
		public default Optional<ArtifactVersion> getModVersion() {
			return Optional.empty();
		}
	}
}

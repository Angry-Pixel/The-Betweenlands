package thebetweenlands.common.config.remapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.versioning.ArtifactVersion;
import net.minecraftforge.fml.common.versioning.DefaultArtifactVersion;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.VersionRange;
import thebetweenlands.common.TheBetweenlands;

public abstract class ConfigRemapper implements Comparable<ConfigRemapper> {
	private static final Map<String, ConfigRemapper> remappers = new HashMap<>();

	/**
	 * Registers a remapper
	 * @param remapper
	 */
	public static void register(ConfigRemapper remapper) {
		remappers.put(remapper.getClass().getName(), remapper);
	}

	/**
	 * Remaps the specified config
	 * @param oldConfig Config to remap onto the new config
	 * @param newConfig New config
	 * @param version Version of the config to remap
	 * @return Returns the remapped config and version, or null if nothing was remapped
	 */
	@Nullable
	public static Pair<Configuration, String> remap(final Configuration oldConfig, final Configuration newConfig, final @Nullable String version) {
		Configuration workingConfig = oldConfig;

		try {
			String workingConfigVersionString = version == null ? "No version" : version;
			ArtifactVersion workingConfigVersion = version == null ? NO_VERSION : parseVersionRangeComparable(version);

			List<ConfigRemapper> sortedRemappers = new ArrayList<>();
			sortedRemappers.addAll(remappers.values());

			Collections.sort(sortedRemappers);

			Set<String> propertiesToRemove = new HashSet<>();

			for(int i = 0; i < sortedRemappers.size(); i++) {
				boolean remapped = false;

				remapLoop:
					for(ConfigRemapper remapper : sortedRemappers) {
						if((workingConfigVersion == NO_VERSION && remapper.acceptedVersions == NO_VERSION) || remapper.acceptedVersions.containsVersion(workingConfigVersion)) {
							TheBetweenlands.logger.info(String.format("### Config remapper '%s' (remaps to '%s') accepted config with version '%s'", remapper.getClass().getName(), remapper.outputVersion, workingConfigVersionString));

							Configuration remappedValues = new Configuration();
							if(remapConfig(remapper, workingConfig, remappedValues, propertiesToRemove)) {
								TheBetweenlands.logger.info(String.format("### Remapped config version '%s' to '%s'", workingConfigVersionString, remapper.outputVersion));

								removeExclusions(remappedValues, propertiesToRemove);

								workingConfig = mergeRemaps(workingConfig, remappedValues, propertiesToRemove, remapper.outputVersion);
								workingConfigVersion = parseVersionRangeComparable(remapper.outputVersion);
								workingConfigVersionString = remapper.outputVersion;

								remapped = true;
								break remapLoop;
							}
						}
					}

				if(!remapped) {
					break;
				}
			}

			if(workingConfig != oldConfig) {
				TheBetweenlands.logger.info(String.format("### Remapped values for config with version '%s':", workingConfigVersionString));
				printConfig(workingConfig);

				Configuration remappedConfig = clear(new Configuration(oldConfig.getConfigFile()));

				TheBetweenlands.logger.info("### Removing properties from remapped config:");
				Set<String> sortedPropertiesToRemove = new TreeSet<>(propertiesToRemove);
				for(String entry : sortedPropertiesToRemove) {
					TheBetweenlands.logger.info(entry);
				}

				copy(newConfig, remappedConfig, propertiesToRemove);

				TheBetweenlands.logger.info("### Stripped config:");
				printConfig(remappedConfig);

				TheBetweenlands.logger.info("### Overriding remapped properties");

				if(overrideValues(remappedConfig, workingConfig)) {
					TheBetweenlands.logger.info("### Remapped config:");
					printConfig(remappedConfig);

					return Pair.of(remappedConfig, workingConfigVersionString);
				}

				TheBetweenlands.logger.info("### No properties were overridden");
			}

			return null;
		} catch(Exception ex) {
			TheBetweenlands.logger.warn("### Failed to remap and update the Betweenlands config file", ex);
		}

		return null;
	}

	/**
	 * Copies all properties of the source config into the target config
	 * @param source
	 * @param target
	 */
	public static void copy(Configuration source, Configuration target) {
		copy(source, target, Collections.emptySet());
	}

	/**
	 * Copies all properties of the source config that are not in the exclusions into the target config.<p>
	 * The format of the exclusions is as follows:<p>
	 * <ul>
	 * <li><b>Category:</b> <i>path.to.category</i></li>
	 * <li><b>Property:</b> <i>path.to.category/property</i></li>
	 * </ul>   
	 * @param source
	 * @param target
	 */
	public static void copy(Configuration source, Configuration target, Set<String> exclusions) {
		traverseConfig(source,
				sourceCategory -> {
					if(!sourceCategory.getValues().isEmpty()) {
						String categoryName = sourceCategory.getQualifiedName();
						if(exclusions.contains(categoryName)) {
							return false;
						}
						target.getCategory(categoryName);
						return target.hasCategory(categoryName);
					}
					return false;
				},
				(sourceCategory, sourceProperty) -> {
					String categoryName = sourceCategory.getQualifiedName();
					if(!exclusions.contains(categoryName + "/" + sourceProperty.getName())) {
						target.getCategory(categoryName).put(sourceProperty.getName(), sourceProperty);
					}
				});
	}

	/**
	 * Clears the specified config
	 * @param config
	 */
	public static Configuration clear(Configuration config) {
		for(String category : config.getCategoryNames()) {
			config.removeCategory(config.getCategory(category));
		}
		return config;
	}

	private static void printConfig(Configuration config) {
		Map<String, String> flattenedStrippedConfig = new TreeMap<>();
		traverseConfig(config,
				category -> true,
				(category, property) -> {
					String key = category.getQualifiedName() + "/" + property.getName();
					String value;
					if(property.isList()) {
						value = Arrays.toString(property.getStringList());
					} else {
						value = property.getString();
					}
					flattenedStrippedConfig.put(key, value);
				});
		for(Entry<String, String> entry : flattenedStrippedConfig.entrySet()) {
			TheBetweenlands.logger.info(entry.getKey() + "=" + entry.getValue());
		}
	}

	private static void traverseConfig(Configuration config, Function<ConfigCategory, Boolean> categoryTraverser, BiConsumer<ConfigCategory, Property> propertyTraverser) {
		for(String categoryName : config.getCategoryNames()) {
			ConfigCategory category = config.getCategory(categoryName);
			traverseCategory(category, categoryTraverser, propertyTraverser);
		}
	}

	private static void traverseCategory(ConfigCategory category, Function<ConfigCategory, Boolean> categoryTraverser, BiConsumer<ConfigCategory, Property> propertyTraverser) {
		if(categoryTraverser.apply(category)) {
			for(Property property : category.getOrderedValues()) {
				propertyTraverser.accept(category, property);
			}

			for(ConfigCategory innerCategory : category.getChildren()) {
				traverseCategory(innerCategory, categoryTraverser, propertyTraverser);
			}
		}
	}

	private static void removeExclusions(Configuration config, Set<String> exclusions) {
		traverseConfig(config, 
				category -> {
					exclusions.remove(category.getQualifiedName());
					return true;
				},
				(category, property) -> {
					exclusions.remove(category.getQualifiedName() + "/" + property.getName());
				});
	}

	private static boolean remapConfig(ConfigRemapper remapper, Configuration inputConfig, Configuration remappedValues, Set<String> remappedProperties) {
		final AtomicBoolean ret = new AtomicBoolean(false);

		traverseConfig(inputConfig, 
				category -> true,
				(category, property) -> {
					String categoryName = category.getQualifiedName();
					if(remapper.remap(categoryName, property, remappedValues)) {
						remappedProperties.add(categoryName);
						remappedProperties.add(categoryName + "/" + property.getName());

						ret.set(true);

						TheBetweenlands.logger.info(String.format("Remapped property '%s'", categoryName + "/" + property.getName()));
					}
				});

		return ret.get();
	}

	private static Configuration mergeRemaps(Configuration inputConfig, Configuration remappedValues, Set<String> remappedProperties, String newVersion) {
		Configuration newConfig = new Configuration();
		copy(inputConfig, newConfig, remappedProperties);
		copy(remappedValues, newConfig, Collections.emptySet());
		return newConfig;
	}

	private static boolean overrideValues(Configuration config, Configuration remappedValues) {
		final AtomicBoolean ret = new AtomicBoolean(false);

		Map<String, Property> flattenedRemappedValues = new HashMap<>();
		traverseConfig(remappedValues,
				remappedCategory -> true,
				(remappedCategory, remappedProperty) -> flattenedRemappedValues.put(remappedCategory.getQualifiedName() + "/" + remappedProperty.getName(), remappedProperty)
				);

		traverseConfig(config,
				category -> true,
				(category, property) -> {
					Property remappedProperty = flattenedRemappedValues.get(category.getQualifiedName() + "/" + property.getName());
					if(remappedProperty != null && remappedProperty.isList() == property.isList()) {
						if(property.isList()) {
							property.set(remappedProperty.getStringList());
						} else {
							property.set(remappedProperty.getString());
						}
						ret.set(true);
					}
				});

		return ret.get();
	}

	private static ArtifactVersion parseVersionRangeSpec(String version) throws InvalidVersionSpecificationException {
		return new DefaultArtifactVersion(null, VersionRange.createFromVersionSpec(version));
	}

	private static ArtifactVersion parseVersionRangeComparable(String version) {
		return new DefaultArtifactVersion(version);
	}

	private static final ArtifactVersion NO_VERSION = new ArtifactVersion() {
		@Override
		public int compareTo(ArtifactVersion other) {
			if(other == this) {
				return 0;
			}
			return 1;
		}

		@Override
		public String getLabel() {
			return null;
		}

		@Override
		public String getVersionString() {
			return null;
		}

		@Override
		public boolean containsVersion(ArtifactVersion source) {
			return false;
		}

		@Override
		public String getRangeString() {
			return null;
		}
	};

	public final ArtifactVersion acceptedVersions;
	public final ArtifactVersion acceptedVersionsComparator;
	public final String outputVersion;

	public ConfigRemapper(@Nullable String acceptedVersions, String outputVersion) {
		try {
			this.acceptedVersions = acceptedVersions == null ? NO_VERSION : parseVersionRangeSpec(acceptedVersions);
		} catch(InvalidVersionSpecificationException ex) {
			throw new RuntimeException(ex);
		}
		this.acceptedVersionsComparator = acceptedVersions == null ? NO_VERSION : parseVersionRangeComparable(acceptedVersions);
		this.outputVersion = outputVersion;
	}

	/**
	 * Can remap a property
	 * @param category
	 * @param prop
	 * @param remappedValues
	 * @return True if the property was remapped or removed
	 */
	public abstract boolean remap(String category, Property prop, Configuration remappedValues);

	@Override
	public int compareTo(ConfigRemapper other) {
		if(this.acceptedVersionsComparator == NO_VERSION && other.acceptedVersionsComparator == NO_VERSION) {
			return 0;
		} else if(this.acceptedVersionsComparator == NO_VERSION) {
			return -1;
		} else if(other.acceptedVersionsComparator == NO_VERSION) {
			return 1;
		}
		return this.acceptedVersionsComparator.compareTo(other.acceptedVersionsComparator);
	}
}

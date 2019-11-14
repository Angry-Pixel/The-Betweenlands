package thebetweenlands.common.config;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.FieldWrapper;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.config.remapper.ConfigRemapper;
import thebetweenlands.common.config.remapper.Remapper1;
import thebetweenlands.common.lib.ModInfo;

public class ConfigHelper {
	/**
	 * Config loaded from existing file
	 */
	@Nullable
	private static Configuration fileConfig;

	/**
	 * Currently loaded config, read-only
	 */
	private static Configuration loadedConfig;

	@SubscribeEvent
	public static void onConfigChanged(OnConfigChangedEvent event) {
		if (ModInfo.ID.equals(event.getModID())) {
			ConfigManager.sync(ModInfo.ID, Config.Type.INSTANCE);

			Configuration newConfig = new Configuration(new File(BetweenlandsConfig.configDir, "config.cfg"));
			newConfig.load();

			loadedConfig = newConfig;

			initProperties(false);
		}
	}

	public static void loadExistingConfig() {
		fileConfig = null;
		BetweenlandsConfig.configDir = new File(Loader.instance().getConfigDir(), ModInfo.ID);
		File configFile = new File(BetweenlandsConfig.configDir, "config.cfg");
		if(configFile.exists()) {
			fileConfig = new Configuration(configFile);
			fileConfig.load();
		}
	}

	public static void init() {
		final File versionFile = new File(BetweenlandsConfig.configDir, "config_version");

		Configuration newConfig = new Configuration(new File(BetweenlandsConfig.configDir, "config.cfg"));
		newConfig.load();

		Configuration oldConfig;
		String configVersion = null;

		if(fileConfig == null) {
			configVersion = ModInfo.CONFIG_VERSION;
			oldConfig = new Configuration(new File(BetweenlandsConfig.configDir, "config.cfg"));
			oldConfig.load();
		} else {
			if(versionFile.exists()) {
				try {
					configVersion = FileUtils.readFileToString(versionFile, (Charset) null);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			oldConfig = fileConfig;
		}

		ConfigRemapper.register(new Remapper1());

		Pair<Configuration, String> remap = ConfigRemapper.remap(oldConfig, newConfig, configVersion);
		if(remap != null) {
			//Create backup of old config values
			final File backupFile = new File(oldConfig.getConfigFile().getParentFile(), "config (" + (configVersion == null ? "no version" : configVersion) + ").cfg.backup");
			Configuration backup = ConfigRemapper.clear(new Configuration(backupFile, oldConfig.getDefinedConfigVersion()));
			ConfigRemapper.copy(oldConfig, backup);
			backup.save();

			newConfig = remap.getKey();
			newConfig.save();
			reloadConfig();
		}

		loadedConfig = newConfig;

		initProperties(false);

		try {
			FileUtils.writeStringToFile(versionFile, ModInfo.CONFIG_VERSION, (Charset) null, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void reloadConfig() {
		try {
			Field configCacheField = ConfigManager.class.getDeclaredField("CONFIGS");
			configCacheField.setAccessible(true);

			@SuppressWarnings("unchecked")
			Map<String, Configuration> configCache = (Map<String, Configuration>) configCacheField.get(null);

			configCache.put(new File(BetweenlandsConfig.configDir, "config.cfg").getAbsolutePath(), null);

			ConfigManager.sync(ModInfo.ID, Config.Type.INSTANCE);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void postInit() {
		initProperties(true);
	}
	
	private static void initProperties(boolean post) {
		initProperties(BetweenlandsConfig.class, null, post);
	}

	private static void initProperties(Class<?> cls, @Nullable Object inst, boolean post) {
		for(Field f : cls.getDeclaredFields()) {
			if(!Modifier.isPublic(f.getModifiers())) {
				continue;
			}
			if(Modifier.isStatic(f.getModifiers()) != (inst == null)) {
				continue;
			}

			if(ConfigProperty.class.isAssignableFrom(f.getType())) {
				try {
					ConfigProperty property = (ConfigProperty) f.get(inst);

					if(property != null) {
						if(!post) {
							property.init();
						} else {
							property.postInitGame();
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			} else if(!FieldWrapper.hasWrapperFor(f) && f.getType().getSuperclass() != null && f.getType().getSuperclass().equals(Object.class)) {
				if(f.isAnnotationPresent(Config.Ignore.class)) {
					continue;
				}

				try {
					Object obj = f.get(inst);
					if(obj != null) {
						initProperties(obj.getClass(), obj, post);
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}

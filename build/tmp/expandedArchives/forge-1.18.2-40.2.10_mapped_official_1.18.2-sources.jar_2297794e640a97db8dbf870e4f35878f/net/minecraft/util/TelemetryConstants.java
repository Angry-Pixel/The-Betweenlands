package net.minecraft.util;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class TelemetryConstants {
   public static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME.withZone(ZoneId.from(ZoneOffset.UTC));
   public static final String EVENT_WORLD_LOADED = "WorldLoaded";
   public static final String SERVER_MODDED = "serverModded";
   public static final String USER_ID = "UserId";
   public static final String CLIENT_ID = "ClientId";
   public static final String DEVICE_SESSION_ID = "deviceSessionId";
   public static final String WORLD_SESSION_ID = "WorldSessionId";
   public static final String EVENT_TIMESTAMP_UTC = "eventTimestampUtc";
   public static final String BUILD_DISPLAY_NAME = "build_display_name";
   public static final String CLIENT_MODDED = "clientModded";
   public static final String SERVER_TYPE = "server_type";
   public static final String BUILD_PLATFORM = "BuildPlat";
   public static final String PLATFORM = "Plat";
   public static final String JAVA_VERSION = "javaVersion";
   public static final String PLAYER_GAME_MODE = "PlayerGameMode";
   public static final int GAME_MODE_SURVIVAL = 0;
   public static final int GAME_MODE_CREATIVE = 1;
   public static final int GAME_MODE_ADVENTURE = 2;
   public static final int GAME_MODE_SPECTATOR = 6;
   public static final int GAME_MODE_HARDCORE = 99;
   public static final String SERVER_TYPE_REALM = "realm";
   public static final String SERVER_TYPE_LOCAL = "local";
   public static final String SERVER_TYPE_OTHER = "server";
}
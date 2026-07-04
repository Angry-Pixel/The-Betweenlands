package thebetweenlands.client.event;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import thebetweenlands.client.handler.ShaderHandler;
import thebetweenlands.client.renderer.BLVertexFormats;
import thebetweenlands.client.shader.ShaderHelper;
import thebetweenlands.common.TheBetweenlands;

import java.io.IOException;

public class BetweenlandsShaders {

	public static ShaderInstance PRIMORDIAL_SHIELD;
	public static ShaderInstance PARTICLE_NO_ALPHA_CHECK;
	public static ShaderInstance DUNGEON_DOOR_RUNES;

	static void registerShaders(RegisterShadersEvent event) {
		try {
			event.registerShader(new ShaderInstance(event.getResourceProvider(), TheBetweenlands.prefix("primordial_shield/primordial_shield"), DefaultVertexFormat.POSITION_TEX_COLOR), instance -> PRIMORDIAL_SHIELD = instance);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), TheBetweenlands.prefix("particle/particle_no_alpha"), DefaultVertexFormat.PARTICLE), instance -> PARTICLE_NO_ALPHA_CHECK = instance);
			event.registerShader(new ShaderInstance(event.getResourceProvider(), TheBetweenlands.prefix("dungeon_door_rune/dungeon_door_rune"), BLVertexFormats.DUNGEON_DOOR_RUNE), instance -> DUNGEON_DOOR_RUNES = instance);
		} catch (IOException e) {
			TheBetweenlands.LOGGER.error("Failed to register Betweenlands shaders. Reason: ", e);
		}
		ShaderHandler.loadWorldShader(event.getResourceProvider());
		ShaderHelper.INSTANCE.initShaders(event.getResourceProvider());
	}
}

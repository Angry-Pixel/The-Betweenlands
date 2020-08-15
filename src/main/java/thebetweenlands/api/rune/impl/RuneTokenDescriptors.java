package thebetweenlands.api.rune.impl;

import net.minecraft.util.ResourceLocation;
import thebetweenlands.common.lib.ModInfo;

public final class RuneTokenDescriptors {
	private RuneTokenDescriptors()  {}

	public static final ResourceLocation ANY = new ResourceLocation(ModInfo.ID, "any");
	public static final ResourceLocation BLOCK = new ResourceLocation(ModInfo.ID, "block");
	public static final ResourceLocation ENTITY = new ResourceLocation(ModInfo.ID, "entity");
	public static final ResourceLocation DIRECTION = new ResourceLocation(ModInfo.ID, "direction");
	public static final ResourceLocation POSITION = new ResourceLocation(ModInfo.ID, "position");
	public static final ResourceLocation EFFECT = new ResourceLocation(ModInfo.ID, "effect");
	public static final ResourceLocation ITEM = new ResourceLocation(ModInfo.ID, "item");
}

package thebetweenlands.client.handler;

import java.util.List;
import java.util.Random;
import java.util.stream.StreamSupport;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraftforge.client.event.GuiContainerEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.storage.ILocalStorage;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.gen.ChunkGeneratorBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.common.world.storage.location.guard.ILocationGuard;

public class DebugHandlerClient {
	@SubscribeEvent
	public static void renderWorld(RenderWorldLastEvent event) {
		if (StreamSupport.stream(Minecraft.getMinecraft().player.getHeldEquipment().spliterator(), false)
				.anyMatch(stack -> !stack.isEmpty() && stack.getItem() == ItemRegistry.LOCATION_DEBUG)) {
			World world = Minecraft.getMinecraft().world;
			BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(world);

			for (ILocalStorage sharedStorage : worldStorage.getLocalStorageHandler().getLoadedStorages()) {
				if (sharedStorage instanceof LocationStorage) {
					LocationStorage location = (LocationStorage) sharedStorage;

					GlStateManager.pushMatrix();
					if (GuiScreen.isCtrlKeyDown()) {
						GlStateManager.disableDepth();
					}
					GlStateManager.disableTexture2D();
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0f);
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.glLineWidth(1F);
					GlStateManager.depthMask(false);
					GL11.glEnable(GL11.GL_LINE_SMOOTH);

					Random rnd = new Random(location.getSeed());

					float red = (0.25F + rnd.nextFloat() / 2.0F * 0.75F) * (location.getLayer() + 2) / 2.0F;
					float green = (0.25F + rnd.nextFloat() / 2.0F * 0.75F) * (location.getLayer() + 2) / 2.0F;
					float blue = (0.25F + rnd.nextFloat() * 0.75F) * (location.getLayer() + 2) / 2.0F;
					float alpha = 0.25F;

					GlStateManager.color(red, green, blue, alpha);
					for (AxisAlignedBB bb : location.getBounds()) {
						drawBoundingBox(bb.offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX,
								-Minecraft.getMinecraft().getRenderManager().viewerPosY,
								-Minecraft.getMinecraft().getRenderManager().viewerPosZ));
					}

					GlStateManager.color(red / 1.5F, green / 1.5F, blue / 1.5F, 1.0F);
					for (AxisAlignedBB bb : location.getBounds()) {
						drawBoundingBoxOutline(bb.offset(-Minecraft.getMinecraft().getRenderManager().viewerPosX,
								-Minecraft.getMinecraft().getRenderManager().viewerPosY,
								-Minecraft.getMinecraft().getRenderManager().viewerPosZ));
					}

					if (location.getEnclosingBounds() != null) {
						GlStateManager.color(red, green, blue, 1.0F);
						drawBoundingBoxOutline(location.getEnclosingBounds().offset(
								-Minecraft.getMinecraft().getRenderManager().viewerPosX,
								-Minecraft.getMinecraft().getRenderManager().viewerPosY,
								-Minecraft.getMinecraft().getRenderManager().viewerPosZ));

						AxisAlignedBB aabb = location.getEnclosingBounds();
						Vec3d center = new Vec3d((aabb.maxX + aabb.minX) / 2.0D, (aabb.maxY + aabb.minY) / 2.0D,
								(aabb.maxZ + aabb.minZ) / 2.0D).add(
										-Minecraft.getMinecraft().getRenderManager().viewerPosX,
										-Minecraft.getMinecraft().getRenderManager().viewerPosY,
										-Minecraft.getMinecraft().getRenderManager().viewerPosZ);

						GlStateManager.pushMatrix();
						GlStateManager.translate(center.x, center.y, center.z);

						float scale = Math.max(2.0F, (float) center.length() / 10.0F);

						GlStateManager.scale(scale, scale, scale);

						renderTag(Minecraft.getMinecraft().fontRenderer, location.hasLocalizedName() ? location.getLocalizedName() : location.getName(), 0, 0, 0, 0,
								Minecraft.getMinecraft().getRenderManager().playerViewY,
								Minecraft.getMinecraft().getRenderManager().playerViewX,
								Minecraft.getMinecraft().getRenderManager().options.thirdPersonView == 2);

						GlStateManager.enableBlend();

						GlStateManager.popMatrix();
					}

					GlStateManager.disableTexture2D();
					GlStateManager.color(1, 1, 1, 1);

					GlStateManager.enableDepth();
					GlStateManager.glLineWidth(2F);

					ILocationGuard guard = location.getGuard();
					if (guard != null) {
						GlStateManager.doPolygonOffset(-0.1F, -10.0F);
						GlStateManager.enablePolygonOffset();
						for (int xo = -8; xo <= 8; xo++) {
							for (int yo = -8; yo <= 8; yo++) {
								for (int zo = -8; zo <= 8; zo++) {
									BlockPos pos = Minecraft.getMinecraft().player.getPosition().add(xo, yo, zo);
									if (pos.getY() >= 0) {
										IBlockState state = world.getBlockState(pos);
										boolean guarded = guard.isGuarded(world, Minecraft.getMinecraft().player, pos);
										if (guarded) {
											if (state.getBlock() != Blocks.AIR) {
												GlStateManager.color(1, 0, 0, 0.25F);
												drawBoundingBox(state.getBoundingBox(world, pos).offset(pos).offset(
														-Minecraft.getMinecraft().getRenderManager().viewerPosX,
														-Minecraft.getMinecraft().getRenderManager().viewerPosY,
														-Minecraft.getMinecraft().getRenderManager().viewerPosZ));
											} else {
												GlStateManager.color(1, 0, 0, 0.8F);
												drawBoundingBoxOutline(new AxisAlignedBB(pos).offset(
														-Minecraft.getMinecraft().getRenderManager().viewerPosX,
														-Minecraft.getMinecraft().getRenderManager().viewerPosY,
														-Minecraft.getMinecraft().getRenderManager().viewerPosZ));
											}
										}
									}
								}
							}
						}
						GlStateManager.disablePolygonOffset();
					}

					GL11.glDisable(GL11.GL_LINE_SMOOTH);
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1f);
					GlStateManager.depthMask(true);
					GlStateManager.enableTexture2D();
					GlStateManager.enableDepth();
					GlStateManager.disableBlend();
					GlStateManager.popMatrix();
				}
			}
		}
	}

	public static void renderTag(FontRenderer fontRenderer, String str, float x, float y, float z, int yOffset,
			float playerViewY, float playerViewX, boolean thirdPerson) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		GlStateManager.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-playerViewY, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) (thirdPerson ? -1 : 1) * playerViewX, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-0.025F, -0.025F, 0.025F);

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA,
				GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE,
				GlStateManager.DestFactor.ZERO);
		int i = fontRenderer.getStringWidth(str) / 2;
		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexbuffer = tessellator.getBuffer();
		vertexbuffer.begin(7, DefaultVertexFormats.POSITION_COLOR);
		vertexbuffer.pos((double) (-i - 1), (double) (-1 + yOffset), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos((double) (-i - 1), (double) (8 + yOffset), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos((double) (i + 1), (double) (8 + yOffset), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		vertexbuffer.pos((double) (i + 1), (double) (-1 + yOffset), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();

		fontRenderer.drawString(str, -fontRenderer.getStringWidth(str) / 2, yOffset, 0xFFFFFFFF);
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	@SideOnly(Side.CLIENT)
	public static void drawBoundingBox(AxisAlignedBB axisalignedbb) {
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.minZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.maxY, axisalignedbb.maxZ);
		GL11.glVertex3d(axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
		GL11.glEnd();
	}

	@SideOnly(Side.CLIENT)
	public static void drawBoundingBoxOutline(AxisAlignedBB par1AxisAlignedBB) {
		GL11.glBegin(GL11.GL_LINE_STRIP);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glEnd();
		GL11.glBegin(GL11.GL_LINES);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.minZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.maxX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.minY, par1AxisAlignedBB.maxZ);
		GL11.glVertex3d(par1AxisAlignedBB.minX, par1AxisAlignedBB.maxY, par1AxisAlignedBB.maxZ);
		GL11.glEnd();
	}

	@SubscribeEvent
	public static void onKey(InputEvent.KeyInputEvent event) {
		if (BetweenlandsConfig.DEBUG.debug && Keyboard.getEventKeyState()) {
			if (Keyboard.getEventKey() == Keyboard.KEY_Y) {
				WorldServer world = DimensionManager.getWorld(BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId);
				if (world != null) {
					ChunkGeneratorBetweenlands cgb = (ChunkGeneratorBetweenlands) world
							.getChunkProvider().chunkGenerator;
					if (Keyboard.getEventKey() == Keyboard.KEY_Y) {
						if (Keyboard.isKeyDown(Keyboard.KEY_LMENU) || Keyboard.isKeyDown(Keyboard.KEY_RMENU)) {
							cgb.debugProvideReset();
							if (Minecraft.getMinecraft().player != null) {
								Minecraft.getMinecraft().player.sendMessage(
										new TextComponentString(String.format("Reset chunk provider debug")));
							}
						} else {
							cgb.debugGenerateChunkProvidesImage(true);
						}
					}
				}
			} else if (Keyboard.getEventKey() == Keyboard.KEY_X) {
				ChunkGeneratorBetweenlands.debugRecord = !ChunkGeneratorBetweenlands.debugRecord;
				if (Minecraft.getMinecraft().player != null) {
					Minecraft.getMinecraft().player
							.sendMessage(new TextComponentString(String.format("Chunk provider debug is now %s",
									ChunkGeneratorBetweenlands.debugRecord ? "enabled" : "disabled")));
				}
			}
		}
	}

	@SubscribeEvent
	public static void onGuiDrawPost(GuiContainerEvent.DrawForeground event) {
		if (BetweenlandsConfig.DEBUG.debug) {
			Container container = event.getGuiContainer().inventorySlots;
			List<Slot> slots = container.inventorySlots;
			FontRenderer renderer = Minecraft.getMinecraft().fontRenderer;
			GlStateManager.pushMatrix();
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
			RenderHelper.disableStandardItemLighting();
			for (Slot slot : slots) {
				renderer.drawString(String.valueOf(slot.slotNumber), slot.xPos, slot.yPos, 0xDDADADAD, false);
			}
			RenderHelper.enableStandardItemLighting();
			GlStateManager.disableBlend();
			GlStateManager.popMatrix();
		}
	}
}

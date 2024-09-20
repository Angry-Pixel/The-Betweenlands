package thebetweenlands.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix4f;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.entity.fishing.BLFishHook;
import thebetweenlands.common.entity.fishing.anadia.Anadia;

public class FishStaminaBarOverlay {

	public static final ResourceLocation BACKGROUND = TheBetweenlands.prefix("fishing/background");
	public static final ResourceLocation FOREGROUND = TheBetweenlands.prefix("fishing/foreground");

	public static final ResourceLocation CORAL = TheBetweenlands.prefix("fishing/coral");
	public static final ResourceLocation CRAB = TheBetweenlands.prefix("fishing/crab");
	public static final ResourceLocation CRAB_UNDERGROUND = TheBetweenlands.prefix("fishing/crab_underground");
	public static final ResourceLocation FISH = TheBetweenlands.prefix("fishing/fish");
	public static final ResourceLocation JELLYFISH = TheBetweenlands.prefix("fishing/jellyfish");
	public static final ResourceLocation ROCK = TheBetweenlands.prefix("fishing/rock");
	public static final ResourceLocation SCOOP_FISH = TheBetweenlands.prefix("fishing/scoop_fish");
	public static final ResourceLocation SEAWEED = TheBetweenlands.prefix("fishing/seaweed");
	public static final ResourceLocation TREASURE = TheBetweenlands.prefix("fishing/treasure");
	public static final ResourceLocation TREASURE_OPEN = TheBetweenlands.prefix("fishing/treasure_open");
	public static final ResourceLocation FISHING_LINE = TheBetweenlands.prefix("textures/gui/fishing_line.png");

	public static void renderFishingHud(GuiGraphics graphics, DeltaTracker tracker) {
		Minecraft minecraft = Minecraft.getInstance();
		Player player = minecraft.player;
		if (player != null && player.fishing instanceof BLFishHook hook) {
			if (hook.isPassenger() && hook.getVehicle() instanceof Anadia anadia) {

				int xPos = graphics.guiWidth() / 2 - 128;
				int yPos = graphics.guiHeight() / 2 - 120;
				int fishpos = -256 + (anadia.getStaminaTicks() * 256 / 180);
				int escapepos = -256 + Math.min(anadia.getEscapeTicks() * 256 / 1024, 256);
				int escapeDelay = Math.min(10, anadia.getEscapeDelay());
				int obstructpos1 = -anadia.getObstruction1Ticks();
				int obstructpos2 = -anadia.getObstruction2Ticks();
				int obstructpos3 = -anadia.getObstruction3Ticks();
				int obstructpos4 = -(anadia.getObstruction4Ticks() * 256 / 512);
				int treasurePos = -(anadia.getTreasureTicks() * 256 / 1024);
				boolean showTreasure = anadia.isTreasureFish();
				boolean treasureUnlocked = anadia.getTreasureUnlocked();

				if (anadia.getStaminaTicks() <= 0) {
					graphics.blitSprite(SCOOP_FISH, xPos + 240, yPos + 4, 64, 32);
				} else {
					graphics.blitSprite(BACKGROUND, xPos, yPos + 2, 256, 25); // background

					graphics.enableScissor(xPos + 4, yPos, xPos + 252, yPos + 19);

					if (showTreasure) graphics.blitSprite(treasureUnlocked ? TREASURE_OPEN : TREASURE, xPos - treasurePos, yPos + 1, 16, 16); // chest

					drawHangingRope(graphics.pose().last().pose(), tracker, fishpos, xPos - fishpos + 7, yPos + 12, xPos + 256 + 16, yPos, 0.5F); // line

					graphics.blitSprite(FISH, xPos - fishpos - 8, yPos + 1, 16, 16); // fish

					graphics.blitSprite(JELLYFISH, xPos - obstructpos4 - 8, yPos + 2, 16, 16); // jolly fush

					graphics.blitSprite(escapeDelay < 10 ? CRAB : CRAB_UNDERGROUND, xPos - escapepos - 8, yPos + 2 + escapeDelay, 16, 16); // crab

					graphics.blitSprite(SEAWEED, xPos - obstructpos1 - 8, yPos, 16, 16); // weed
					graphics.blitSprite(CORAL, xPos - obstructpos3 - 8, yPos, 16, 16); // coral
					graphics.blitSprite(ROCK, xPos - obstructpos2 - 8, yPos, 16, 16); // rock

					graphics.disableScissor();

					graphics.blitSprite(FOREGROUND, xPos, yPos + 2, 256, 25); // foreground
				}
			}
		}
	}

	private static void drawHangingRope(Matrix4f matrix, DeltaTracker tracker, int updateCounter, float sx, float sy, float ex, float ey, float hang) {
		float x3 = ex;

		if (sx - x3 >= 0.0F && sx - x3 < 1.0F) {
			x3 = sx + 1;
		} else if (sx - x3 < 0.0F && sx - x3 > -1.0F) {
			x3 = sx - 1;
		}

		float x2 = (sx + x3) / 2.0F;
		float y2 = Math.max(sy, ey) + hang + (float) Math.sin((updateCounter + tracker.getRealtimeDeltaTicks()) / 25.0F) * 1.5f;

		//Fit parabola
		float a1 = -sx * sx + x2 * x2;
		float b1 = -sx + x2;
		float d1 = -sy + y2;
		float a2 = -x2 * x2 + x3 * x3;
		float b2 = -x2 + x3;
		float d2 = -y2 + ey;
		float b3 = -b2 / b1;
		float a3 = b3 * a1 + a2;
		float d = b3 * d1 + d2;
		float a = d / a3;
		float b = (d1 - a1 * a) / b1;
		float c = sy - a * sx * sx - b * sx;

		float px = sx;
		float py = sy;

		float width = 0.75F;

		float pxc1 = sx - width;
		float pyc1 = sy;
		float pxc2 = sx + width;
		float pyc2 = sy;

		boolean isTowardsRight = sx < x3;

		float ropeV1 = isTowardsRight ? 0 : 0.5F;
		float ropeV2 = isTowardsRight ? 0.5F : 0;

		float endV1 = isTowardsRight ? 0.5F : 1.0F;
		float endV2 = isTowardsRight ? 1.0F : 0.5F;

		float endU11 = isTowardsRight ? 0 : 1.0F;
		float endU12 = 0.5F;

		float endU21 = 0.5F;
		float endU22 = isTowardsRight ? 1.0F : 0;

		float u = 0;

		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderTexture(0, FISHING_LINE);
		BufferBuilder builder = Tesselator.getInstance().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION_TEX);

		int pieces = 32;

		for (int i = -1; i <= pieces; i++) {
			float x = sx + (x3 - sx) / (float) (pieces - 1) * i;
			float y = a * x * x + b * x + c;

			float sideX = y - py;
			float sideY = -(x - px);
			float sideDirLength = (float) Math.sqrt(sideX * sideX + sideY * sideY);
			sideX *= width / sideDirLength;
			sideY *= width / sideDirLength;

			float xc1 = x - sideX;
			float yc1 = y - sideY;
			float xc2 = x + sideX;
			float yc2 = y + sideY;

			if (i == 1) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				builder.addVertex(matrix, pxc2 - offX, pyc2 - offY, 0.0F).setUv(endU11, endV1);
				builder.addVertex(matrix, pxc1 - offX, pyc1 - offY, 0.0F).setUv(endU11, endV2);
				builder.addVertex(matrix, pxc2, pyc2, 0.0F).setUv(endU12, endV1);
				builder.addVertex(matrix, pxc1, pyc1, 0.0F).setUv(endU12, endV2);
			}

			if (i > 0) {
				builder.addVertex(matrix, pxc2, pyc2, 0.0F).setUv(u, ropeV1);
				builder.addVertex(matrix, pxc1, pyc1, 0.0F).setUv(u, ropeV2);

				u += (float) Math.sqrt((x - px) * (x - px) + (y - py) * (y - py)) / 16.0F;
			}

			if (i == pieces) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				builder.addVertex(matrix, pxc2, pyc2, 0.0F).setUv(endU21, endV1);
				builder.addVertex(matrix, pxc1, pyc1, 0.0F).setUv(endU21, endV2);
				builder.addVertex(matrix, pxc2 + offX, pyc2 + offY, 0.0F).setUv(endU22, endV1);
				builder.addVertex(matrix, pxc1 + offX, pyc1 + offY, 0.0F).setUv(endU22, endV2);
			}

			px = x;
			py = y;

			pxc1 = xc1;
			pyc1 = yc1;
			pxc2 = xc2;
			pyc2 = yc2;
		}

		BufferUploader.drawWithShader(builder.buildOrThrow());
	}
}

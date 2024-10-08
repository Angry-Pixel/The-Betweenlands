package thebetweenlands.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.common.NeoForge;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.registry.AspectType;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.gui.book.widget.text.FormatTags;
import thebetweenlands.client.gui.book.widget.text.TextContainer;
import thebetweenlands.client.gui.overlay.swarm.SwarmOverlay;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.item.AspectContents;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.registries.DimensionRegistries;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationStorage;

import java.util.List;

public class ScreenHandler {

	private static double obstructionPercentage = 0;
	private static double prevObstructionPercentage = 0;
	private static double dispersionIndicatorPercentage = 0;
	private static double prevDispersionIndicatorPercentage = 0;

//	private static final ResizableFramebuffer ringOfDispersionWorldFramebuffer = new ResizableFramebuffer(true);
//	private static final RingOfDispersionWorldRenderer ringOfDispersionWorldRenderer = new RingOfDispersionWorldRenderer(6, 6);

	private static Component currentLocation = Component.empty();
	private static int titleTicks = 0;
	private static int maxTitleTicks = 120;

	private static int cavingRopeConnectTicks = 0;
	private static int cavingRopeCount = 0;

	private static final Object2IntMap<LocationStorage> titleDisplayCooldowns = new Object2IntOpenHashMap<>();

	public static List<LocationStorage> getVisibleLocations(Entity entity) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.get(entity.level());
		if (worldStorage != null) {
			return worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, entity.getX(), entity.getZ(), location -> location.isInside(entity.getEyePosition(1)) && location.isVisible(entity));
		}
		return List.of();
	}

	public static void init() {
		NeoForge.EVENT_BUS.addListener(ScreenHandler::tickScreen);
		NeoForge.EVENT_BUS.addListener(ScreenHandler::drawAspectTooltip);
	}

	private static void tickScreen(ClientTickEvent.Pre event) {
		if (!Minecraft.getInstance().isPaused()) {
			SwarmOverlay.INSTANCE.update();

			if (titleTicks > 0) {
				titleTicks--;
			}

			cavingRopeCount = 0;
			if (cavingRopeConnectTicks > 0) {
				cavingRopeConnectTicks--;
			}

			Player player = Minecraft.getInstance().player;

			if (player != null) {
//				IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);
//
//				if (cap != null) {
//					prevObstructionPercentage = obstructionPercentage;
//
//					double fadeStartDistance = Math.min(cap.getViewObstructionCheckDistance(), 0.25D);
//					double obstructionDistance = cap.getViewObstructionDistance();
//
//					if (obstructionDistance < fadeStartDistance) {
//						obstructionPercentage = 1.0D - obstructionDistance / fadeStartDistance;
//					} else {
//						obstructionPercentage = 0.0D;
//					}
//				} else {
//					obstructionPercentage = prevObstructionPercentage = 0.0D;
//				}

				prevDispersionIndicatorPercentage = dispersionIndicatorPercentage;

				float targetDispersionPercentage = 0;

//				ItemStack ring = RingOfDispersionEntityCapability.getRing(player);
//				if (!ring.isEmpty()) {
//					ItemRingOfDispersion item = (ItemRingOfDispersion) ring.getItem();
//					targetDispersionPercentage = item.getTimer(ring) / (float) item.getMaxPhasingDuration(ring);
//				}
//
//				if (dispersionIndicatorPercentage < targetDispersionPercentage) {
//					dispersionIndicatorPercentage += 0.005D;
//					if (dispersionIndicatorPercentage > targetDispersionPercentage) {
//						dispersionIndicatorPercentage = targetDispersionPercentage;
//					}
//				} else if (dispersionIndicatorPercentage > targetDispersionPercentage) {
//					dispersionIndicatorPercentage -= 0.025D;
//					if (dispersionIndicatorPercentage < targetDispersionPercentage) {
//						dispersionIndicatorPercentage = targetDispersionPercentage;
//					}
//				}

				if (BetweenlandsConfig.cavingRopeIndicator) {
					for (ItemStack stack : player.getInventory().items) {
						if (!stack.isEmpty() && stack.is(ItemRegistry.CAVING_ROPE)) {
							cavingRopeCount += stack.getCount();
						}
					}
				}

				var titleDisplayCooldownsIT = titleDisplayCooldowns.object2IntEntrySet().iterator();
				while (titleDisplayCooldownsIT.hasNext()) {
					var entry = titleDisplayCooldownsIT.next();

					int cooldown = entry.getIntValue();
					if (cooldown > 1) {
						titleDisplayCooldowns.put(entry.getKey(), cooldown - 1);
					} else {
						titleDisplayCooldownsIT.remove();
					}
				}

				if (player.level().dimension() == DimensionRegistries.DIMENSION_KEY) {
					Component prevLocation = currentLocation;

					List<LocationStorage> locations = getVisibleLocations(player);
					if (locations.isEmpty()) {
						Component location;
						if (player.getY() < TheBetweenlands.CAVE_START - 10) {
							Component wildernessName = Component.translatable("location.wilderness.name");
							if (currentLocation.equals(wildernessName)) {
								prevLocation = Component.empty();
							}
							location = Component.translatable("location.caverns.name");
						} else {
							Component cavernsName = Component.translatable("location.caverns.name");
							if (currentLocation.equals(cavernsName)) {
								prevLocation = Component.empty();
							}
							location = Component.translatable("location.wilderness.name");
						}
						currentLocation = location;
					} else {
						LocationStorage highestLocation = null;
						for (LocationStorage storage : locations) {
							if (highestLocation == null || storage.getLayer() > highestLocation.getLayer())
								highestLocation = storage;
						}

						int displayCooldown = 60 * 20; //1 minute cooldown for title

						int currentCooldown = titleDisplayCooldowns.getInt(highestLocation);

						if (currentCooldown == 0) {
							titleDisplayCooldowns.put(highestLocation, displayCooldown);

							if (highestLocation.hasLocalizedName()) {
								currentLocation = highestLocation.getLocalizedName();
							} else {
								currentLocation = Component.literal(highestLocation.getName());
							}
						} else if (currentCooldown > 0) {
							titleDisplayCooldowns.put(highestLocation, displayCooldown); //Keep cooldown up until player leaves location
						}
					}

					if (!currentLocation.getString().isEmpty()) {
						if (currentLocation.getString().contains(":")) {
							int startIndex = currentLocation.getString().indexOf(":");
							try {
								String ticks = currentLocation.getString().substring(0, startIndex);
								maxTitleTicks = Integer.parseInt(ticks);
								currentLocation = Component.translatable(currentLocation.getString().substring(startIndex + 1));
							} catch (Exception ex) {
								maxTitleTicks = 80;
							}
						}
						if (!prevLocation.equals(currentLocation)) {
							titleTicks = maxTitleTicks;
							TextContainer titleContainer = new TextContainer(2048, 2048, currentLocation, TheBetweenlands.HERBLORE_FONT.getFont());
							titleContainer.setCurrentScale(2.0f).setCurrentColor(0xFFFFFFFF);
							titleContainer.registerTag(new FormatTags.TagNewLine());
							titleContainer.registerTag(new FormatTags.TagScale(2.0F));
							titleContainer.registerTag(new FormatTags.TagSimple("bold", ChatFormatting.BOLD));
							titleContainer.registerTag(new FormatTags.TagSimple("obfuscated", ChatFormatting.OBFUSCATED));
							titleContainer.registerTag(new FormatTags.TagSimple("italic", ChatFormatting.ITALIC));
							titleContainer.registerTag(new FormatTags.TagSimple("strikethrough", ChatFormatting.STRIKETHROUGH));
							titleContainer.registerTag(new FormatTags.TagSimple("underline", ChatFormatting.UNDERLINE));
							try {
								titleContainer.parse();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	//Render the untextured black quad in world s.t. it can't be disabled
	//by F1 or other GUIs
//	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
//	public void onRenderWorldLast(RenderWorldLastEvent event) {
//		Minecraft mc = Minecraft.getMinecraft();
//		EntityPlayer player = mc.player;
//
//		if (player != null) {
//			GlStateManager.matrixMode(GL11.GL_PROJECTION);
//			GlStateManager.pushMatrix();
//
//			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//			GlStateManager.pushMatrix();
//
//			//Same as mc.entityRenderer.setupOverlayRendering(); but without clearing depth
//			ScaledResolution scaledresolution = new ScaledResolution(mc);
//			GlStateManager.matrixMode(GL11.GL_PROJECTION);
//			GlStateManager.loadIdentity();
//			GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
//			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//			GlStateManager.loadIdentity();
//			GlStateManager.translate(0.0F, 0.0F, -2000.0F);
//
//			renderDispersionRingOverlay(mc, player, false, event.getPartialTicks());
//
//			GlStateManager.disableBlend();
//
//			GlStateManager.matrixMode(GL11.GL_PROJECTION);
//			GlStateManager.popMatrix();
//
//			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//			GlStateManager.popMatrix();
//		}
//	}

//	private void renderDispersionRingOverlay(Minecraft mc, EntityPlayer player, boolean guiOverlay, float partialTicks) {
//		Tessellator tessellator = Tessellator.getInstance();
//		BufferBuilder bufferbuilder = tessellator.getBuffer();
//		ScaledResolution res = new ScaledResolution(mc);
//
//		GlStateManager.disableAlpha();
//		GlStateManager.disableTexture2D();
//		GlStateManager.depthMask(false);
//		GlStateManager.disableDepth();
//
//		GlStateManager.enableBlend();
//		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//
//		IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);
//
//		if (cap != null) {
//			double viewFadeStartDistance = Math.min(cap.getViewObstructionCheckDistance(), 0.25D);
//			double viewObstructionDistance = cap.getViewObstructionDistance();
//
//			if (viewObstructionDistance < viewFadeStartDistance) {
//				float alpha = (float) Math.min(1, Math.max(0, prevObstructionPercentage + (obstructionPercentage - prevObstructionPercentage) * partialTicks));
//
//				GlStateManager.color(0, 0, 0, alpha);
//
//				if (!guiOverlay) {
//					bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
//					bufferbuilder.pos(0.0D, (double) res.getScaledHeight_double(), 0.0D).endVertex();
//					bufferbuilder.pos((double) res.getScaledWidth_double(), (double) res.getScaledHeight_double(), 0.0D).endVertex();
//					bufferbuilder.pos((double) res.getScaledWidth_double(), 0.0D, 0.0D).endVertex();
//					bufferbuilder.pos(0.0D, 0.0D, 0.0D).endVertex();
//					tessellator.draw();
//				} else {
//					bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
//					bufferbuilder.pos(0.0D, (double) res.getScaledHeight_double(), 0.0D).endVertex();
//					bufferbuilder.pos((double) res.getScaledWidth_double(), (double) res.getScaledHeight_double(), 0.0D).endVertex();
//					bufferbuilder.pos((double) res.getScaledWidth_double(), 0.0D, 0.0D).endVertex();
//					bufferbuilder.pos(0.0D, 0.0D, 0.0D).endVertex();
//					tessellator.draw();
//
//					float brightness = 0.5F + (1 - alpha) * 0.5F;
//
//					Framebuffer dispersionWorldFbo = null;
//
//					//Render some blocks around player
//					GlStateManager.matrixMode(GL11.GL_PROJECTION);
//					GlStateManager.pushMatrix();
//
//					GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//					GlStateManager.pushMatrix();
//
//					mc.entityRenderer.setupCameraTransform(partialTicks, 0);
//
//					ringOfDispersionWorldRenderer.setPos(new BlockPos(player.getPositionVector()));
//					ringOfDispersionWorldRenderer.setWorld(player.world);
//
//					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//					GlStateManager.enableDepth();
//					GlStateManager.depthMask(true);
//
//					Framebuffer mainFbo = mc.getFramebuffer();
//
//					dispersionWorldFbo = ringOfDispersionWorldFramebuffer.getFramebuffer(mainFbo.framebufferWidth, mainFbo.framebufferHeight);
//
//					dispersionWorldFbo.framebufferClear();
//					dispersionWorldFbo.bindFramebuffer(true);
//
//					ringOfDispersionWorldRenderer.render();
//
//					mainFbo.bindFramebuffer(true);
//
//					GlStateManager.disableDepth();
//					GlStateManager.depthMask(false);
//
//					GlStateManager.matrixMode(GL11.GL_PROJECTION);
//					GlStateManager.popMatrix();
//
//					GlStateManager.matrixMode(GL11.GL_MODELVIEW);
//					GlStateManager.popMatrix();
//
//					GlStateManager.enableBlend();
//					GlStateManager.disableLighting();
//					GlStateManager.color(brightness, brightness, brightness, alpha * alpha * alpha * 0.75F);
//					GlStateManager.enableTexture2D();
//
//					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//
//					//Render surrounding blocks overlay
//					GlStateManager.bindTexture(dispersionWorldFbo.framebufferTexture);
//					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//					bufferbuilder.pos(0.0D, (double) res.getScaledHeight_double(), -90.0D).tex(0.0D, 0.0D).endVertex();
//					bufferbuilder.pos((double) res.getScaledWidth_double(), (double) res.getScaledHeight_double(), -90.0D).tex(1.0D, 0.0D).endVertex();
//					bufferbuilder.pos((double) res.getScaledWidth_double(), 0.0D, -90.0D).tex(1.0D, 1.0D).endVertex();
//					bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 1.0D).endVertex();
//					tessellator.draw();
//
//					GlStateManager.color(brightness, brightness, brightness, alpha);
//					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//
//					//Render blinds
//					float centerPieceWidth = (float) (res.getScaledHeight_double() / 2.0D);
//					float centerStart = (float) (res.getScaledWidth_double() / 2.0D - centerPieceWidth / 2.0D);
//					float centerEnd = (float) (res.getScaledWidth_double() / 2.0D + centerPieceWidth / 2.0D);
//
//					mc.getTextureManager().bindTexture(RING_OF_DISPERSION_OVERLAY_TEXTURE);
//
//					GlStateManager.pushMatrix();
//					GlStateManager.translate(0, -res.getScaledHeight_double() / 2 + res.getScaledHeight_double() / 2 * alpha, 0);
//
//					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//
//					//top left
//					bufferbuilder.pos(0.0D, res.getScaledHeight_double() / 2, -90.0D).tex(0.0D, 0.5D).endVertex();
//					bufferbuilder.pos(centerStart, res.getScaledHeight_double() / 2, -90.0D).tex(0.333D, 0.5D).endVertex();
//					bufferbuilder.pos(centerStart, 0.0D, -90.0D).tex(0.333D, 0.0D).endVertex();
//					bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
//					//top center
//					bufferbuilder.pos(centerStart, res.getScaledHeight_double() / 2, -90.0D).tex(0.333D, 0.5D).endVertex();
//					bufferbuilder.pos(centerEnd, res.getScaledHeight_double() / 2, -90.0D).tex(0.666D, 0.5D).endVertex();
//					bufferbuilder.pos(centerEnd, 0.0D, -90.0D).tex(0.666D, 0.0D).endVertex();
//					bufferbuilder.pos(centerStart, 0.0D, -90.0D).tex(0.333D, 0.0D).endVertex();
//					//top right
//					bufferbuilder.pos(centerEnd, res.getScaledHeight_double() / 2, -90.0D).tex(0.666D, 0.5D).endVertex();
//					bufferbuilder.pos(res.getScaledWidth_double(), res.getScaledHeight_double() / 2, -90.0D).tex(1.0D, 0.5D).endVertex();
//					bufferbuilder.pos(res.getScaledWidth_double(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
//					bufferbuilder.pos(centerEnd, 0.0D, -90.0D).tex(0.666D, 0.0D).endVertex();
//
//					tessellator.draw();
//
//					GlStateManager.popMatrix();
//
//					GlStateManager.pushMatrix();
//					GlStateManager.translate(0, res.getScaledHeight_double() / 2 - res.getScaledHeight_double() / 2 * alpha, 0);
//
//					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//
//					//bottom left
//					bufferbuilder.pos(0.0D, res.getScaledHeight_double(), -90.0D).tex(0.0D, 1.0D).endVertex();
//					bufferbuilder.pos(centerStart, res.getScaledHeight_double(), -90.0D).tex(0.333D, 1.0D).endVertex();
//					bufferbuilder.pos(centerStart, res.getScaledHeight_double() / 2, -90.0D).tex(0.333D, 0.5D).endVertex();
//					bufferbuilder.pos(0.0D, res.getScaledHeight_double() / 2, -90.0D).tex(0.0D, 0.5D).endVertex();
//					//bottom center
//					bufferbuilder.pos(centerStart, res.getScaledHeight_double(), -90.0D).tex(0.333D, 1.0D).endVertex();
//					bufferbuilder.pos(centerEnd, res.getScaledHeight_double(), -90.0D).tex(0.666D, 1.0D).endVertex();
//					bufferbuilder.pos(centerEnd, res.getScaledHeight_double() / 2, -90.0D).tex(0.666D, 0.5D).endVertex();
//					bufferbuilder.pos(centerStart, res.getScaledHeight_double() / 2, -90.0D).tex(0.333D, 0.5D).endVertex();
//					//bottom right
//					bufferbuilder.pos(centerEnd, res.getScaledHeight_double(), -90.0D).tex(0.666D, 1.0D).endVertex();
//					bufferbuilder.pos(res.getScaledWidth_double(), res.getScaledHeight_double(), -90.0D).tex(1.0D, 1.0D).endVertex();
//					bufferbuilder.pos(res.getScaledWidth_double(), res.getScaledHeight_double() / 2, -90.0D).tex(1.0D, 0.5D).endVertex();
//					bufferbuilder.pos(centerEnd, res.getScaledHeight_double() / 2, -90.0D).tex(0.666D, 0.5D).endVertex();
//
//					tessellator.draw();
//
//					GlStateManager.popMatrix();
//
//					GlStateManager.color((1 - brightness) * 2, (1 - brightness) * 2, (1 - brightness) * 2, 1);
//
//					//Vignette
//					mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
//					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//					bufferbuilder.pos(0.0D, (double) res.getScaledHeight_double(), -90.0D).tex(0.0D, 1.0D).endVertex();
//					bufferbuilder.pos((double) res.getScaledWidth_double(), (double) res.getScaledHeight_double(), -90.0D).tex(1.0D, 1.0D).endVertex();
//					bufferbuilder.pos((double) res.getScaledWidth_double(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
//					bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
//					tessellator.draw();
//					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//				}
//			}
//		}
//
//		GlStateManager.depthMask(false);
//		GlStateManager.enableTexture2D();
//		GlStateManager.enableAlpha();
//		GlStateManager.enableBlend();
//		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
//
//		if (guiOverlay) {
//			float indicatorAlpha = (float) (prevDispersionIndicatorPercentage + (dispersionIndicatorPercentage - prevDispersionIndicatorPercentage) * partialTicks);
//
//			if (indicatorAlpha > 0.01F) {
//				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);
//
//				GlStateManager.color(indicatorAlpha, indicatorAlpha, indicatorAlpha, indicatorAlpha);
//
//				//Indicator overlay
//				mc.getTextureManager().bindTexture(RING_OF_DISPERSION_INDICATOR_OVERLAY_TEXTURE);
//				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
//				bufferbuilder.pos(0.0D, (double) res.getScaledHeight_double(), -90.0D).tex(0.0D, 1.0D).endVertex();
//				bufferbuilder.pos((double) res.getScaledWidth_double(), (double) res.getScaledHeight_double(), -90.0D).tex(1.0D, 1.0D).endVertex();
//				bufferbuilder.pos((double) res.getScaledWidth_double(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
//				bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
//				tessellator.draw();
//
//				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
//			}
//		}
//
//		GlStateManager.color(1, 1, 1, 1);
//
//		GlStateManager.enableTexture2D();
//		GlStateManager.enableAlpha();
//		GlStateManager.depthMask(true);
//		GlStateManager.enableDepth();
//	}

	private static void drawAspectTooltip(RenderTooltipEvent.Color event) {
		if (Screen.hasShiftDown()) {
			Minecraft mc = Minecraft.getInstance();
			Font fontRenderer = mc.font;

			int yOffset = 0;
			int width = 0;

			List<Aspect> aspects = AspectContents.getAllAspectsForItem(event.getItemStack(), mc.level.registryAccess(), AspectManager.get(mc.level));

			if (!aspects.isEmpty()) {
				PoseStack stack = event.getGraphics().pose();
				stack.pushPose();

				stack.translate(event.getX(), event.getY() - 29, 300);

				for (Aspect aspect : aspects) {
					String aspectText = AspectType.getAspectName(aspect.type()).getString() + " (" + aspect.getRoundedDisplayAmount() + ")";
					String aspectTypeText = AspectType.getAspectType(aspect.type()).getString();

					event.getGraphics().setColor(1, 1, 1, 1);

					event.getGraphics().drawString(fontRenderer, aspectText, 6 + 17, 2 + yOffset, 0xFFFFFFFF);
					event.getGraphics().drawString(fontRenderer, aspectTypeText, 6 + 17, 2 + 9 + yOffset, 0xFFFFFFFF);

					var sprite = BetweenlandsClient.getAspectIconManager().get(aspect.type());
					event.getGraphics().blit(2, 2 + yOffset, 0, sprite.contents().width(), sprite.contents().height(), sprite);

					int entryWidth = Math.max(fontRenderer.width(aspectText), fontRenderer.width(aspectTypeText)) + 26;
					if (entryWidth > width) {
						width = entryWidth;
					}

					yOffset -= 21;
				}

				stack.translate(0, 0, -1);

				int height = -yOffset;
				int rectX = 0;
				int rectY = yOffset + 20;

				int backgroundColor = 0xF0100010;
				int borderColorStart = 0x505000FF;
				int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
				event.getGraphics().fillGradient(rectX - 3, rectY - 4, rectX + width + 3, rectY - 3, backgroundColor, backgroundColor);
				event.getGraphics().fillGradient(rectX - 3, rectY + height + 3, rectX + width + 3, rectY + height + 4, backgroundColor, backgroundColor);
				event.getGraphics().fillGradient(rectX - 3, rectY - 3, rectX + width + 3, rectY + height + 3, backgroundColor, backgroundColor);
				event.getGraphics().fillGradient(rectX - 4, rectY - 3, rectX - 3, rectY + height + 3, backgroundColor, backgroundColor);
				event.getGraphics().fillGradient(rectX + width + 3, rectY - 3, rectX + width + 4, rectY + height + 3, backgroundColor, backgroundColor);
				event.getGraphics().fillGradient(rectX - 3, rectY - 3 + 1, rectX - 3 + 1, rectY + height + 3 - 1, borderColorStart, borderColorEnd);
				event.getGraphics().fillGradient(rectX + width + 2, rectY - 3 + 1, rectX + width + 3, rectY + height + 3 - 1, borderColorStart, borderColorEnd);
				event.getGraphics().fillGradient(rectX - 3, rectY - 3, rectX + width + 3, rectY - 3 + 1, borderColorStart, borderColorStart);
				event.getGraphics().fillGradient(rectX - 3, rectY + height + 2, rectX + width + 3, rectY + height + 3, borderColorEnd, borderColorEnd);

				event.getGraphics().setColor(1, 1, 1, 1);

				stack.popPose();
			}
		}
	}
}

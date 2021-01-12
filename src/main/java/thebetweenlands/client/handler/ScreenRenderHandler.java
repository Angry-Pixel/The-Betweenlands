package thebetweenlands.client.handler;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import thebetweenlands.api.aspect.Aspect;
import thebetweenlands.api.aspect.ItemAspectContainer;
import thebetweenlands.api.capability.IDecayCapability;
import thebetweenlands.api.capability.IEntityCustomCollisionsCapability;
import thebetweenlands.api.capability.IEquipmentCapability;
import thebetweenlands.api.capability.IPortalCapability;
import thebetweenlands.client.gui.SwarmOverlayRenderer;
import thebetweenlands.client.render.block.RingOfDispersionWorldRenderer;
import thebetweenlands.client.render.shader.ResizableFramebuffer;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.capability.collision.RingOfDispersionEntityCapability;
import thebetweenlands.common.capability.equipment.EnumEquipmentInventory;
import thebetweenlands.common.config.BetweenlandsConfig;
import thebetweenlands.common.entity.EntityRopeNode;
import thebetweenlands.common.handler.PlayerPortalHandler;
import thebetweenlands.common.herblore.aspect.AspectManager;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer.TextPage;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer.TextSegment;
import thebetweenlands.common.item.equipment.ItemRingOfDispersion;
import thebetweenlands.common.registries.BlockRegistry;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.ItemRegistry;
import thebetweenlands.common.world.WorldProviderBetweenlands;
import thebetweenlands.common.world.storage.BetweenlandsWorldStorage;
import thebetweenlands.common.world.storage.location.LocationStorage;
import thebetweenlands.util.AspectIconRenderer;
import thebetweenlands.util.ColorUtils;

public class ScreenRenderHandler extends Gui {
	private ScreenRenderHandler() { }

	public static ScreenRenderHandler INSTANCE = new ScreenRenderHandler();

	private static final ResourceLocation DECAY_BAR_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/decay_bar.png");
	private static final ResourceLocation RING_OF_DISPERSION_OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/overlay/ring_of_dispersion_overlay.png");
	private static final ResourceLocation RING_OF_DISPERSION_OVERLAY_TOP_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/overlay/ring_of_dispersion_overlay_top.png");
	private static final ResourceLocation RING_OF_DISPERSION_OVERLAY_SIDE_TOP_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/overlay/ring_of_dispersion_overlay_side_top.png");
	private static final ResourceLocation RING_OF_DISPERSION_OVERLAY_BOTTOM_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/overlay/ring_of_dispersion_overlay_bottom.png");
	private static final ResourceLocation RING_OF_DISPERSION_OVERLAY_SIDE_BOTTOM_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/overlay/ring_of_dispersion_overlay_side_bottom.png");
	private static final ResourceLocation RING_OF_DISPERSION_INDICATOR_OVERLAY_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/overlay/ring_of_dispersion_indicator_overlay.png");
	private static final ResourceLocation VIGNETTE_TEXTURE = new ResourceLocation("textures/misc/vignette.png");

	private Random random = new Random();
	private int updateCounter;

	private double obstructionPercentage = 0;
	private double prevObstructionPercentage = 0;
	private double dispersionIndicatorPercentage = 0;
	private double prevDispersionIndicatorPercentage = 0;

	private final ResizableFramebuffer ringOfDispersionWorldFramebuffer = new ResizableFramebuffer(true);
	private final RingOfDispersionWorldRenderer ringOfDispersionWorldRenderer = new RingOfDispersionWorldRenderer(6, 6);

	private TextContainer titleContainer = null;
	private String currentLocation = "";
	private int titleTicks = 0;
	private int maxTitleTicks = 120;

	private int cavingRopeConnectTicks = 0;
	private int cavingRopeCount = 0;

	private final TObjectIntMap<LocationStorage> titleDisplayCooldowns = new TObjectIntHashMap<>();
	
	public static final ResourceLocation TITLE_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/location_title.png");

	public static final ResourceLocation CAVING_ROPE_CONNECTED = new ResourceLocation("thebetweenlands:textures/gui/caving_rope_connected.png");
	public static final ResourceLocation CAVING_ROPE_DISCONNECTED = new ResourceLocation("thebetweenlands:textures/gui/caving_rope_disconnected.png");

	private final SwarmOverlayRenderer crawlerOverlayRenderer = new SwarmOverlayRenderer();
	
	public static List<LocationStorage> getVisibleLocations(Entity entity) {
		BetweenlandsWorldStorage worldStorage = BetweenlandsWorldStorage.forWorld(entity.world);
		return worldStorage.getLocalStorageHandler().getLocalStorages(LocationStorage.class, entity.posX, entity.posZ, location -> location.isInside(entity.getPositionEyes(1)) && location.isVisible(entity));
	}

	@SubscribeEvent
	public void onClientTick(ClientTickEvent event) {
		if(event.phase == Phase.START && !Minecraft.getMinecraft().isGamePaused()) {
			this.crawlerOverlayRenderer.update();
			
			this.updateCounter++;

			if(this.titleTicks > 0) {
				this.titleTicks--;
			}

			this.cavingRopeCount = 0;
			if(this.cavingRopeConnectTicks > 0) {
				this.cavingRopeConnectTicks--;
			}

			EntityPlayer player = Minecraft.getMinecraft().player;

			if(player != null) {
				IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

				if(cap != null) {
					this.prevObstructionPercentage = this.obstructionPercentage;

					double fadeStartDistance = Math.min(cap.getViewObstructionCheckDistance(), 0.25D);
					double obstructionDistance = cap.getViewObstructionDistance();

					if(obstructionDistance < fadeStartDistance) {
						this.obstructionPercentage = 1.0D - obstructionDistance / fadeStartDistance;
					} else {
						this.obstructionPercentage = 0.0D;
					}
				} else {
					this.obstructionPercentage = this.prevObstructionPercentage = 0.0D;
				}

				this.prevDispersionIndicatorPercentage = this.dispersionIndicatorPercentage;

				float targetDispersionPercentage = 0;

				ItemStack ring = RingOfDispersionEntityCapability.getRing(player);
				if(!ring.isEmpty()) {
					ItemRingOfDispersion item = (ItemRingOfDispersion) ring.getItem();
					targetDispersionPercentage = item.getTimer(ring) / (float)item.getMaxPhasingDuration(ring);
				}

				if(this.dispersionIndicatorPercentage < targetDispersionPercentage) {
					this.dispersionIndicatorPercentage += 0.005D;
					if(this.dispersionIndicatorPercentage > targetDispersionPercentage) {
						this.dispersionIndicatorPercentage = targetDispersionPercentage;
					}
				} else if(this.dispersionIndicatorPercentage > targetDispersionPercentage) {
					this.dispersionIndicatorPercentage -= 0.025D;
					if(this.dispersionIndicatorPercentage < targetDispersionPercentage) {
						this.dispersionIndicatorPercentage = targetDispersionPercentage;
					}
				}

				if(BetweenlandsConfig.GENERAL.cavingRopeIndicator) {
					for(ItemStack stack : player.inventory.mainInventory) {
						if(!stack.isEmpty() && stack.getItem() == ItemRegistry.CAVING_ROPE) {
							this.cavingRopeCount += stack.getCount();
						}
					}
				}

				TObjectIntIterator<LocationStorage> titleDisplayCooldownsIT = this.titleDisplayCooldowns.iterator();
				while(titleDisplayCooldownsIT.hasNext()) {
					titleDisplayCooldownsIT.advance();
					
					int cooldown = titleDisplayCooldownsIT.value();
					if(cooldown > 1) {
						this.titleDisplayCooldowns.put(titleDisplayCooldownsIT.key(), cooldown - 1);
					} else {
						titleDisplayCooldownsIT.remove();
					}
				}
				
				if(player.dimension == BetweenlandsConfig.WORLD_AND_DIMENSION.dimensionId) {
					String prevLocation = this.currentLocation;

					List<LocationStorage> locations = getVisibleLocations(player);
					if(locations.isEmpty()) {
						String location;
						if(player.posY < WorldProviderBetweenlands.CAVE_START - 10) {
							String wildernessName = I18n.format("location.wilderness.name");
							if(this.currentLocation.equals(wildernessName)) {
								prevLocation = "";
							}
							location = I18n.format("location.caverns.name");
						} else {
							String cavernsName = I18n.format("location.caverns.name");
							if(this.currentLocation.equals(cavernsName)) {
								prevLocation = "";
							}
							location = I18n.format("location.wilderness.name");
						}
						this.currentLocation = location;
					} else {
						LocationStorage highestLocation = null;
						for(LocationStorage storage : locations) {
							if(highestLocation == null || storage.getLayer() > highestLocation.getLayer())
								highestLocation = storage;
						}
						
						int displayCooldown = 60*20; //1 minute cooldown for title
						
						int currentCooldown = this.titleDisplayCooldowns.get(highestLocation);
						
						if(currentCooldown == 0) {
							this.titleDisplayCooldowns.put(highestLocation, displayCooldown);

							if(highestLocation.hasLocalizedName()) {
								this.currentLocation = highestLocation.getLocalizedName();
							} else {
								this.currentLocation = highestLocation.getName();
							}
						} else if(currentCooldown > 0) {
							this.titleDisplayCooldowns.put(highestLocation, displayCooldown); //Keep cooldown up until player leaves location
						}
					}

					if(this.currentLocation.length() > 0) {
						if(this.currentLocation.contains(":")) {
							int startIndex = this.currentLocation.indexOf(":");
							try {
								String ticks = this.currentLocation.substring(0, startIndex);
								this.maxTitleTicks = Integer.parseInt(ticks);
								this.currentLocation = this.currentLocation.substring(startIndex+1, this.currentLocation.length());
							} catch(Exception ex) {
								this.maxTitleTicks = 80;
							}
						}
						if(prevLocation != null && !prevLocation.equals(this.currentLocation)) {
							this.titleTicks = this.maxTitleTicks;
							this.titleContainer = new TextContainer(2048, 2048, this.currentLocation, TheBetweenlands.proxy.getCustomFontRenderer());
							this.titleContainer.setCurrentScale(2.0f).setCurrentColor(0xFFFFFFFF);
							this.titleContainer.registerTag(new FormatTags.TagNewLine());
							this.titleContainer.registerTag(new FormatTags.TagScale(2.0F));
							this.titleContainer.registerTag(new FormatTags.TagSimple("bold", TextFormatting.BOLD));
							this.titleContainer.registerTag(new FormatTags.TagSimple("obfuscated", TextFormatting.OBFUSCATED));
							this.titleContainer.registerTag(new FormatTags.TagSimple("italic", TextFormatting.ITALIC));
							this.titleContainer.registerTag(new FormatTags.TagSimple("strikethrough", TextFormatting.STRIKETHROUGH));
							this.titleContainer.registerTag(new FormatTags.TagSimple("underline", TextFormatting.UNDERLINE));
							try {
								this.titleContainer.parse();
							} catch (Exception e) {
								this.titleContainer = null;
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;
		int width = event.getResolution().getScaledWidth();
		int height = event.getResolution().getScaledHeight();

		if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			/*GlStateManager.pushMatrix();

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.color(1, 1, 1, 1);

			GlStateManager.enableTexture2D();
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, ShaderHelper.INSTANCE.getWorldShader().getRepellerShieldBuffer().getDiffuseTexture());

			Tessellator t = Tessellator.getInstance();
			BufferBuilder b = t.getBuffer();
			b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

			b.pos(0, 0, 0).tex(0, 1).endVertex();
			b.pos(0, event.getResolution().getScaledHeight(), 0).tex(0, 0).endVertex();
			b.pos(event.getResolution().getScaledWidth(), event.getResolution().getScaledHeight(), 0).tex(1, 0).endVertex();
			b.pos(event.getResolution().getScaledWidth(), 0, 0).tex(1, 1).endVertex();

			t.draw();

			GlStateManager.enableBlend();

			GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

			GlStateManager.popMatrix();*/
			
			this.crawlerOverlayRenderer.render(event.getPartialTicks());

			if(BetweenlandsConfig.GENERAL.cavingRopeIndicator && player != null) {
				boolean connected = false;
				List<EntityRopeNode> ropeNodes = player.world.getEntitiesWithinAABB(EntityRopeNode.class, player.getEntityBoundingBox().grow(32, 32, 32));
				for(EntityRopeNode rope : ropeNodes) {
					if(rope.getNextNode() == player) {
						connected = true;
						break;
					}
				}
				if(connected) {
					this.cavingRopeConnectTicks = 80;

					GlStateManager.pushMatrix();
					GlStateManager.translate(width / 2, height / 2, 0);
					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
					GlStateManager.disableBlend();

					GlStateManager.pushMatrix();
					GlStateManager.translate(14, 14, 0);
					GlStateManager.scale(0.5D, 0.5D, 1);
					mc.fontRenderer.drawString(String.valueOf(this.cavingRopeCount), 0, 0, 0xFFFFFFFF);
					GlStateManager.popMatrix();

					Minecraft.getMinecraft().renderEngine.bindTexture(CAVING_ROPE_CONNECTED);

					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder buffer = tessellator.getBuffer();
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					this.renderTexturedRect(buffer, 2, 2, 18, 18, 0, 1, 0, 1);
					tessellator.draw();

					GlStateManager.enableBlend();
					GlStateManager.popMatrix();
				} else if(!connected && this.cavingRopeConnectTicks > 0) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(width / 2, height / 2, 0);
					GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
					GlStateManager.enableBlend();
					GlStateManager.color(1.0F, 1.0F, 1.0F, MathHelper.clamp(this.cavingRopeConnectTicks / 80.0F * (0.8F + 0.2F * (float)Math.sin((this.cavingRopeConnectTicks + 1 - event.getPartialTicks()) / 2.0F)), 0, 1));

					Minecraft.getMinecraft().renderEngine.bindTexture(CAVING_ROPE_DISCONNECTED);

					Tessellator tessellator = Tessellator.getInstance();
					BufferBuilder buffer = tessellator.getBuffer();
					buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
					this.renderTexturedRect(buffer, 2, 2, 18, 18, 0, 1, 0, 1);
					tessellator.draw();

					GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
					GlStateManager.popMatrix();
				}
			}
		} else if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			if(player != null && !player.isSpectator()) {
				if (BetweenlandsConfig.GENERAL.equipmentVisible) {
					IEquipmentCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_EQUIPMENT, null);
					if (capability != null) {

						EnumHandSide offhand = player.getPrimaryHand().opposite();

						int posX = 0;
						int posY = 0;

						boolean isOnOppositeSide = BetweenlandsConfig.GENERAL.equipmentHotbarSide == 1;
						boolean showOnRightSide = (offhand == EnumHandSide.LEFT) != isOnOppositeSide;

						switch (BetweenlandsConfig.GENERAL.equipmentZone) {
						default:
						case 0:
							if (showOnRightSide) {
								posX = width / 2 + 93;
								if (isOnOppositeSide && !player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
									posX += 30;
								}
							} else {
								posX = width / 2 - 93 - 16;
								if (isOnOppositeSide && !player.getHeldItem(EnumHand.OFF_HAND).isEmpty()) {
									posX -= 30;
								}
							}
							posY = height - 19;
							break;
						case 1:
							posX = 0;
							posY = 0;
							break;
						case 2:
							posX = width - 18;
							posY = 0;
							break;
						case 3:
							posX = width - 18;
							posY = height - 18;
							break;
						case 4:
							posX = 0;
							posY = height - 18;
							break;
						case 5:
							posX = 0;
							posY = height / 2;
							break;
						case 6:
							posX = width / 2;
							posY = 0;
							break;
						case 7:
							posX = width - 18;
							posY = height / 2;
							break;
						case 8:
							posX = width / 2;
							posY = height - 18;
							break;
						}

						posX += BetweenlandsConfig.GENERAL.equipmentOffsetX;
						posY += BetweenlandsConfig.GENERAL.equipmentOffsetY;

						int yOffset = 0;

						for (EnumEquipmentInventory type : EnumEquipmentInventory.VALUES) {
							IInventory inv = capability.getInventory(type);

							int xOffset = 0;

							boolean hadItem = false;

							for (int i = 0; i < inv.getSizeInventory(); i++) {
								ItemStack stack = inv.getStackInSlot(i);

								if (!stack.isEmpty()) {
									float scale = 1.0F;

									GlStateManager.pushMatrix();
									GlStateManager.translate(posX + xOffset, posY + yOffset, 0);
									GlStateManager.color(1, 1, 1, 1);
									GlStateManager.enableBlend();
									GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
									GlStateManager.scale(scale, scale, scale);

									mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 0, 0);
									mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, 0, 0, null);

									GlStateManager.disableAlpha();
									GlStateManager.disableRescaleNormal();
									GlStateManager.disableLighting();
									GlStateManager.color(1, 1, 1, 1);
									GlStateManager.enableBlend();
									GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
									GlStateManager.enableTexture2D();
									GlStateManager.color(1, 1, 1, 1);
									GlStateManager.popMatrix();

									if (showOnRightSide) {
										xOffset += BetweenlandsConfig.GENERAL.equipmentHorizontalSpacing;
									} else {
										xOffset -= BetweenlandsConfig.GENERAL.equipmentHorizontalSpacing;
									}

									hadItem = true;
								}
							}

							if (hadItem) {
								yOffset += BetweenlandsConfig.GENERAL.equipmentVerticalSpacing;
							}
						}
					}
				}

				IDecayCapability capability = player.getCapability(CapabilityRegistry.CAPABILITY_DECAY, null);

				if(capability != null && capability.isDecayEnabled()) {
					int startX = 0;
					int startY = 0;

					switch(BetweenlandsConfig.GENERAL.decayBarZone) {
					default:
					case 0:
						startX = (width / 2) - (27 / 2) + 23;
						startY = height - 49;

						//Erebus compatibility
						if (player.getEntityData().hasKey("antivenomDuration")) {
							int duration = player.getEntityData().getInteger("antivenomDuration");
							if (duration > 0) {
								startY -= 12;
							}
						}

						//TaN compatibility
						if(TheBetweenlands.isToughAsNailsModInstalled) {
							startY -= 10;
						}

						//Ridden entity hearts offset
						Entity ridingEntity = player.getRidingEntity();
						if(ridingEntity != null && ridingEntity instanceof EntityLivingBase) {
							EntityLivingBase riddenEntity = (EntityLivingBase)ridingEntity;
							float maxEntityHealth = riddenEntity.getMaxHealth();
							int maxHealthHearts = (int)(maxEntityHealth + 0.5F) / 2;
							if (maxHealthHearts > 30) {
								maxHealthHearts = 30;
							}
							int guiOffsetY = 0;
							while(maxHealthHearts > 0) {
								int renderedHearts = Math.min(maxHealthHearts, 10);
								maxHealthHearts -= renderedHearts;
								guiOffsetY -= 10;
							}
							startY += guiOffsetY;
						}

						//Air bar offset
						if(player.isInsideOfMaterial(Material.WATER)) {
							startY -= 10;
						}

						break;
					case 1:
						startX = 0;
						startY = 0;
						break;
					case 2:
						startX = width;
						startY = 0;
						break;
					case 3:
						startX = width;
						startY = height;
						break;
					case 4:
						startX = 0;
						startY = height;
						break;
					case 5:
						startX = 0;
						startY = height / 2;
						break;
					case 6:
						startX = width / 2;
						startY = 0;
						break;
					case 7:
						startX = width;
						startY = height / 2;
						break;
					case 8:
						startX = width / 2;
						startY = height;
						break;
					}

					startX += BetweenlandsConfig.GENERAL.decayBarOffsetX;
					startY += BetweenlandsConfig.GENERAL.decayBarOffsetY;

					int decay = 20 - capability.getDecayStats().getDecayLevel();

					Minecraft.getMinecraft().getTextureManager().bindTexture(DECAY_BAR_TEXTURE);

					for (int i = 0; i < 10; i++) {
						int offsetY = 0;

						if (this.updateCounter % (decay * 3 + 1) == 0) 
							offsetY += this.random.nextInt(3) - 1;

						GlStateManager.enableBlend();
						GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
						GlStateManager.color(1, 1, 1, 1);

						drawTexturedModalRect(startX + 71 - i * 8, startY + offsetY, 18, 0, 9, 9);
						if (i * 2 + 1 < decay) 
							drawTexturedModalRect(startX + 71 - i * 8, startY + offsetY, 0, 0, 9, 9);

						if (i * 2 + 1 == decay) 
							drawTexturedModalRect(startX + 72 - i * 8, startY + offsetY, 9, 0, 9, 9);
					}
				}
			}
		} else if(event.getType() == ElementType.TEXT) {
			if(this.titleTicks > 0 && this.titleContainer != null && !this.titleContainer.getPages().isEmpty()) {
				TextPage page = this.titleContainer.getPages().get(0);
				double strWidth = page.getTextWidth();
				double strHeight = page.getTextHeight();
				double strX = width / 2.0D - strWidth / 2.0F;
				double strY = height / 5.0D;
				GlStateManager.pushMatrix();
				GlStateManager.translate(strX, strY, 0);
				float fade = Math.min(1.0F, ((float)this.maxTitleTicks - (float)this.titleTicks) / Math.min(40.0F, this.maxTitleTicks - 5.0F) + 0.02F) - Math.max(0, (-this.titleTicks + 5) / 5.0F);
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
				GlStateManager.enableBlend();
				float averageScale = 0F;
				for(TextSegment segment : page.getSegments()) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(segment.x, segment.y, 0.0D);
					GlStateManager.scale(segment.scale, segment.scale, 1.0F);
					float[] rgba = ColorUtils.getRGBA(segment.color);
					segment.font.drawString(segment.text, 0, 0, ColorUtils.toHex(rgba[0], rgba[1], rgba[2], rgba[3] * fade));
					GlStateManager.color(1, 1, 1, 1);
					GlStateManager.popMatrix();
					averageScale += segment.scale;
				}
				averageScale /= page.getSegments().size();
				GlStateManager.popMatrix();
				Minecraft.getMinecraft().renderEngine.bindTexture(TITLE_TEXTURE);
				GlStateManager.color(1, 1, 1, fade);
				double sidePadding = 6;
				double yOffset = 5;
				double sy = Math.ceil(strY + strHeight - yOffset * averageScale);
				double ey = Math.ceil(strY + strHeight + (-yOffset + 16) * averageScale);
				Tessellator tessellator = Tessellator.getInstance();
				BufferBuilder buffer = tessellator.getBuffer();
				buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
				this.renderTexturedRect(buffer, strX - sidePadding*averageScale, sy, strX - sidePadding*averageScale + 9*averageScale, ey, 0, 9 / 128.0D, 0, 1);
				this.renderTexturedRect(buffer, strX - sidePadding*averageScale + 9*averageScale, sy, strX + strWidth / 2.0D - 6*averageScale, ey, 9 / 128.0D, 58 / 128.0D, 0, 1);
				this.renderTexturedRect(buffer, strX + strWidth / 2.0D - 6*averageScale, sy, strX + strWidth / 2.0D + 6*averageScale, ey, 58 / 128.0D, 70 / 128.0D, 0, 1);
				this.renderTexturedRect(buffer, strX + strWidth / 2.0D + 6*averageScale, sy, strX + strWidth + sidePadding*averageScale - 9*averageScale, ey, 70 / 128.0D, 119 / 128.0D, 0, 1);
				this.renderTexturedRect(buffer, strX + strWidth + sidePadding*averageScale - 9*averageScale, sy, strX + strWidth + sidePadding*averageScale, ey, 119 / 128.0D, 1, 0, 1);
				tessellator.draw();
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			}
		} else if(event.getType() == ElementType.PORTAL) {
			if(player != null) {
				IPortalCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_PORTAL, null);

				if(cap != null && cap.isInPortal()) {
					this.renderPortal(mc, MathHelper.clamp((1.0F - cap.getTicksUntilTeleport() / (float)PlayerPortalHandler.MAX_PORTAL_TIME), 0, 1), event.getResolution());
				}
			}
		}
	}

	@SubscribeEvent
	public void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
		if(event.getType() == ElementType.ALL) {
			Minecraft mc = Minecraft.getMinecraft();
			EntityPlayer player = mc.player;

			if(player != null) {
				this.renderDispersionRingOverlay(mc, player, true, event.getPartialTicks());
			}
		}
	}

	//Render the untextured black quad in world s.t. it can't be disabled
	//by F1 or other GUIs
	@SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player;

		if(player != null) {
			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.pushMatrix();

			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.pushMatrix();

			//Same as mc.entityRenderer.setupOverlayRendering(); but without clearing depth
			ScaledResolution scaledresolution = new ScaledResolution(mc);
	        GlStateManager.matrixMode(GL11.GL_PROJECTION);
	        GlStateManager.loadIdentity();
	        GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(), 0.0D, 1000.0D, 3000.0D);
	        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
	        GlStateManager.loadIdentity();
	        GlStateManager.translate(0.0F, 0.0F, -2000.0F);

			this.renderDispersionRingOverlay(mc, player, false, event.getPartialTicks());

			GlStateManager.disableBlend();

			GlStateManager.matrixMode(GL11.GL_PROJECTION);
			GlStateManager.popMatrix();

			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.popMatrix();
		}
	}

	private void renderDispersionRingOverlay(Minecraft mc, EntityPlayer player, boolean guiOverlay, float partialTicks) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		ScaledResolution res = new ScaledResolution(mc);

		GlStateManager.disableAlpha();
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		GlStateManager.disableDepth();

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		IEntityCustomCollisionsCapability cap = player.getCapability(CapabilityRegistry.CAPABILITY_ENTITY_CUSTOM_BLOCK_COLLISIONS, null);

		if(cap != null) {
			double viewFadeStartDistance = Math.min(cap.getViewObstructionCheckDistance(), 0.25D);
			double viewObstructionDistance = cap.getViewObstructionDistance();

			if(viewObstructionDistance < viewFadeStartDistance) {
				float alpha = (float) Math.min(1, Math.max(0, this.prevObstructionPercentage + (this.obstructionPercentage - this.prevObstructionPercentage) * partialTicks));

				GlStateManager.color(0, 0, 0, alpha);

				if(!guiOverlay) {
					bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
					bufferbuilder.pos(0.0D, (double)res.getScaledHeight_double(), 0.0D).endVertex();
					bufferbuilder.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), 0.0D).endVertex();
					bufferbuilder.pos((double)res.getScaledWidth_double(), 0.0D, 0.0D).endVertex();
					bufferbuilder.pos(0.0D, 0.0D, 0.0D).endVertex();
					tessellator.draw();
				} else {
					bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
					bufferbuilder.pos(0.0D, (double)res.getScaledHeight_double(), 0.0D).endVertex();
					bufferbuilder.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), 0.0D).endVertex();
					bufferbuilder.pos((double)res.getScaledWidth_double(), 0.0D, 0.0D).endVertex();
					bufferbuilder.pos(0.0D, 0.0D, 0.0D).endVertex();
					tessellator.draw();

					float brightness = 0.5F + (1 - alpha) * 0.5F;

					Framebuffer dispersionWorldFbo = null;

					//Render some blocks around player
					GlStateManager.matrixMode(GL11.GL_PROJECTION);
					GlStateManager.pushMatrix();

					GlStateManager.matrixMode(GL11.GL_MODELVIEW);
					GlStateManager.pushMatrix();

					mc.entityRenderer.setupCameraTransform(partialTicks, 0);

					this.ringOfDispersionWorldRenderer.setPos(new BlockPos(player.getPositionVector()));
					this.ringOfDispersionWorldRenderer.setWorld(player.world);

					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					GlStateManager.enableDepth();
					GlStateManager.depthMask(true);

					Framebuffer mainFbo = mc.getFramebuffer();

					dispersionWorldFbo = this.ringOfDispersionWorldFramebuffer.getFramebuffer(mainFbo.framebufferWidth, mainFbo.framebufferHeight);

					dispersionWorldFbo.framebufferClear();
					dispersionWorldFbo.bindFramebuffer(true);

					this.ringOfDispersionWorldRenderer.render();

					mainFbo.bindFramebuffer(true);

					GlStateManager.disableDepth();
					GlStateManager.depthMask(false);

					GlStateManager.matrixMode(GL11.GL_PROJECTION);
					GlStateManager.popMatrix();

					GlStateManager.matrixMode(GL11.GL_MODELVIEW);
					GlStateManager.popMatrix();

					GlStateManager.enableBlend();
					GlStateManager.disableLighting();
					GlStateManager.color(brightness, brightness, brightness, alpha * alpha * alpha * 0.75F);
					GlStateManager.enableTexture2D();

					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

					//Render surrounding blocks overlay
					GlStateManager.bindTexture(dispersionWorldFbo.framebufferTexture);
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
					bufferbuilder.pos(0.0D, (double)res.getScaledHeight_double(), -90.0D).tex(0.0D, 0.0D).endVertex();
					bufferbuilder.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), -90.0D).tex(1.0D, 0.0D).endVertex();
					bufferbuilder.pos((double)res.getScaledWidth_double(), 0.0D, -90.0D).tex(1.0D, 1.0D).endVertex();
					bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 1.0D).endVertex();
					tessellator.draw();

					GlStateManager.color(brightness, brightness, brightness, alpha);
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

					//Render blinds
					float centerPieceWidth = (float)(res.getScaledHeight_double() / 2.0D);
					float centerStart = (float)(res.getScaledWidth_double() / 2.0D - centerPieceWidth / 2.0D);
					float centerEnd = (float)(res.getScaledWidth_double() / 2.0D + centerPieceWidth / 2.0D);

					mc.getTextureManager().bindTexture(RING_OF_DISPERSION_OVERLAY_TEXTURE);

					GlStateManager.pushMatrix();
					GlStateManager.translate(0, -res.getScaledHeight_double() / 2 + res.getScaledHeight_double() / 2 * alpha, 0);

					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

					//top left
					bufferbuilder.pos(0.0D, res.getScaledHeight_double() / 2, -90.0D).tex(0.0D, 0.5D).endVertex();
					bufferbuilder.pos(centerStart, res.getScaledHeight_double() / 2, -90.0D).tex(0.333D, 0.5D).endVertex();
					bufferbuilder.pos(centerStart, 0.0D, -90.0D).tex(0.333D, 0.0D).endVertex();
					bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
					//top center
					bufferbuilder.pos(centerStart, res.getScaledHeight_double() / 2, -90.0D).tex(0.333D, 0.5D).endVertex();
					bufferbuilder.pos(centerEnd, res.getScaledHeight_double() / 2, -90.0D).tex(0.666D, 0.5D).endVertex();
					bufferbuilder.pos(centerEnd, 0.0D, -90.0D).tex(0.666D, 0.0D).endVertex();
					bufferbuilder.pos(centerStart, 0.0D, -90.0D).tex(0.333D, 0.0D).endVertex();
					//top right
					bufferbuilder.pos(centerEnd, res.getScaledHeight_double() / 2, -90.0D).tex(0.666D, 0.5D).endVertex();
					bufferbuilder.pos(res.getScaledWidth_double(), res.getScaledHeight_double() / 2, -90.0D).tex(1.0D, 0.5D).endVertex();
					bufferbuilder.pos(res.getScaledWidth_double(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
					bufferbuilder.pos(centerEnd, 0.0D, -90.0D).tex(0.666D, 0.0D).endVertex();

					tessellator.draw();

					GlStateManager.popMatrix();

					GlStateManager.pushMatrix();
					GlStateManager.translate(0, res.getScaledHeight_double() / 2 - res.getScaledHeight_double() / 2 * alpha, 0);

					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);

					//bottom left
					bufferbuilder.pos(0.0D, res.getScaledHeight_double(), -90.0D).tex(0.0D, 1.0D).endVertex();
					bufferbuilder.pos(centerStart, res.getScaledHeight_double(), -90.0D).tex(0.333D, 1.0D).endVertex();
					bufferbuilder.pos(centerStart, res.getScaledHeight_double() / 2, -90.0D).tex(0.333D, 0.5D).endVertex();
					bufferbuilder.pos(0.0D, res.getScaledHeight_double() / 2, -90.0D).tex(0.0D, 0.5D).endVertex();
					//bottom center
					bufferbuilder.pos(centerStart, res.getScaledHeight_double(), -90.0D).tex(0.333D, 1.0D).endVertex();
					bufferbuilder.pos(centerEnd, res.getScaledHeight_double(), -90.0D).tex(0.666D, 1.0D).endVertex();
					bufferbuilder.pos(centerEnd, res.getScaledHeight_double() / 2, -90.0D).tex(0.666D, 0.5D).endVertex();
					bufferbuilder.pos(centerStart, res.getScaledHeight_double() / 2, -90.0D).tex(0.333D, 0.5D).endVertex();
					//bottom right
					bufferbuilder.pos(centerEnd, res.getScaledHeight_double(), -90.0D).tex(0.666D, 1.0D).endVertex();
					bufferbuilder.pos(res.getScaledWidth_double(), res.getScaledHeight_double(), -90.0D).tex(1.0D, 1.0D).endVertex();
					bufferbuilder.pos(res.getScaledWidth_double(), res.getScaledHeight_double() / 2, -90.0D).tex(1.0D, 0.5D).endVertex();
					bufferbuilder.pos(centerEnd, res.getScaledHeight_double() / 2, -90.0D).tex(0.666D, 0.5D).endVertex();

					tessellator.draw();

					GlStateManager.popMatrix();

					GlStateManager.color((1 - brightness) * 2, (1 - brightness) * 2, (1 - brightness) * 2, 1);

					//Vignette
					mc.getTextureManager().bindTexture(VIGNETTE_TEXTURE);
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
					bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
					bufferbuilder.pos(0.0D, (double)res.getScaledHeight_double(), -90.0D).tex(0.0D, 1.0D).endVertex();
					bufferbuilder.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), -90.0D).tex(1.0D, 1.0D).endVertex();
					bufferbuilder.pos((double)res.getScaledWidth_double(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
					bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
					tessellator.draw();
					GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
				}
			}
		}

		GlStateManager.depthMask(false);
		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

		if(guiOverlay) {
			float indicatorAlpha = (float)(this.prevDispersionIndicatorPercentage + (this.dispersionIndicatorPercentage - this.prevDispersionIndicatorPercentage) * partialTicks);
	
			if(indicatorAlpha > 0.01F) {
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.01F);
	
				GlStateManager.color(indicatorAlpha, indicatorAlpha, indicatorAlpha, indicatorAlpha);
	
				//Indicator overlay
				mc.getTextureManager().bindTexture(RING_OF_DISPERSION_INDICATOR_OVERLAY_TEXTURE);
				bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
				bufferbuilder.pos(0.0D, (double)res.getScaledHeight_double(), -90.0D).tex(0.0D, 1.0D).endVertex();
				bufferbuilder.pos((double)res.getScaledWidth_double(), (double)res.getScaledHeight_double(), -90.0D).tex(1.0D, 1.0D).endVertex();
				bufferbuilder.pos((double)res.getScaledWidth_double(), 0.0D, -90.0D).tex(1.0D, 0.0D).endVertex();
				bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 0.0D).endVertex();
				tessellator.draw();
	
				GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
			}
		}

		GlStateManager.color(1, 1, 1, 1);

		GlStateManager.enableTexture2D();
		GlStateManager.enableAlpha();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
	}

	protected void renderPortal(Minecraft mc, float timeInPortal, ScaledResolution scaledRes)
	{
		if (timeInPortal < 1.0F) {
			timeInPortal = timeInPortal * timeInPortal;
			timeInPortal = timeInPortal * 0.8F + 0.2F;
		}

		GlStateManager.disableAlpha();
		GlStateManager.disableDepth();
		GlStateManager.depthMask(false);
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.color(1.0F, 1.0F, 1.0F, timeInPortal);
		mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		TextureAtlasSprite textureatlassprite = mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(BlockRegistry.TREE_PORTAL.getDefaultState());
		float f = textureatlassprite.getMinU();
		float f1 = textureatlassprite.getMinV();
		float f2 = textureatlassprite.getMaxU();
		float f3 = textureatlassprite.getMaxV();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos(0.0D, (double)scaledRes.getScaledHeight(), -90.0D).tex((double)f, (double)f3).endVertex();
		bufferbuilder.pos((double)scaledRes.getScaledWidth(), (double)scaledRes.getScaledHeight(), -90.0D).tex((double)f2, (double)f3).endVertex();
		bufferbuilder.pos((double)scaledRes.getScaledWidth(), 0.0D, -90.0D).tex((double)f2, (double)f1).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex((double)f, (double)f1).endVertex();
		tessellator.draw();
		GlStateManager.depthMask(true);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	}

	private void renderTexturedRect(BufferBuilder buffer, double x, double y, double x2, double y2, double umin, double umax, double vmin, double vmax) {
		buffer.pos(x, y2, 0.0D).tex(umin, vmax).endVertex();
		buffer.pos(x2, y2, 0.0D).tex(umax, vmax).endVertex();
		buffer.pos(x2, y, 0.0D).tex(umax, vmin).endVertex();
		buffer.pos(x, y, 0.0D).tex(umin, vmin).endVertex();
	}

	@SubscribeEvent
	public void onRenderScreen(RenderTooltipEvent.PostText event) {
		if(GuiScreen.isShiftKeyDown()) {
			Minecraft mc = Minecraft.getMinecraft();
			FontRenderer fontRenderer = mc.fontRenderer;

			int yOffset = 0;
			int width = 0;

			List<Aspect> aspects = ItemAspectContainer.fromItem(event.getStack(), AspectManager.get(mc.world)).getAspects(mc.player);

			if(aspects != null && aspects.size() > 0) {
				GlStateManager.pushMatrix();

				GlStateManager.enableDepth();

				GlStateManager.translate(event.getX(), event.getY() - 29, 300);

				for(Aspect aspect : aspects) {
					String aspectText = aspect.type.getName() + " (" + aspect.getRoundedDisplayAmount() + ")";
					String aspectTypeText = aspect.type.getType();

					GlStateManager.color(1, 1, 1, 1);

					fontRenderer.drawString(aspectText, 6 + 17, 2 + yOffset, 0xFFFFFFFF);
					fontRenderer.drawString(aspectTypeText, 6 + 17, 2 + 9 + yOffset, 0xFFFFFFFF);

					AspectIconRenderer.renderIcon(2, 2 + yOffset, 16, 16, aspect.type.getIcon());

					int entryWidth = Math.max(fontRenderer.getStringWidth(aspectText), fontRenderer.getStringWidth(aspectTypeText)) + 26;
					if(entryWidth > width) {
						width = entryWidth;
					}

					yOffset -= 21;
				}

				GlStateManager.translate(0, 0, -1);

				int height = -yOffset;
				int rectX = 0;
				int rectY = yOffset + 20;

				int backgroundColor = 0xF0100010;
				int borderColorStart = 0x505000FF;
				int borderColorEnd = (borderColorStart & 0xFEFEFE) >> 1 | borderColorStart & 0xFF000000;
				GuiUtils.drawGradientRect(0, rectX - 3,           rectY - 4,           rectX + width + 3,   rectY - 3,              backgroundColor, backgroundColor);
				GuiUtils.drawGradientRect(0, rectX - 3,           rectY + height + 3,  rectX + width + 3,   rectY + height + 4,     backgroundColor, backgroundColor);
				GuiUtils.drawGradientRect(0, rectX - 3,           rectY - 3,           rectX + width + 3,   rectY + height + 3,     backgroundColor, backgroundColor);
				GuiUtils.drawGradientRect(0, rectX - 4,           rectY - 3,           rectX - 3,           rectY + height + 3,     backgroundColor, backgroundColor);
				GuiUtils.drawGradientRect(0, rectX + width + 3,   rectY - 3,           rectX + width + 4,   rectY + height + 3,     backgroundColor, backgroundColor);
				GuiUtils.drawGradientRect(0, rectX - 3,           rectY - 3 + 1,       rectX - 3 + 1,       rectY + height + 3 - 1, borderColorStart, borderColorEnd);
				GuiUtils.drawGradientRect(0, rectX + width + 2,   rectY - 3 + 1,       rectX + width + 3,   rectY + height + 3 - 1, borderColorStart, borderColorEnd);
				GuiUtils.drawGradientRect(0, rectX - 3,           rectY - 3,           rectX + width + 3,   rectY - 3 + 1,          borderColorStart, borderColorStart);
				GuiUtils.drawGradientRect(0, rectX - 3,           rectY + height + 2,  rectX + width + 3,   rectY + height + 3,     borderColorEnd, borderColorEnd);

				GlStateManager.color(1, 1, 1, 1);

				GlStateManager.popMatrix();
			}
		}
	}
}

package thebetweenlands.client.gui;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.render.shader.MainShader;
import thebetweenlands.client.render.shader.ShaderHelper;
import thebetweenlands.client.render.shader.effect.DeferredEffect;
import thebetweenlands.client.render.shader.effect.StarfieldEffect;
import thebetweenlands.decay.DecayManager;
import thebetweenlands.entities.mobs.boss.IBossBL;
import thebetweenlands.entities.properties.BLEntityPropertiesRegistry;
import thebetweenlands.entities.properties.list.EntityPropertiesCircleGem;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.event.debugging.DebugHandlerClient;
import thebetweenlands.items.BLItemRegistry;
import thebetweenlands.utils.ItemRenderHelper;

@SideOnly(Side.CLIENT)
public class GuiOverlay extends Gui {
	public static final GuiOverlay INSTANCE = new GuiOverlay();

	private ResourceLocation decayBarTexture = new ResourceLocation("thebetweenlands:textures/gui/decayBar.png");
	private ResourceLocation bossBarTexture = new ResourceLocation("thebetweenlands:textures/gui/bossHealthBar.png");
	private Minecraft mc = Minecraft.getMinecraft();
	private Random random = new Random();

	private int updateCounter;

	private DeferredEffect de = null;
	private Framebuffer tb1 = null;

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(event.phase == Phase.START) {
			this.updateCounter++;
		}
	}

	@SubscribeEvent
	public void renderGui(RenderGameOverlayEvent.Post event) {
		//GLUProjection test
		/*for(Entity e : (List<Entity>)Minecraft.getMinecraft().theWorld.loadedEntityList) {
			Projection p = GLUProjection.getInstance().project(e.posX - Minecraft.getMinecraft().thePlayer.posX, e.posY - Minecraft.getMinecraft().thePlayer.posY, e.posZ - Minecraft.getMinecraft().thePlayer.posZ, ClampMode.NONE, false);
			if(p.isType(Type.INSIDE)) Gui.drawRect((int)p.getX(), (int)p.getY(), (int)p.getX() + 5, (int)p.getY() + 5, 0xFFFF0000);
		}*/

		/*try {
			String str1 = "This might be a <tooltip:hehe haha hoho> <color:0x800000> long </color> </tooltip> text but it also might not  ";
			String str2 = "This might be a <tooltip:hehe haha hoho><color:0x800000> long </color> </tooltip> text but it also might not  ";
			String str3 = "<a>no<b>word<c>wrap</a>this</b>time</c>";
			String str4 = "This might be a <tooltip:hehe haha hoho><color:0x800000> long </color> </tooltip> text <a>but<b>it</b>also</a>might not  ";
			String str5 = "This is <scale:0.5>scaled</scale> but not anymore now. Now it's <color:0xFF0000>colored!</color> and this is <underline>underlined</underline>. <nl> Now it's <underline>underlined and <color:0x00FF00>colored and <bold>bold at the same time and even <scale:2>scaled! </scale>and it still </bold>works properly </color> like</underline> expected. This is a new page by the way. And this is a <tooltip:Hi!>tooltip. <scale:0.75>Yay!</scale></tooltip> <font:custom>Custom fonts also work btw</font>.";
			String str6 = "Escaping now also works: \\<font:custom\\><nl>But you can also escape the escape character: \\ <np>Additionally you can now also <font:custom>change the font inline and<font:default> add a new page wherever you want.<np><scale:0.5> Unnecessary spacing is automatically discarded when wrapping words, but can be forced by manually adding a new line with \\<nl\\> if you don't want them to be discarded. E.g. like this: \\<nl\\><nl>   - 1 \\<nl\\><nl>   - 2 \\<nl\\><nl>   - 3 \\<nl\\><nl> The space discarding comes in handy when underlining words: <underline>This is an underlined text.</underline><nl>Without space discarding it would look like this in a bad case: <underline>This is an     <nl>    underlined text.</underline><nl>And that doesn't look nice</scale>";
			TextContainer container = new TextContainer(100, 200, str6, Minecraft.getMinecraft().fontRenderer);

			container.setCurrentScale(1.0F).setCurrentColor(0x808080);
			container.registerTag(new TagNewLine());
			container.registerTag(new TagScale(1.0F));
			container.registerTag(new TagColor(0x808080));
			container.registerTag(new TagTooltip("N/A"));
			container.registerTag(new TagSimple("bold", EnumChatFormatting.BOLD));
			container.registerTag(new TagSimple("obfuscated", EnumChatFormatting.OBFUSCATED));
			container.registerTag(new TagSimple("italic", EnumChatFormatting.ITALIC));
			container.registerTag(new TagSimple("strikethrough", EnumChatFormatting.STRIKETHROUGH));
			container.registerTag(new TagSimple("underline", EnumChatFormatting.UNDERLINE));
			container.registerTag(new TagPagelink());
			container.registerTag(new TagRainbow());
			container.registerTag(new TagFont());
			container.registerTag(new TagNewPage());

			container.parse();

			int xOff = 0;
			for(thebetweenlands.manual.widgets.text.TextContainer.TextPage page : container.getPages()) {
				page.render(50 + (xOff += 104), 2);
				//page.renderBounds(50D + xOff, 2);
			}
		} catch(Exception ex) {
			ex.printStackTrace();
		}*/

		//Just some shader debugging stuff. Applies gaussian blur to the top half of the screen
		if(DebugHandlerClient.INSTANCE.debugDeferredEffect && event.type == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			if(ShaderHelper.INSTANCE.canUseShaders()) {
				MainShader shader = ShaderHelper.INSTANCE.getCurrentShader();
				if(shader != null) {
					if(this.tb1 == null) {
						this.tb1 = new Framebuffer(Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight, false);
						this.de = new StarfieldEffect(true).init();
					} else {
						if(this.tb1.framebufferWidth != Minecraft.getMinecraft().displayWidth / 2 || this.tb1.framebufferHeight != Minecraft.getMinecraft().displayHeight / 2) {
							this.tb1.deleteFramebuffer();
							this.tb1 = new Framebuffer(Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2, false);
						}
					}

					((StarfieldEffect)this.de).setTimeScale(0.000003F);

					this.de.create(shader.getBlitBuffer("starfieldBlitBuffer"))
					.setSource(this.tb1.framebufferTexture)
					.setPreviousFBO(Minecraft.getMinecraft().getFramebuffer())
					.setRenderDimensions(Minecraft.getMinecraft().displayWidth / 2, Minecraft.getMinecraft().displayHeight / 2)
					.render();

					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_BLEND);
					ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
					GL11.glPushMatrix();
					GL11.glTranslated(0, 60, 0);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, shader.getBlitBuffer("starfieldBlitBuffer").framebufferTexture);
					GL11.glBegin(GL11.GL_TRIANGLES);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glTexCoord2d(0, 0);
					GL11.glVertex2d(0, 128*5);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128*5, 128*5);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128*5, 128*5);
					GL11.glTexCoord2d(1, 1);
					GL11.glVertex2d(128*5, 0);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glEnd();

					/*GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glEnable(GL11.GL_BLEND);
					ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
					GL11.glPushMatrix();
					GL11.glTranslated(0, 60, 0);
					GL11.glColor4f(0.0F, 0.8F, 0.25F, 1.0F);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, ShaderHandler.INSTANCE.getGasTextureID());
					GL11.glBegin(GL11.GL_TRIANGLES);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glTexCoord2d(0, 0);
					GL11.glVertex2d(0, 128);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128, 128);
					GL11.glTexCoord2d(1, 0);
					GL11.glVertex2d(128, 128);
					GL11.glTexCoord2d(1, 1);
					GL11.glVertex2d(128, 0);
					GL11.glTexCoord2d(0, 1);
					GL11.glVertex2d(0, 0);
					GL11.glEnd();*/
					GL11.glPopMatrix();
				}
			}
		}

		if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
			int width = event.resolution.getScaledWidth();
			int height = event.resolution.getScaledHeight();

			/*EntityPropertiesCircleGem property = BLEntityPropertiesRegistry.HANDLER.getProperties(mc.thePlayer, EntityPropertiesCircleGem.class);
			if(property != null && property.getAmulets().size() > 0) {
				for(int a = 0; a < property.getAmulets().size(); a++) {
					EntityAmulet amulet = property.getAmulets().get(a);
					CircleGem entityGem = amulet.getAmuletGem();
					ItemStack gemItem = ItemAmulet.createStack(entityGem);
					GL11.glPushMatrix();
					int posX = (width / 2) - (27 / 2) + 115 + a * 8;
					int posY = height - 8;
					GL11.glTranslated(posX, posY, 0);
					GL11.glColor4f(1, 1, 1, 1);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glScaled(-10, -10, 10);
					float scale = ((float) Math.cos(mc.thePlayer.ticksExisted / 5.0F) + 1.0F) / 15.0F + 1.05F;
					GL11.glScaled(scale, scale, scale);
					for(int i = 0; i < gemItem.getItem().getRenderPasses(gemItem.getItemDamage()); i++) {
						ItemRenderHelper.renderItem(gemItem, i);
					}
					GL11.glPopMatrix();
				}
			}*/
			EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(this.mc.thePlayer);
			int yOffset = 0;
			for(EnumEquipmentCategory category : EnumEquipmentCategory.TYPES) {
				List<Equipment> equipmentList = equipmentInventory.getEquipment(category);
				int posX = (width / 2) - (20) + 113;
				int posY = height - 19 + yOffset;
				if(equipmentList.size() > 0) {
					if(category == EnumEquipmentCategory.AMULET) {
						EntityPropertiesCircleGem properties = BLEntityPropertiesRegistry.HANDLER.getProperties(this.mc.thePlayer, EntityPropertiesCircleGem.class);
						if(properties != null) {
							for(int a = 0; a < properties.getAmuletSlots(); a++) {
								GL11.glPushMatrix();
								GL11.glTranslated(posX, posY, 0);
								GL11.glColor4f(1, 1, 1, 1);
								GL11.glEnable(GL11.GL_BLEND);
								GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
								float scale = 1.0F;
								GL11.glScaled(scale, scale, scale);
								GL11.glColor4f(0, 0, 0, 0.4F);
								ItemRenderHelper.drawItemStack(new ItemStack(BLItemRegistry.amulet), 0, 0, null, false);
								GL11.glColor4f(1, 1, 1, 1);
								GL11.glPopMatrix();
								posX += 8;
							}
						}
					}
					posX = (width / 2) - (20) + 113;
					for(int a = 0; a < equipmentList.size(); a++) {
						Equipment equipment = equipmentList.get(a);
						GL11.glPushMatrix();
						GL11.glTranslated(posX, posY, 0);
						GL11.glColor4f(1, 1, 1, 1);
						GL11.glEnable(GL11.GL_BLEND);
						GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
						float scale = 1.0F;
						GL11.glScaled(scale, scale, scale);
						ItemRenderHelper.drawItemStack(equipment.item, 0, 0, null, true);
						GL11.glColor4f(1, 1, 1, 1);
						GL11.glPopMatrix();
						posX += 8;
					}
					yOffset -= 13;
				}
			}

			if (DecayManager.isDecayEnabled(mc.thePlayer)) {
				int startX = (width / 2) - (27 / 2) + 23;
				int startY = height - 49;

				//Erebus compatibility
				if (mc.thePlayer.getEntityData().hasKey("antivenomDuration")) {
					int duration = mc.thePlayer.getEntityData().getInteger("antivenomDuration");
					if (duration > 0) {
						startY -= 12;
					}
				}

				//Ridden entity hearts offset
				Entity ridingEntity = mc.thePlayer.ridingEntity;
				if(ridingEntity != null && ridingEntity instanceof EntityLivingBase) {
					EntityLivingBase riddenEntity = (EntityLivingBase)ridingEntity;
					int entityHealth = (int)Math.ceil((double)riddenEntity.getHealth());
					float maxEntityHealth = riddenEntity.getMaxHealth();
					int maxHealthHearts = (int)(maxEntityHealth + 0.5F) / 2;
					if (maxHealthHearts > 30) {
						maxHealthHearts = 30;
					}
					int guiOffsetY = 0;
					for (int column = 0; maxHealthHearts > 0; column += 20) {
						int renderedHearts = Math.min(maxHealthHearts, 10);
						maxHealthHearts -= renderedHearts;
						guiOffsetY -= 10;
					}
					startY += guiOffsetY + 10;
				}

				int decayLevel = DecayManager.getDecayLevel(mc.thePlayer);

				mc.getTextureManager().bindTexture(decayBarTexture);

				GL11.glEnable(GL11.GL_BLEND);
				for (int i = 0; i < 10; i++) {
					int offsetY = mc.thePlayer.isInsideOfMaterial(Material.water) ? -10 : 0;

					if (updateCounter % (decayLevel * 3 + 1) == 0) offsetY += random.nextInt(3) - 1;

					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glColor4f(1, 1, 1, 1);

					drawTexturedModalRect(startX + 71 - i * 8, startY + offsetY, 18, 0, 9, 9);
					if (i * 2 + 1 < decayLevel) drawTexturedModalRect(startX + 71 - i * 8, startY + offsetY, 0, 0, 9, 9);
					if (i * 2 + 1 == decayLevel) drawTexturedModalRect(startX + 72 - i * 8, startY + offsetY, 9, 0, 9, 9);
				}
			}
		} else if(event.type == ElementType.BOSSHEALTH) {
			Entity boss = null;
			for(Entity entity : (List<Entity>) mc.theWorld.loadedEntityList) {
				if(entity instanceof IBossBL) {
					if(boss == null || entity.getDistanceToEntity(mc.thePlayer) < boss.getDistanceToEntity(mc.thePlayer))
						boss = entity;
				}
			}
			if(boss != null) {
				float maxHealth = ((IBossBL) boss).getMaxBossHealth();
				float health = ((IBossBL) boss).getBossHealth();
				IChatComponent name = ((IBossBL) boss).getBossName();
				mc.getTextureManager().bindTexture(bossBarTexture);
				int texWidth = 256;
				int texHeight = 32/2;
				double renderWidth = 250;
				double renderHeight = (double)texHeight / (double)texWidth * renderWidth;
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				GL11.glPushMatrix();
				GL11.glTranslated(event.resolution.getScaledWidth() / 2 - renderWidth / 2.0D, 10, 0);
				GL11.glBegin(GL11.GL_QUADS);
				//Background
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex2d(0, 0);
				GL11.glTexCoord2f(0, 0.5F);
				GL11.glVertex2d(0, renderHeight);
				GL11.glTexCoord2f(1, 0.5F);
				GL11.glVertex2d(renderWidth, renderHeight);
				GL11.glTexCoord2f(1, 0);
				GL11.glVertex2d(renderWidth, 0);
				//Foreground
				GL11.glTexCoord2f(0, 0.5F);
				GL11.glVertex2d(0, 0);
				GL11.glTexCoord2f(0, 1.0F);
				GL11.glVertex2d(0, renderHeight);
				GL11.glTexCoord2f(16.0F/texWidth + (1.0F-16.0F/texWidth) / maxHealth * health, 1.0F);
				GL11.glVertex2d(renderWidth - (renderWidth-16.0F/texWidth*renderWidth-(renderWidth-16.0F/texWidth*renderWidth) / maxHealth * health), renderHeight);
				GL11.glTexCoord2f(16.0F/texWidth + (1.0F-16.0F/texWidth) / maxHealth * health, 0.5F);
				GL11.glVertex2d(renderWidth - (renderWidth-16.0F/texWidth*renderWidth-(renderWidth-16.0F/texWidth*renderWidth) / maxHealth * health), 0);
				GL11.glEnd();
				GL11.glPopMatrix();
				int strWidth = TheBetweenlands.proxy.getCustomFontRenderer().getStringWidth(name.getFormattedText());
				TheBetweenlands.proxy.getCustomFontRenderer().drawString(name.getFormattedText(), event.resolution.getScaledWidth() / 2 - strWidth / 2, 13, 0xFFFFFFFF);
			}
		}
	}
}

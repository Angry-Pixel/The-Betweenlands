package thebetweenlands.client.event.handler;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.entity.capability.DecayEntityCapability;
import thebetweenlands.common.entity.capability.IDecayCapability;

public class ScreenRenderHandler extends Gui {
	private ScreenRenderHandler() { }

	public static ScreenRenderHandler INSTANCE = new ScreenRenderHandler();

	private static final ResourceLocation DECAY_BAR_TEXTURE = new ResourceLocation("thebetweenlands:textures/gui/decay_bar.png");

	private Random random = new Random();
	private int updateCounter;

	@SubscribeEvent
	public void renderGui(RenderGameOverlayEvent.Post event) {
		if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			this.updateCounter++;

			int width = event.getResolution().getScaledWidth();
			int height = event.getResolution().getScaledHeight();

			/*EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(this.mc.thePlayer);
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
			}*/

			EntityPlayer player = Minecraft.getMinecraft().thePlayer;

			if (player != null && player.hasCapability(DecayEntityCapability.CAPABILITY, null)) {
				IDecayCapability capability = player.getCapability(DecayEntityCapability.CAPABILITY, null);

				if(capability.isDecayEnabled()) {
					int startX = (width / 2) - (27 / 2) + 23;
					int startY = height - 49;

					//Erebus compatibility
					if (player.getEntityData().hasKey("antivenomDuration")) {
						int duration = player.getEntityData().getInteger("antivenomDuration");
						if (duration > 0) {
							startY -= 12;
						}
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
						startY += guiOffsetY + 10;
					}

					int decay = 20 - capability.getDecayStats().getDecayLevel();

					Minecraft.getMinecraft().getTextureManager().bindTexture(DECAY_BAR_TEXTURE);

					for (int i = 0; i < 10; i++) {
						int offsetY = player.isInsideOfMaterial(Material.WATER) ? -10 : 0;

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
		}
	}
}

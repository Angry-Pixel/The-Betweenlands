package thebetweenlands.event.input.radialmenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import thebetweenlands.TheBetweenlands;
import thebetweenlands.client.input.KeyBindingsBL;
import thebetweenlands.entities.properties.list.equipment.EnumEquipmentCategory;
import thebetweenlands.entities.properties.list.equipment.Equipment;
import thebetweenlands.entities.properties.list.equipment.EquipmentInventory;
import thebetweenlands.forgeevent.entity.EquipmentChangeEvent;
import thebetweenlands.items.IEquippable;
import thebetweenlands.utils.GuiUtil;
import thebetweenlands.utils.RenderUtils;

@SideOnly(Side.CLIENT)
public class RadialMenuHandler {
	public static final RadialMenuHandler INSTANCE = new RadialMenuHandler();

	private boolean isOpen = false;

	private boolean repositionMouse = false;
	private int prevMouseX;
	private int prevMouseY;
	private boolean[] mouseButtons;
	private boolean scheduleMenuUpdate = false;


	////// Input ///////

	@SubscribeEvent
	public void onKeyInput(InputEvent.KeyInputEvent event) {
		if(KeyBindingsBL.radialMenu.isPressed()) {
			KeyBinding.setKeyBindState(KeyBindingsBL.radialMenu.getKeyCode(), false);
			if(!this.isOpen) {
				this.openGUI();
			} else {
				this.closeGUI();
			}
		}
	}

	@SubscribeEvent
	public void onMouseInput(MouseEvent event) {
		if(this.isOpen) {
			event.setCanceled(true);
			if(Minecraft.getMinecraft().inGameHasFocus) {
				Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
				Minecraft.getMinecraft().inGameHasFocus = false;
				this.repositionMouse = true;
			}
			if(this.mouseButtons == null || this.mouseButtons.length < Mouse.getButtonCount()) {
				this.mouseButtons = new boolean[Mouse.getButtonCount()];
			}
			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
			double mouseX = (this.prevMouseX * res.getScaledWidth()) / Minecraft.getMinecraft().displayWidth;
			double mouseY = res.getScaledHeight() - (this.prevMouseY * res.getScaledHeight()) / Minecraft.getMinecraft().displayHeight;
			for(int i = 0; i < Mouse.getButtonCount(); i++) {
				if(Mouse.isButtonDown(i)) {
					if(!this.mouseButtons[i]) {
						this.mouseButtons[i] = true;
						this.onMouseClick((int)mouseX, (int)mouseY, i);
					}
				} else {
					this.mouseButtons[i] = false;
				}
			}
		}
	}

	public void openGUI() {
		this.isOpen = true;
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		Minecraft.getMinecraft().mouseHelper.ungrabMouseCursor();
		Minecraft.getMinecraft().inGameHasFocus = false;
		this.prevMouseX = Mouse.getX();
		this.prevMouseY = Mouse.getY();

		this.updateMenu();
	}

	public void closeGUI() {
		this.isOpen = false;
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
		Minecraft.getMinecraft().setIngameFocus();
	}

	public boolean isOpen() {
		return this.isOpen;
	}

	public void scheduleMenuUpdate() {
		this.scheduleMenuUpdate = true;
	}

	public void updateMenu() {
		int prevIndex = this.currentCategory != null ? this.currentCategory.index : 0;
		this.rootCategory.categories.clear();
		this.currentCategory = this.rootCategory;
		this.lastCategories.clear();

		EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
		if(player != null) {
			IInventory inventory = player.inventory;
			EquipmentInventory equipmentInventory = EquipmentInventory.getEquipmentInventory(player);
			List<Category> categories = new ArrayList<Category>();
			for(int c = 0; c < EnumEquipmentCategory.TYPES.length; c++) {
				for(int i = 0; i < inventory.getSizeInventory(); i++) {
					ItemStack stack = inventory.getStackInSlot(i);
					if(stack != null && stack.getItem() instanceof IEquippable) {
						IEquippable equippable = (IEquippable) stack.getItem();
						if(equippable.getEquipmentCategory(stack) == EnumEquipmentCategory.TYPES[c]) {
							if(equippable.canEquip(stack, player, player, equipmentInventory)) {
								categories.add(new Categories.EquipCategory(StatCollector.translateToLocal("equipment.menu.equip"), 0x6010AA10, 0xDD10AA10, stack, i));
							}
						}
					}
				}
			}
			for(Equipment equipment : equipmentInventory.getEquipment()) {
				if(equipment.equippable.canUnequip(equipment.item, player, player, equipmentInventory)) {
					categories.add(new Categories.UnequipCategory(StatCollector.translateToLocal("equipment.menu.unequip"), 0x60AA1010, 0xDDAA1010, equipment.item, equipmentInventory.getEquipment().indexOf(equipment)));
				}
			}
			int index = 0;
			int page = 1;
			int categoryLimit = 10;
			Category currentCategory = this.rootCategory;
			List<Category> pages = new ArrayList<Category>();
			for(Category category : categories) {
				if(currentCategory.getCategories().size() > categoryLimit) {
					page++;
					Category newPage = new Category(String.format(StatCollector.translateToLocal("equipment.menu.page"), page), 0x80101010, 0xEE202020);
					currentCategory.addCategory(newPage);
					currentCategory = newPage;
					pages.add(newPage);
				}
				currentCategory.addCategory(category);
			}
			List<Category> allCategories = new ArrayList<Category>();
			allCategories.addAll(categories);
			allCategories.addAll(pages);
			for(Category category : allCategories) {
				category.index = ++index;
			}
			for(Category category : allCategories) {
				if(category.index == prevIndex && !category.getCategories().isEmpty()) {
					Category parent = category;
					while((parent = parent.parent) != null) {
						this.lastCategories.add(parent);
					}
					Collections.reverse(this.lastCategories);
					this.currentCategory = category;
				}
			}
		}

		//this.displayedCategories = 0;
		this.displayedCategories = this.currentCategory.getCategories().size();
	}

	@SubscribeEvent
	public void onEquipmentChange(EquipmentChangeEvent event) {
		if(this.isOpen && event.entity == TheBetweenlands.proxy.getClientPlayer()) {
			this.scheduleMenuUpdate();
		}
	}

	////// GUI ///////

	public static class Category {
		private Category parent;
		private int index;
		private List<Category> categories = new ArrayList<Category>();
		private String name;
		private int color;
		private int highlightColor;

		public Category(String name, int color, int highlightColor) {
			this.name = name;
			this.color = color;
			this.highlightColor = highlightColor;
		}

		public int getColor() {
			return this.color;
		}

		public int getHighlightColor() {
			return this.highlightColor;
		}

		public String getName() {
			return this.name;
		}

		public List<Category> getCategories() {
			return this.categories;
		}

		public Category getCategory(String name) {
			Category category = null;
			for(Category cCategory : this.categories) {
				if(cCategory.name.equalsIgnoreCase(name)) {
					category = cCategory;
					break;
				}
			}
			return category;
		}

		public Category addCategory(Category category) {
			category.parent = this;
			this.categories.add(category);
			return this;
		}

		public Category getByName(String name) {
			Category category = null;
			for(Category cCategory : this.categories) {
				if(cCategory.name.equalsIgnoreCase(name)) {
					category = cCategory;
					break;
				}
			}
			return category;
		}

		public void setName(String name) {
			this.name = name;
		}

		public void setColor(int color) {
			this.color = color;
		}

		public void setHighlightColor(int color) {
			this.highlightColor = color;
		}

		public void renderCategory(double centerX, double centerY, double dirX, double dirY, double radius, double startX, double startY, double angle, double segmentAngle) { }

		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			return false;
		}
	}

	private Category rootCategory = new Category("Root", 0x30101010, 0x30101010);
	private Category currentCategory;
	private List<Category> lastCategories = new ArrayList<Category>();
	private int radius = 50;
	private int innerRadius = 15;
	private int guiX;
	private int guiY;
	private int displayedCategories = 0;
	private int updateCounter;

	private List<ItemStack> equippables = new ArrayList<ItemStack>();

	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if(this.isOpen) {
			if(event.phase == Phase.START) {
				if(this.repositionMouse) {
					Mouse.setCursorPosition(this.prevMouseX, this.prevMouseY);
				}
				this.updateCounter++;
				if(this.isOpen) {
					/*if(this.currentCategory.getCategories().size() > 2) {
						for(int i = 0; i < 2; i++) {
							if(this.displayedCategories < this.currentCategory.getCategories().size()) {
								this.displayedCategories++;
							}
						}
					} else {
						this.displayedCategories = this.currentCategory.getCategories().size();
					}*/
					this.displayedCategories = this.currentCategory.getCategories().size();
				}
			} else {
				if(this.repositionMouse) {
					Mouse.setCursorPosition(this.prevMouseX, this.prevMouseY);
					this.repositionMouse = false;
				}
				this.prevMouseX = Mouse.getX();
				this.prevMouseY = Mouse.getY();

				if(this.scheduleMenuUpdate) {
					this.scheduleMenuUpdate = false;
					this.updateMenu();
				}

				EntityPlayer player = TheBetweenlands.proxy.getClientPlayer();
				if(player != null) {
					List<ItemStack> currentEquippables = new ArrayList<ItemStack>();
					for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
						ItemStack stack = player.inventory.getStackInSlot(i);
						if(stack != null && stack.getItem() instanceof IEquippable) {
							currentEquippables.add(stack);
						}
					}
					this.equippables.removeAll(currentEquippables);
					if(!this.equippables.isEmpty()) {
						this.updateMenu();
					}
					this.equippables.clear();
					this.equippables.addAll(currentEquippables);
				}
			}
		}
	}

	@SubscribeEvent
	public void renderGui(RenderGameOverlayEvent.Post event) {
		if(event.type == ElementType.HOTBAR) {
			if(Minecraft.getMinecraft().currentScreen != null && this.isOpen) {
				this.closeGUI();
				return;
			}

			if(!this.isOpen || this.currentCategory == null)
				return;

			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

			this.guiX = res.getScaledWidth() / 2;
			this.guiY = res.getScaledHeight() / 2;
			float circleAngle = 360.0F / this.currentCategory.getCategories().size();

			double mouseX = (this.prevMouseX * res.getScaledWidth()) / Minecraft.getMinecraft().displayWidth;
			double mouseY = res.getScaledHeight() - (this.prevMouseY * res.getScaledHeight()) / Minecraft.getMinecraft().displayHeight;

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			double diffX = this.guiX - mouseX;
			double diffY = this.guiY - mouseY;
			double centerDistance = Math.sqrt(diffX * diffX + diffY * diffY);
			double angle = 360 - (Math.toDegrees(Math.atan2(diffX, diffY)) + 180);
			GL11.glEnable(GL11.GL_TEXTURE_2D);

			//Render circle sections
			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("thebetweenlands:textures/gui/radialMenu.png"));
			int segments = Math.min(this.currentCategory.getCategories().size(), this.displayedCategories);
			for(int i = 0; i < segments; i++) {
				Category category = this.currentCategory.getCategories().get(i);
				float midAngle = i * circleAngle + circleAngle / 2.0F;
				double xOffset = Math.sin(Math.toRadians(midAngle));
				double yOffset = Math.cos(Math.toRadians(180-midAngle));

				int color;
				if(this.isInside((int)mouseX, (int)mouseY, i)) {
					color = category.getHighlightColor();
				} else {
					color = category.getColor();
				}

				float alpha = (float)(color >> 24 & 255) / 255.0F;
				float red = (float)(color >> 16 & 255) / 255.0F;
				float green = (float)(color >> 8 & 255) / 255.0F;
				float blue = (float)(color & 255) / 255.0F;

				GL11.glPushMatrix();
				GL11.glTranslated(this.guiX, this.guiY, 0);
				GL11.glTranslated(xOffset*segments*2.8D, yOffset*segments*2.8D, 0);

				GL11.glRotated(180.0D + 360.0D / Math.min(this.currentCategory.getCategories().size(), this.displayedCategories), 0, 0, 1);

				GL11.glRotated(((i) * circleAngle), 0, 0, 1);

				GL11.glColor4f(1, 1, 1, 1);

				double radius = /*60*/this.radius;
				double circumference = Math.PI * radius * 2.0D;
				double innerRadius = /*30*/this.innerRadius;
				double wrapRadius = ((radius - innerRadius - 8)) * 1;
				if(this.isInside((int)mouseX, (int)mouseY, i)) {
					radius = radius + 5;
				}
				double x = 0;
				double y = 0;
				double maxAngle = 360.0D / Math.min(this.currentCategory.getCategories().size(), this.displayedCategories);
				int subSegments = (int)(maxAngle / 10.0D);

				double wrapAngle = 90.0D * circumference / (Math.PI * radius * 2.0D);

				double textureWidth = 160.0D;
				double textureHeight = 64.0D;

				//									         segments,    maxAngle, wrapAngle, wrapRadius, radius, innerRadius, borderWidth
				RenderUtils.renderMappedCircleSegmentWrapped(subSegments, maxAngle, wrapAngle, wrapRadius, radius, MathHelper.clamp_double(innerRadius-segments*2.5D, 10, innerRadius), 8, 
						//Central piece
						0 / textureWidth, 100 / textureWidth, 10 / textureHeight, 47 / textureHeight,
						//Border 1
						111 / textureWidth, 120 / textureWidth, 10 / textureHeight, 47 / textureHeight,
						//Border 2
						0 / textureWidth, 100 / textureWidth, 0 / textureHeight, 9 / textureHeight,
						//Border 3
						121 / textureWidth, 130 / textureWidth, 10 / textureHeight, 47 / textureHeight,
						//Border 4
						0 / textureWidth, 100 / textureWidth, 48 / textureHeight, 57 / textureHeight,
						//Corner 1
						102 / textureWidth, 110 / textureWidth, 1 / textureHeight, 9 / textureHeight,
						//Corner 2
						102 / textureWidth, 110 / textureWidth, 11 / textureHeight, 19 / textureHeight,
						//Corner 3
						102 / textureWidth, 110 / textureWidth, 21 / textureHeight, 29 / textureHeight,
						//Corner 4
						102 / textureWidth, 110 / textureWidth, 31 / textureHeight, 39 / textureHeight);

				GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);

				GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);

				GL11.glEnable(GL11.GL_TEXTURE_2D);


				GL11.glPopMatrix();
			}

			//Stencil.getInstance().stopLayer();

			for(int i = 0; i < Math.min(this.currentCategory.getCategories().size(), this.displayedCategories); i++) {
				Category category = this.currentCategory.getCategories().get(i);
				float midAngle = i * circleAngle + circleAngle / 2.0F;
				double yOffset = Math.cos(Math.toRadians(180-midAngle))*segments*2.8D;
				double xOffset = Math.sin(Math.toRadians(midAngle))*segments*2.8D;

				//Render category stuff
				double dst = Math.sqrt(xOffset*xOffset+yOffset*yOffset);
				category.renderCategory(this.guiX+xOffset, this.guiY+yOffset, xOffset/dst, yOffset/dst, this.radius - this.innerRadius - 8, xOffset/dst*this.innerRadius, yOffset/dst*this.innerRadius, (i + 0.5D) * circleAngle, circleAngle);
			}

			//Render return button
			GL11.glEnable(GL11.GL_BLEND);
			if(centerDistance > this.innerRadius - 2) {
				GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.1f);
			} else {
				GL11.glColor4f(0.1f, 0.1f, 0.1f, 0.3f);
			}
			GuiUtil.drawCircle(this.guiX, this.guiY, this.innerRadius - 2);

			//Render lines and text
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			for(int i = 0; i < Math.min(this.currentCategory.getCategories().size(), this.displayedCategories); i++) {
				Category category = this.currentCategory.getCategories().get(i);
				float midAngle = i * circleAngle + circleAngle / 2.0F;
				int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(category.getName());
				int stringX = this.guiX + (int)(Math.sin(Math.toRadians(midAngle)) * ((this.radius - this.innerRadius) / 2 + this.innerRadius)) - width / 2;
				int stringY = this.guiY + (int)(Math.cos(Math.toRadians(180-midAngle)) * ((this.radius - this.innerRadius) / 2 + this.innerRadius)) - 4;

				GL11.glEnable(GL11.GL_TEXTURE_2D);

				double startX = this.guiX + Math.sin(Math.toRadians(midAngle)) * (this.radius + 5);
				double startY = this.guiY + Math.cos(Math.toRadians(180-midAngle)) * (this.radius + 5);
				double endX = this.guiX + Math.sin(Math.toRadians(midAngle)) * (this.radius + 40);
				double endY = this.guiY + Math.cos(Math.toRadians(180-midAngle)) * (this.radius + 40);

				//Render lines
				int color;
				if(this.isInside((int)mouseX, (int)mouseY, i)) {
					color = category.getHighlightColor();
				} else {
					color = category.getColor();
				}
				float alpha = (float)(color >> 24 & 255) / 255.0F;
				float red = (float)(color >> 16 & 255) / 255.0F;
				float green = (float)(color >> 8 & 255) / 255.0F;
				float blue = (float)(color & 255) / 255.0F;
				GL11.glColor4f(red, green, blue, alpha);
				GL11.glEnable(GL11.GL_LINE_SMOOTH);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glLineWidth(3.0f);

				GL11.glPushMatrix();
				double yOffset = Math.cos(Math.toRadians(180-midAngle))*segments*2.8D;
				double xOffset = Math.sin(Math.toRadians(midAngle))*segments*2.8D;
				GL11.glTranslated(xOffset, yOffset, 0);

				GL11.glBegin(GL11.GL_LINES);
				GL11.glVertex2d(startX, startY);
				if(Math.abs(endX - this.guiX) > 1) {
					GL11.glVertex2d(endX, (int)endY);
				} else {
					GL11.glVertex2d(endX, (int)endY - 1);
				}
				if(Math.abs(endX - this.guiX) > 1) {
					GL11.glVertex2d(endX, (int)endY);
					if(endX < this.guiX) {
						GL11.glVertex2d(this.guiX-100, (int)endY);
					} else {
						GL11.glVertex2d(this.guiX+99, (int)endY);
					}
				}
				GL11.glEnd();

				//Render text
				GL11.glEnable(GL11.GL_TEXTURE_2D);
				int textColor = 0xFFFFFFFF;
				if(endX < this.guiX - 1) {
					Gui.drawRect((int)this.guiX - 100 - width - 1, (int)endY - 8, (int)this.guiX - 100, (int)endY + 1, color);
					Minecraft.getMinecraft().fontRenderer.drawString(category.getName(), (int)this.guiX-100-width, (int)endY - 7, textColor);
				} else if(endX > this.guiX + 1) {
					Gui.drawRect((int)this.guiX + 100 - 1, (int)endY - 8, (int)this.guiX + 100 + width, (int)endY + 1, color);
					Minecraft.getMinecraft().fontRenderer.drawString(category.getName(), (int)this.guiX+100, (int)endY - 7, textColor);
				} else {
					Gui.drawRect((int)this.guiX - width / 2 - 1, (int)endY - 1, (int)this.guiX - width / 2 + width, (int)endY + 8, color);
					Minecraft.getMinecraft().fontRenderer.drawString(category.getName(), (int)this.guiX - width / 2, (int)endY, textColor);
				}

				GL11.glPopMatrix();
			}

			GL11.glEnable(GL11.GL_TEXTURE_2D);

			//Render category tree
			if(this.currentCategory != this.rootCategory) {
				String categoryName = "";
				for(int i = 1; i < this.lastCategories.size(); i++) {
					Category category = this.lastCategories.get(i);
					categoryName = categoryName + category.getName() + " > ";
				}
				categoryName = categoryName + this.currentCategory.getName();
				int width = Minecraft.getMinecraft().fontRenderer.getStringWidth(categoryName);
				Minecraft.getMinecraft().fontRenderer.drawString(categoryName, (int)this.guiX - width / 2, (int)this.guiY - this.radius - 60, 0xFFFFFFFF);
			}

			GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
		}
	}

	private void onMouseClick(int mouseX, int mouseY, int mouseButton) {
		double diffX = this.guiX - mouseX;
		double diffY = this.guiY - mouseY;
		double length = Math.sqrt(diffX * diffX + diffY * diffY);
		if(length <= this.innerRadius - 2) {
			if(this.lastCategories.size() > 0) {
				this.currentCategory = this.lastCategories.get(this.lastCategories.size() - 1);
				this.lastCategories.remove(this.lastCategories.size() - 1);
				//this.displayedCategories = 0;
				this.displayedCategories = this.currentCategory.getCategories().size();
				Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
			}
		} else {
			float circleAngle = 360.0F / this.currentCategory.getCategories().size();
			for(int i = 0; i < Math.min(this.currentCategory.getCategories().size(), this.displayedCategories); i++) {
				if(this.isInside(mouseX, mouseY, i)) {
					Category category = this.currentCategory.getCategories().get(i);
					if(category.getCategories().size() > 0) {
						this.lastCategories.add(currentCategory);
						this.currentCategory = category;
						//this.displayedCategories = 0;
						this.displayedCategories = this.currentCategory.getCategories().size();
					} else {
						if(category.onClicked(mouseX, mouseY, mouseButton)) {
							this.closeGUI();
						}
					}
					Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.func_147674_a(new ResourceLocation("gui.button.press"), 1.0F));
					break;
				}
			}
		}
	}

	private boolean isInside(int x, int y, int category) {
		double diffX = this.guiX - x;
		double diffY = this.guiY - y;
		float circleAngle = 360.0F / this.currentCategory.getCategories().size();
		double length = Math.sqrt(diffX * diffX + diffY * diffY);
		double angle = (360 - (Math.toDegrees(Math.atan2(diffX, diffY)) + 180) + 180) % 360;
		int segments = Math.min(this.currentCategory.getCategories().size(), this.displayedCategories);
		if(angle >= (int)(category * circleAngle) && angle < (int)((category + 1) * circleAngle) && length <= this.radius + segments*2D && length >= MathHelper.clamp_double(this.innerRadius-segments*2.8D, 10, this.innerRadius) + segments*2D - 4) {
			return true;
		}
		return false;
	}
}

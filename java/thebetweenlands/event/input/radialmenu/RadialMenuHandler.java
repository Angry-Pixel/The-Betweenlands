package thebetweenlands.event.input.radialmenu;

import java.util.ArrayList;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
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
import thebetweenlands.utils.MCStencil;
import thebetweenlands.utils.Stencil;

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
	public void onMouseInput(InputEvent.MouseInputEvent event) {
		if(this.isOpen) {
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
		this.rootCategory.categories.clear();
		this.currentCategory = this.rootCategory;

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
			int page = 1;
			int categoryLimit = 10;
			Category currentCategory = this.rootCategory;
			for(Category category : categories) {
				if(currentCategory.getCategories().size() > categoryLimit) {
					page++;
					Category newPage = new Category(String.format(StatCollector.translateToLocal("equipment.menu.page"), page), 0x80101010, 0xEE202020);
					currentCategory.addCategory(newPage);
					currentCategory = newPage;
				}
				currentCategory.addCategory(category);
			}
		}

		this.lastCategories.clear();
		//this.displayedCategories = 0;
		this.displayedCategories = this.rootCategory.getCategories().size();
	}

	@SubscribeEvent
	public void onEquipmentChange(EquipmentChangeEvent event) {
		if(this.isOpen && event.entity == TheBetweenlands.proxy.getClientPlayer()) {
			this.scheduleMenuUpdate();
		}
	}

	////// GUI ///////

	public static class Category {
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

			//TODO: Implement menu refreshing

			//TODO: Will look into this, it seems Forge is also doing some stencil buffer stuff, but it doesn't seem like there is any buffer properly set up
			MCStencil.checkSetupFBO();

			GL11.glAlphaFunc(GL11.GL_GREATER, 0.0F);

			ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft(), Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);

			this.guiX = res.getScaledWidth() / 2;
			this.guiY = res.getScaledHeight() / 2;
			float circleAngle = 360.0F / this.currentCategory.getCategories().size();

			double mouseX = (this.prevMouseX * res.getScaledWidth()) / Minecraft.getMinecraft().displayWidth;
			double mouseY = res.getScaledHeight() - (this.prevMouseY * res.getScaledHeight()) / Minecraft.getMinecraft().displayHeight;

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_TEXTURE_2D);

			Stencil.getInstance().startLayer();
			Stencil.getInstance().setBuffer();
			Stencil.getInstance().createCirlce(this.guiX, this.guiY, this.radius + 10);
			Stencil.getInstance().setBuffer(false);
			Stencil.getInstance().createCirlce(this.guiX, this.guiY, this.innerRadius);
			Stencil.getInstance().cropInside();

			double diffX = this.guiX - mouseX;
			double diffY = this.guiY - mouseY;
			double centerDistance = Math.sqrt(diffX * diffX + diffY * diffY);
			double angle = 360 - (Math.toDegrees(Math.atan2(diffX, diffY)) + 180);

			//Render circle sections
			for(int i = 0; i < Math.min(this.currentCategory.getCategories().size(), this.displayedCategories); i++) {
				Category category = this.currentCategory.getCategories().get(i);
				float midAngle = i * circleAngle + circleAngle / 2.0F;
				double xOffset = Math.sin(Math.toRadians(midAngle)) * 3.0;
				double yOffset = Math.cos(Math.toRadians(180-midAngle)) * 3.0;

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
				GL11.glTranslated(xOffset, yOffset, 0);
				GL11.glColor4f(red, green, blue, alpha);

				GuiUtil.drawPartialCircle(this.guiX, this.guiY - 1, this.radius - 4, (int)(i * circleAngle), (int)((i + 1) * circleAngle));

				GL11.glPopMatrix();
			}

			Stencil.getInstance().stopLayer();

			for(int i = 0; i < Math.min(this.currentCategory.getCategories().size(), this.displayedCategories); i++) {
				Category category = this.currentCategory.getCategories().get(i);
				float midAngle = i * circleAngle + circleAngle / 2.0F;
				double yOffset = Math.cos(Math.toRadians(180-midAngle)) * 3.0;
				double xOffset = Math.sin(Math.toRadians(midAngle)) * 3.0;

				//Render category stuff
				double dst = Math.sqrt(xOffset*xOffset+yOffset*yOffset);
				category.renderCategory(this.guiX, this.guiY, xOffset/dst, yOffset/dst, this.radius - this.innerRadius, xOffset/dst*this.innerRadius, yOffset/dst*this.innerRadius, (i + 0.5D) * circleAngle, circleAngle);
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

				double startX = this.guiX + Math.sin(Math.toRadians(midAngle)) * (this.radius + 2);
				double startY = this.guiY + Math.cos(Math.toRadians(180-midAngle)) * (this.radius + 2);
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
		if(angle >= (int)(category * circleAngle) && angle < (int)((category + 1) * circleAngle) && length <= this.radius && length >= this.innerRadius) {
			return true;
		}
		return false;
	}
}

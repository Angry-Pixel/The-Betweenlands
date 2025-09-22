package thebetweenlands.client.handler.equipment;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import thebetweenlands.api.event.EquipmentChangedEvent;
import thebetweenlands.api.item.RadialMenuEquippable;
import thebetweenlands.client.BetweenlandsClient;
import thebetweenlands.client.BetweenlandsKeybinds;
import thebetweenlands.client.renderer.BLRenderTypes;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.component.entity.equipment.EquipmentData;
import thebetweenlands.common.component.entity.equipment.EquipmentHelper;
import thebetweenlands.common.component.entity.equipment.EquipmentInventoryType;
import thebetweenlands.common.registries.AttachmentRegistry;
import thebetweenlands.util.GuiUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadialMenuHandler {

	public static final RadialMenuHandler INSTANCE = new RadialMenuHandler();
	public static final ResourceLocation TEXTURE = TheBetweenlands.prefix("textures/gui/radial_menu.png");

	private boolean isOpen = false;

	private boolean repositionMouse = false;
	private double prevMouseX;
	private double prevMouseY;
	private boolean scheduleMenuUpdate = false;

	public void init() {
		NeoForge.EVENT_BUS.addListener(this::onKeyInput);
		NeoForge.EVENT_BUS.addListener(this::onMouseInput);
		NeoForge.EVENT_BUS.addListener(this::onPreTick);
		NeoForge.EVENT_BUS.addListener(this::onPostTick);
		NeoForge.EVENT_BUS.addListener(this::onEquipmentChange);
	}

	/// /// Input ///////
	public void onKeyInput(InputEvent.Key event) {
		if (BetweenlandsKeybinds.RADIAL_MENU.consumeClick()) {
			KeyMapping.set(BetweenlandsKeybinds.RADIAL_MENU.getKey(), false);
			if (!this.isOpen) {
				this.openGUI();
			} else {
				this.closeGUI();
			}
		}
	}

	public void onMouseInput(InputEvent.MouseButton.Pre event) {
		if (this.isOpen && event.getAction() == InputConstants.PRESS) {
			event.setCanceled(true);
			if (Minecraft.getInstance().isWindowActive()) {
				Minecraft.getInstance().mouseHandler.releaseMouse();
				this.repositionMouse = true;
			}
			Window window = Minecraft.getInstance().getWindow();

			double mouseX = (this.prevMouseX * window.getGuiScaledWidth()) / window.getScreenWidth();
			double mouseY = (this.prevMouseY * window.getGuiScaledHeight()) / window.getScreenHeight();
			this.onMouseClick((int) mouseX, (int) mouseY, event.getButton());
		}
	}

	public void openGUI() {
		this.isOpen = true;
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		Minecraft.getInstance().mouseHandler.releaseMouse();
		this.prevMouseX = Minecraft.getInstance().mouseHandler.xpos();
		this.prevMouseY = Minecraft.getInstance().mouseHandler.ypos();

		this.updateMenu();
	}

	public void closeGUI() {
		this.isOpen = false;
		Minecraft.getInstance().mouseHandler.grabMouse();
		Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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

		Player player = BetweenlandsClient.getClientPlayer();
		if (player != null) {
			Inventory inventory = player.getInventory();

			List<Category> categories = new ArrayList<>();

			EquipmentData data = player.getData(AttachmentRegistry.EQUIPMENT);
			//Equippable items
			for (EquipmentInventoryType type : EquipmentInventoryType.values()) {
				for (int i = 0; i < inventory.getContainerSize(); i++) {
					ItemStack stack = inventory.getItem(i);

					if (!stack.isEmpty() && stack.getItem() instanceof RadialMenuEquippable equippable) {

						if (equippable.getEquipmentCategory(stack) == type) {
							if (equippable.canEquip(stack, player, player)) {
								ItemStack res = EquipmentHelper.equipItem(player, player, stack, true);

								if (res.isEmpty() || res.getCount() != stack.getCount()) {
									categories.add(new Categories.EquipCategory(Component.translatable("equipment.thebetweenlands.menu.equip", stack.getHoverName()), 0x6010AA10, 0xDD10AA10, stack, type, i));
								}
							}
						}
					}
				}
			}

			//Unequippable items
			for (EquipmentInventoryType type : EquipmentInventoryType.values()) {
				Container inv = data.getContainer(player, type);

				for (int i = 0; i < inv.getContainerSize(); i++) {
					ItemStack stack = inv.getItem(i);

					if (!stack.isEmpty()) {
						if (stack.getItem() instanceof RadialMenuEquippable equippable &&
							!equippable.canUnequip(stack, player, player, inv)) {
							continue;
						}

						categories.add(new Categories.UnequipCategory(Component.translatable("equipment.thebetweenlands.menu.unequip", stack.getHoverName()), 0x60AA1010, 0xDDAA1010, stack, type, i));
					}
				}
			}

			int index = 0;
			int page = 1;
			int categoryLimit = 10;
			Category currentCategory = this.rootCategory;
			List<Category> pages = new ArrayList<>();
			for (Category category : categories) {
				if (currentCategory.getCategories().size() > categoryLimit) {
					page++;
					Category newPage = new Category(Component.translatable("equipment.thebetweenlands.menu.page", page), 0x80101010, 0xEE202020);
					currentCategory.addCategory(newPage);
					currentCategory = newPage;
					pages.add(newPage);
				}
				currentCategory.addCategory(category);
			}
			List<Category> allCategories = new ArrayList<>();
			allCategories.addAll(categories);
			allCategories.addAll(pages);
			for (Category category : allCategories) {
				category.index = ++index;
			}
			for (Category category : allCategories) {
				if (category.index == prevIndex && !category.getCategories().isEmpty()) {
					Category parent = category;
					while ((parent = parent.parent) != null) {
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

	public void onEquipmentChange(EquipmentChangedEvent event) {
		if (this.isOpen && event.getEntity() == BetweenlandsClient.getClientPlayer()) {
			this.scheduleMenuUpdate();
		}
	}

	/// /// GUI ///////

	public static class Category {
		private Category parent;
		private int index;
		private final List<Category> categories = new ArrayList<>();
		private final Component name;
		private final int color;
		private final int highlightColor;

		public Category(Component name, int color, int highlightColor) {
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

		public Component getName() {
			return this.name;
		}

		public List<Category> getCategories() {
			return this.categories;
		}

		public Category addCategory(Category category) {
			category.parent = this;
			this.categories.add(category);
			return this;
		}

		public void renderCategory(GuiGraphics graphics, double centerX, double centerY, double dirX, double dirY, double radius, double startX, double startY, double angle, double segmentAngle) {
		}

		public boolean onClicked(int mouseX, int mouseY, int mouseButton) {
			return false;
		}
	}

	private final Category rootCategory = new Category(Component.literal("Root"), 0x30101010, 0x30101010);
	private Category currentCategory;
	private final List<Category> lastCategories = new ArrayList<>();
	private final int radius = 50;
	private final int innerRadius = 15;
	private int guiX;
	private int guiY;
	private int displayedCategories = 0;

	private final List<ItemStack> equippables = new ArrayList<>();

	public void onPreTick(ClientTickEvent.Pre event) {
		if (this.isOpen) {
			if (this.repositionMouse) {
				GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(), this.prevMouseX, this.prevMouseY);
			}
			this.displayedCategories = this.currentCategory.getCategories().size();
		}
	}

	public void onPostTick(ClientTickEvent.Post event) {
		if (this.isOpen) {
			if (this.repositionMouse) {
				GLFW.glfwSetCursorPos(Minecraft.getInstance().getWindow().getWindow(), this.prevMouseX, this.prevMouseY);
				this.repositionMouse = false;
			}
			this.prevMouseX = Minecraft.getInstance().mouseHandler.xpos();
			this.prevMouseY = Minecraft.getInstance().mouseHandler.ypos();

			if (this.scheduleMenuUpdate) {
				this.scheduleMenuUpdate = false;
				this.updateMenu();
			}

			Player player = BetweenlandsClient.getClientPlayer();
			if (player != null) {
				List<ItemStack> currentEquippables = new ArrayList<>();
				for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
					ItemStack stack = player.getInventory().getItem(i);
					if (!stack.isEmpty() && stack.getItem() instanceof RadialMenuEquippable) {
						currentEquippables.add(stack);
					}
				}
				this.equippables.removeAll(currentEquippables);
				if (!this.equippables.isEmpty()) {
					this.updateMenu();
				}
				this.equippables.clear();
				this.equippables.addAll(currentEquippables);
			}
		}
	}

	public void renderRadialMenu(GuiGraphics graphics, DeltaTracker tracker) {
		Minecraft mc = Minecraft.getInstance();

		if (mc.screen != null && this.isOpen) {
			this.closeGUI();
			return;
		}

		if (!this.isOpen || this.currentCategory == null)
			return;

		Window window = Minecraft.getInstance().getWindow();

		this.guiX = graphics.guiWidth() / 2;
		this.guiY = graphics.guiHeight() / 2;
		float circleAngle = 360.0F / this.currentCategory.getCategories().size();

		double mouseX = (this.prevMouseX * window.getGuiScaledWidth()) / window.getScreenWidth();
		double mouseY = (this.prevMouseY * window.getGuiScaledHeight()) / window.getScreenHeight();

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

		double diffX = this.guiX - mouseX;
		double diffY = this.guiY - mouseY;
		double centerDistance = Math.sqrt(diffX * diffX + diffY * diffY);

		//Render circle sections
		int segments = Math.min(this.currentCategory.getCategories().size(), this.displayedCategories);
		for (int i = 0; i < segments; i++) {
			float midAngle = i * circleAngle + circleAngle / 2.0F;
			double xOffset = Math.sin(Math.toRadians(midAngle));
			double yOffset = Math.cos(Math.toRadians(180 - midAngle));

			graphics.pose().pushPose();
			graphics.pose().translate(this.guiX + xOffset * segments * 2.8D, this.guiY + yOffset * segments * 2.8D, 0);

			graphics.pose().mulPose(Axis.ZP.rotationDegrees(180.0F + 360.0F / Math.min(this.currentCategory.getCategories().size(), this.displayedCategories)));

			graphics.pose().mulPose(Axis.ZP.rotationDegrees(i * circleAngle));

			float radius = /*60*/this.radius;
			float circumference = Mth.PI * radius * 2.0F;
			float innerRadius = /*30*/this.innerRadius;
			float wrapRadius = ((radius - innerRadius - 8)) * 1;
			if (this.isInside((int) mouseX, (int) mouseY, i)) {
				radius = radius + 5;
			}
			float maxAngle = 360.0F / Math.min(this.currentCategory.getCategories().size(), this.displayedCategories);
			int subSegments = (int) (maxAngle / 10.0F);

			float wrapAngle = 90.0F * circumference / (Mth.PI * radius * 2.0F);

			float textureWidth = 160.0F;
			float textureHeight = 64.0F;

			GuiUtils.renderMappedCircleSegmentWrapped(graphics.pose(), TEXTURE, subSegments, maxAngle, wrapAngle, wrapRadius, radius, Mth.clamp(innerRadius - segments * 2.5F, 10, innerRadius), 8,
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
			graphics.pose().popPose();
		}

		for (int i = 0; i < Math.min(this.currentCategory.getCategories().size(), this.displayedCategories); i++) {
			Category category = this.currentCategory.getCategories().get(i);
			float midAngle = i * circleAngle + circleAngle / 2.0F;
			double yOffset = Math.cos(Math.toRadians(180 - midAngle)) * segments * 2.8D;
			double xOffset = Math.sin(Math.toRadians(midAngle)) * segments * 2.8D;

			//Render category stuff
			double dst = Math.sqrt(xOffset * xOffset + yOffset * yOffset);
			category.renderCategory(graphics, this.guiX + xOffset, this.guiY + yOffset, xOffset / dst, yOffset / dst, this.radius - this.innerRadius - 8, xOffset / dst * this.innerRadius, yOffset / dst * this.innerRadius, (i + 0.5D) * circleAngle, circleAngle);
		}

		//Render return button
		RenderSystem.enableBlend();
		if (centerDistance > this.innerRadius - 2) {
			graphics.setColor(0.0f, 0.0f, 0.0f, 0.1f);
		} else {
			graphics.setColor(0.1f, 0.1f, 0.1f, 0.3f);
		}
		GuiUtils.drawCircle(graphics.pose().last(), this.guiX, this.guiY, this.innerRadius - 2);
		graphics.setColor(1, 1, 1, 1);

		//Render lines and text
		for (int i = 0; i < Math.min(this.currentCategory.getCategories().size(), this.displayedCategories); i++) {
			Category category = this.currentCategory.getCategories().get(i);
			float midAngle = i * circleAngle + circleAngle / 2.0F;
			int width = mc.font.width(category.getName());

			float startX = this.guiX + Mth.sin((float) Math.toRadians(midAngle)) * (this.radius + 5);
			float startY = this.guiY + Mth.cos((float) Math.toRadians(180 - midAngle)) * (this.radius + 5);
			float endX = this.guiX + Mth.sin((float) Math.toRadians(midAngle)) * (this.radius + 40);
			float endY = this.guiY + Mth.cos((float) Math.toRadians(180 - midAngle)) * (this.radius + 40);

			//Render lines
			int color;
			if (this.isInside((int) mouseX, (int) mouseY, i)) {
				color = category.getHighlightColor();
			} else {
				color = category.getColor();
			}
			float alpha = (float) (color >> 24 & 255) / 255.0F;
			float red = (float) (color >> 16 & 255) / 255.0F;
			float green = (float) (color >> 8 & 255) / 255.0F;
			float blue = (float) (color & 255) / 255.0F;

			RenderSystem.enableBlend();

			graphics.pose().pushPose();
			double yOffset = Math.cos(Math.toRadians(180 - midAngle)) * segments * 2.8D;
			double xOffset = Math.sin(Math.toRadians(midAngle)) * segments * 2.8D;
			graphics.pose().translate(xOffset, yOffset, 0);

			VertexConsumer consumer = graphics.bufferSource().getBuffer(BLRenderTypes.equipmentLines(3.0F));

			consumer.addVertex(graphics.pose().last(), startX, startY, 0.0F).setColor(red, green, blue, alpha);
			if (Math.abs(endX - this.guiX) > 1) {
				consumer.addVertex(graphics.pose().last(), endX, endY, 0.0F).setColor(red, green, blue, alpha);
			} else {
				consumer.addVertex(graphics.pose().last(), endX, endY - 1, 0.0F).setColor(red, green, blue, alpha);
			}
			if (Math.abs(endX - this.guiX) > 1) {
				consumer.addVertex(graphics.pose().last(), endX, endY, 0.0F).setColor(red, green, blue, alpha);
				if (endX < this.guiX) {
					consumer.addVertex(graphics.pose().last(), this.guiX - 100, endY, 0.0F).setColor(red, green, blue, alpha);
				} else {
					consumer.addVertex(graphics.pose().last(), this.guiX + 99, endY, 0.0F).setColor(red, green, blue, alpha);
				}
			}

			//Render text
			int textColor = 0xFFFFFFFF;
			if (endX < this.guiX - 1) {
				graphics.fill(this.guiX - 100 - width - 1, (int) endY - 8, this.guiX - 100, (int) endY + 1, color);
				graphics.drawString(mc.font, category.getName(), this.guiX - 100 - width, (int) endY - 7, textColor, false);
			} else if (endX > this.guiX + 1) {
				graphics.fill(this.guiX + 100 - 1, (int) endY - 8, this.guiX + 100 + width, (int) endY + 1, color);
				graphics.drawString(mc.font, category.getName(), this.guiX + 100, (int) endY - 7, textColor, false);
			} else {
				graphics.fill(this.guiX - width / 2 - 1, (int) endY - 1, this.guiX - width / 2 + width, (int) endY + 8, color);
				graphics.drawString(mc.font, category.getName(), this.guiX - width / 2, (int) endY, textColor, false);
			}

			graphics.pose().popPose();
		}

		//Render category tree
		if (this.currentCategory != this.rootCategory) {
			StringBuilder categoryName = new StringBuilder();
			for (int i = 1; i < this.lastCategories.size(); i++) {
				Category category = this.lastCategories.get(i);
				categoryName.append(category.getName().getString()).append(" > ");
			}
			categoryName.append(this.currentCategory.getName().getString());
			int width = mc.font.width(categoryName.toString());
			graphics.drawString(mc.font, categoryName.toString(), this.guiX - width / 2, this.guiY - this.radius - 60, 0xFFFFFFFF, false);
		}
	}

	private void onMouseClick(int mouseX, int mouseY, int mouseButton) {
		double diffX = this.guiX - mouseX;
		double diffY = this.guiY - mouseY;
		double length = Math.sqrt(diffX * diffX + diffY * diffY);
		if (length <= this.innerRadius - 2) {
			if (!this.lastCategories.isEmpty()) {
				this.currentCategory = this.lastCategories.getLast();
				this.lastCategories.removeLast();
				this.displayedCategories = this.currentCategory.getCategories().size();
				Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
			}
		} else {
			for (int i = 0; i < Math.min(this.currentCategory.getCategories().size(), this.displayedCategories); i++) {
				if (this.isInside(mouseX, mouseY, i)) {
					Category category = this.currentCategory.getCategories().get(i);
					if (!category.getCategories().isEmpty()) {
						this.lastCategories.add(this.currentCategory);
						this.currentCategory = category;
						this.displayedCategories = this.currentCategory.getCategories().size();
					} else {
						if (category.onClicked(mouseX, mouseY, mouseButton)) {
							this.closeGUI();
						}
					}
					Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
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
		return angle >= (category * circleAngle) && angle < ((category + 1) * circleAngle) && length <= this.radius + segments * 2D && length >= Mth.clamp(this.innerRadius - segments * 2.8D, 10, this.innerRadius) + segments * 2D - 4;
	}
}

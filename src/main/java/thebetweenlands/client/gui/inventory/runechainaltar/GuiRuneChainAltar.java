package thebetweenlands.client.gui.inventory.runechainaltar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.rune.IGuiRuneMark;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.INodeConfiguration.IConfigurationInput;
import thebetweenlands.api.rune.INodeConfiguration.IConfigurationOutput;
import thebetweenlands.api.rune.INodeConfiguration.IType;
import thebetweenlands.api.rune.IRuneChainAltarGui;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneGui;
import thebetweenlands.api.rune.IRuneLink;
import thebetweenlands.api.rune.RuneMenuDrawingContext;
import thebetweenlands.api.rune.RuneMenuType;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltar;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltar.Page;
import thebetweenlands.common.inventory.container.runechainaltar.ContainerRuneChainAltarGui;
import thebetweenlands.common.inventory.slot.SlotRuneChainAltarInput;
import thebetweenlands.common.network.serverbound.MessageLinkRuneChainAltarRune;
import thebetweenlands.common.network.serverbound.MessageSetRuneChainAltarPage;
import thebetweenlands.common.network.serverbound.MessageShiftRuneChainAltarSlot;
import thebetweenlands.common.network.serverbound.MessageUnlinkRuneChainAltarRune;
import thebetweenlands.common.registries.CapabilityRegistry;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;
import thebetweenlands.util.ColoredItemRenderer;

@SideOnly(Side.CLIENT)
public class GuiRuneChainAltar extends GuiContainer implements IRuneChainAltarGui {
	private static final Random rand = new Random();

	public static final ResourceLocation GUI_RUNE_CHAIN_ALTAR = new ResourceLocation("thebetweenlands:textures/gui/rune_chain_altar.png");
	public static final ResourceLocation GUI_RUNE_CHAIN_ALTAR_ROPE = new ResourceLocation("thebetweenlands:textures/gui/rune_chain_altar_rope.png");

	private final TileEntityRuneChainAltar tile;
	private final ContainerRuneChainAltar container;
	private final EntityPlayer player;

	private static final int SWAP_ANIMATION_HALF_DURATION = 7;
	private int lastSwapAnimationTicks = 0;
	private int swapAnimationTicks = 0;
	private int newPage = -1;

	private int updateCounter;

	private boolean drawHoveringSlots = false;

	private EnumMap<RuneMenuType, IRuneGui> openRuneGuis = new EnumMap<>(RuneMenuType.class);

	private IGuiRuneMark draggingMark = null;

	private int linkingDropdownMenuSlot = -1;
	private Tuple<IGuiRuneMark, IRuneLink> recentlyLinked = null;

	private static final int[][] CORD_PIECE_SLOT_UVs = new int[][] {
		{10, 251}, {36, 266}, {60, 261}, {84, 260}, {108, 260}, {132, 261}, {156, 266},
		{180, 274},
		{156, 312}, {132, 315}, {108, 318}, {84, 318}, {60, 315}, {36, 312}, {10, 309}
	};

	private static final int[][] CORD_PIECE_UVs = new int[][] {
		{248, 0}, {274, 15}, {298, 10}, {322, 9}, {346, 9}, {370, 10}, {394, 15},
		{418, 23},
		{394, 61}, {370, 64}, {346, 67}, {322, 67}, {298, 64}, {274, 61}, {248, 58}
	};

	private static final int[][] CORD_PIECE_SIZES = new int[][] {
		{10, 22}, {6, 6}, {6, 5}, {6, 4}, {6, 4}, {6, 5}, {6, 6},
		{9, 34},
		{6, 6}, {6, 5}, {6, 4}, {6, 4}, {6, 5}, {6, 6}, {10, 22}
	};

	public GuiRuneChainAltar(EntityPlayer player, TileEntityRuneChainAltar tile) {
		super(new ContainerRuneChainAltarGui(player, tile));
		((ContainerRuneChainAltarGui) this.inventorySlots).setGui(this);
		this.container = (ContainerRuneChainAltar) this.inventorySlots;
		this.tile = tile;
		this.allowUserInput = false;
		this.xSize = 176;
		this.ySize = 224;
		this.player = player;
	}

	public void onSetSelectedRune(int runeIndex) {
		this.linkingDropdownMenuSlot = -1;

		this.updateSelectedRuneGui(runeIndex);
	}

	protected void updateSelectedRuneGui(int runeIndex) {
		boolean clear = false;

		if(runeIndex < 0) {
			clear = true;
		} else {
			IRuneGui currentGui = this.openRuneGuis.get(RuneMenuType.PRIMARY);

			ItemStack stack = this.container.getRuneItemStack(runeIndex);

			if(!stack.isEmpty() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE, null)) {
				IRuneContainer container = this.container.getRuneContainer(runeIndex);

				if(currentGui == null || currentGui.getContainer() != container) {
					if(currentGui != null) {
						currentGui.close();
					}

					IRuneGui newGui = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE, null).getRuneContainerFactory().createGui(RuneMenuType.PRIMARY);

					newGui.init(container, this.width, this.height);

					this.openRuneGuis.put(RuneMenuType.PRIMARY, newGui);

					this.draggingMark = null;
				}
			} else {
				clear = true;
			}
		}

		if(clear) {
			for(IRuneGui gui : this.openRuneGuis.values()) {
				gui.close();
			}

			this.openRuneGuis.clear();

			this.draggingMark = null;
		}
	}

	protected void updateSecondaryRuneGui() {
		int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;

		IRuneGui currentGui = this.openRuneGuis.get(RuneMenuType.SECONDARY);

		boolean closeCurrentGui = false;

		int targetRune = -1;

		Tuple<IGuiRuneMark, IRuneLink> hoveredLink = this.getHoveredOnLink(mouseX, mouseY);

		if(hoveredLink != null) {
			targetRune = hoveredLink.getSecond().getOutputRune();
		} else if(this.linkingDropdownMenuSlot >= this.tile.getChainStart()) {
			targetRune = this.linkingDropdownMenuSlot - this.tile.getChainStart();
		} else if(this.draggingMark == null) {
			closeCurrentGui = true;
		}

		if(targetRune >= 0) {
			ItemStack stack = this.container.getRuneItemStack(targetRune);

			if(!stack.isEmpty() && stack.hasCapability(CapabilityRegistry.CAPABILITY_RUNE, null)) {
				IRuneContainer container = this.container.getRuneContainer(targetRune);

				if(container != null) {
					if(currentGui == null || currentGui.getContainer().getContext().getRuneIndex() != targetRune || currentGui.getContainer() != container) {
						if(currentGui != null) {
							currentGui.close();
						}

						IRuneGui newGui = stack.getCapability(CapabilityRegistry.CAPABILITY_RUNE, null).getRuneContainerFactory().createGui(RuneMenuType.SECONDARY);

						newGui.init(container, this.width, this.height);

						this.openRuneGuis.put(RuneMenuType.SECONDARY, newGui);
					}
				} else {
					closeCurrentGui = true;
				}
			} else {
				closeCurrentGui = true;
			}
		}

		if(closeCurrentGui && currentGui != null) {
			currentGui.close();
			this.openRuneGuis.remove(RuneMenuType.SECONDARY);
		}
	}

	protected float setSlabTransform() {
		GlStateManager.pushMatrix();

		if(this.swapAnimationTicks > 0) {
			float tick = this.lastSwapAnimationTicks + (this.swapAnimationTicks - this.lastSwapAnimationTicks) * this.mc.getRenderPartialTicks();

			float easeInOut = 0.0F;

			if(tick <= SWAP_ANIMATION_HALF_DURATION) {
				easeInOut = this.easeInOutCubic(tick, 0, 1, SWAP_ANIMATION_HALF_DURATION);
				GlStateManager.translate(0, easeInOut * -130, 0);
			} else {
				easeInOut = 1 - this.easeInOutCubic(tick - SWAP_ANIMATION_HALF_DURATION, 0, 1, SWAP_ANIMATION_HALF_DURATION);
				GlStateManager.translate(0, -easeInOut * 130, 0);
			}

			float alpha = 1.0F - easeInOut;

			GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
			GlStateManager.enableBlend();
			GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

			return alpha;
		}

		return 1.0F;
	}

	protected void revertSlabTransform() {
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.popMatrix();
	}

	protected boolean isSlabSlot(Slot slot) {
		return slot.slotNumber < this.tile.getMaxChainLength() + 1;
	}

	protected float easeInOutCubic(float time, float start, float change, float duration) {
		time /= duration/2;
		if (time < 1) return change/2*time*time*time + start;
		time -= 2;
		return change/2*(time*time*time + 2) + start;
	}

	@Override
	protected boolean hasClickedOutside(int mouseX, int mouseY, int guiLeft, int guiTop) {
		if(mouseX >= this.guiLeft - 15 && mouseX < this.guiLeft + this.xSize + 15 && mouseY >= this.guiTop && mouseY < this.guiTop + this.ySize) {
			return false;
		}

		for(IRuneGui gui : this.openRuneGuis.values()) {
			if(mouseX >= gui.getMinX() && mouseX < gui.getMaxX() && mouseY >= gui.getMinY() && mouseY < gui.getMaxY()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		//Make sure that the currently selected and secondary rune GUI is always correct
		if(this.container.getSelectedRuneIndex() >= 0) {
			this.updateSelectedRuneGui(this.container.getSelectedRuneIndex());
		}
		this.updateSecondaryRuneGui();

		this.drawDefaultBackground();

		super.drawScreen(mouseX, mouseY, partialTicks);

		if(this.swapAnimationTicks > 0 && this.hoveredSlot != null && this.isSlabSlot(this.hoveredSlot)) {
			this.hoveredSlot = null;
		}

		if(!this.isInsideLinkingDropdownMenu(mouseX, mouseY, this.draggingMark != null)) {
			this.renderHoveredToolTip(mouseX, mouseY);
		}
	}

	@Override
	public void drawSlot(Slot slot) {
		if(slot instanceof SlotRuneChainAltarInput) {
			SlotRuneChainAltarInput slotRune = (SlotRuneChainAltarInput) slot;

			if(slotRune.hoverTicks + slotRune.prevHoverTicks == 0 != this.drawHoveringSlots) {
				float hoverPercent = (slotRune.prevHoverTicks + (slotRune.hoverTicks - slotRune.prevHoverTicks) * this.mc.getRenderPartialTicks()) / 7.0F;

				float hover = this.easeInOutCubic(hoverPercent, 0, 1.0F, 1.0F);

				float hoverHeight = hover * 5.5F + hover * ((float)Math.sin((this.updateCounter + this.mc.getRenderPartialTicks()) / 8.0F) * 0.8F);

				ItemStack stack = slot.getStack();

				if(!stack.isEmpty()) {
					GlStateManager.pushMatrix();

					float alpha = this.setSlabTransform();

					Framebuffer fbo = this.mc.getFramebuffer();

					boolean useStencil = false;
					int stencilBit = MinecraftForgeClient.reserveStencilBit();
					int stencilMask = 1 << stencilBit;

					if(stencilBit >= 0) {
						useStencil = fbo.isStencilEnabled() ? true : fbo.enableStencil();
					}

					if(useStencil) {
						GlStateManager.translate(slot.xPos + hoverHeight / 2.25F + 8, slot.yPos + hoverHeight / 2.25F + 8, 0);
						GlStateManager.scale(1.0F / (1.0F + hoverHeight / 32.0F), 1.0F / (1.0F + hoverHeight / 32.0F), 1.0F);

						GL11.glEnable(GL11.GL_STENCIL_TEST);

						//Clear our stencil bit to 0
						GL11.glStencilMask(stencilMask);
						GL11.glClearStencil(0);
						GL11.glClear(GL11.GL_STENCIL_BUFFER_BIT);
						GL11.glStencilMask(~0);

						GL11.glStencilFunc(GL11.GL_ALWAYS, stencilMask, stencilMask);
						GL11.glStencilOp(GL11.GL_REPLACE, GL11.GL_REPLACE, GL11.GL_REPLACE);

						GlStateManager.colorMask(false, false, false, false);
						GlStateManager.depthMask(false);
						GlStateManager.enableAlpha();
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.05F);

						ColoredItemRenderer.renderItemAndEffectIntoGUI(this.itemRender, this.mc.player, stack, -8, -8, 1, 1, 1, 1);

						GL11.glStencilFunc(GL11.GL_EQUAL, stencilMask, stencilMask);
						GL11.glStencilOp(GL11.GL_KEEP, GL11.GL_KEEP, GL11.GL_KEEP);

						GlStateManager.colorMask(true, true, true, true);
						GlStateManager.depthMask(true);
						GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);

						int left = -16;
						int right = 16;
						int top = -16;
						int bottom = 16;

						Tessellator tessellator = Tessellator.getInstance();
						BufferBuilder bufferbuilder = tessellator.getBuffer();
						GlStateManager.enableBlend();
						GlStateManager.disableTexture2D();
						GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
						GlStateManager.color(0.15F, 0.15F, 0.15F, (Math.min(hoverPercent / 0.25F, 1.0F) * (1.0F - hover * 0.4F)) * alpha);
						bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
						bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
						bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
						bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
						bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
						tessellator.draw();
						GlStateManager.enableTexture2D();

						GlStateManager.disableBlend();

						GL11.glDisable(GL11.GL_STENCIL_TEST);
					}

					if(stencilBit >= 0) {
						MinecraftForgeClient.releaseStencilBit(stencilBit);
					}

					GlStateManager.color(1, 1, 1, 1);

					GlStateManager.popMatrix();

					this.revertSlabTransform();
				}

				GlStateManager.pushMatrix();
				GlStateManager.translate(0, -hoverHeight, 0);

				this.drawSlotItem(slot);

				GlStateManager.popMatrix();
			}
		} else if(!this.drawHoveringSlots) {
			this.drawSlotItem(slot);
		}
	}

	protected void drawSlotItem(Slot slot) {
		if(this.swapAnimationTicks > 0 && this.isSlabSlot(slot)) {
			this.zLevel = 100.0F;
			this.itemRender.zLevel = 100.0F;
			GlStateManager.enableDepth();

			float alpha = this.setSlabTransform();

			int x = slot.xPos;
			int y = slot.yPos;
			ItemStack itemstack = slot.getStack();

			ColoredItemRenderer.renderItemAndEffectIntoGUI(this.itemRender, this.mc.player, itemstack, x, y, 1, 1, 1, alpha);
			ColoredItemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, itemstack, x, y, null, 1, 1, 1, alpha);

			this.revertSlabTransform();

			this.itemRender.zLevel = 0.0F;
			this.zLevel = 0.0F;
		} else {
			super.drawSlot(slot);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		//Render hovering slots so that they render above the slot highlight

		this.drawHoveringSlots = true;
		for(int i = 0; i < this.inventorySlots.inventorySlots.size(); ++i) {
			Slot slot = this.inventorySlots.inventorySlots.get(i);

			if(slot.isEnabled()) {
				this.drawSlot(slot);
			}
		}
		this.drawHoveringSlots = false;

		RenderHelper.disableStandardItemLighting();

		this.drawLinkingDropdownMenus(mouseX, mouseY);

		this.drawRuneMarkConnections(mouseX, mouseY);

		GlStateManager.pushMatrix();
		GlStateManager.translate(-this.guiLeft, -this.guiTop, 0);

		for(IRuneGui gui : this.openRuneGuis.values()) {
			gui.drawForeground(mouseX, mouseY);
		}

		if(this.draggingMark != null) {
			IRuneGui primaryRuneGui = this.openRuneGuis.get(RuneMenuType.PRIMARY);

			if(primaryRuneGui != null) {
				if(primaryRuneGui.getInputMarks().contains(this.draggingMark) && this.draggingMark.isInteractable()) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(0, 0, 120);

					primaryRuneGui.drawMarkConnection(this.draggingMark, mouseX, mouseY, RuneMenuDrawingContext.Connection.CONNECTING); //TODO Check context

					GlStateManager.popMatrix();
				} else {
					this.draggingMark = null;
				}
			} else {
				this.draggingMark = null;
			}
		}

		GlStateManager.popMatrix();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int x, int y) {
		this.mc.getTextureManager().bindTexture(GUI_RUNE_CHAIN_ALTAR);

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
		GlStateManager.enableBlend();

		this.setSlabTransform();
		this.drawSlabBackground(this.guiLeft - 11, this.guiTop + 4);
		this.revertSlabTransform();

		this.drawTexturedModalRect512(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);

		this.setSlabTransform();
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.0F);
		this.drawSlabForeground(this.guiLeft - 11, this.guiTop + 4);
		GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
		this.revertSlabTransform();

		if(this.container.getCurrentPage().index > 0) {
			this.drawUpArrow(this.guiLeft + this.xSize + 2, this.guiTop + 18, x, y, this.setSlabTransform());
			this.revertSlabTransform();
		}

		if(this.container.getCurrentPage().index < this.container.getPages() - 1) {
			this.drawDownArrow(this.guiLeft + this.xSize + 2, this.guiTop + 100, x, y, this.setSlabTransform());
			this.revertSlabTransform();
		}

		this.setSlabTransform();

		for(int i = 0; i < Math.min(ContainerRuneChainAltar.SLOTS_PER_PAGE + 1, this.tile.getChainLength() - this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE); i++) {
			this.drawCordPiece(this.guiLeft - 11, this.guiTop + 4, i);
		}

		int coverStartIndex = this.tile.getChainLength() + (this.tile.isOutputItemAvailable() ? 1 : 0);

		for(int i = coverStartIndex; i < this.tile.getMaxChainLength(); i++) {
			SlotRuneChainAltarInput slot = (SlotRuneChainAltarInput) this.inventorySlots.getSlot(i + TileEntityRuneChainAltar.NON_INPUT_SLOTS);

			if(slot.getPage().isCurrent()) {
				this.drawSlotCoverStone(this.guiLeft + slot.xPos, this.guiTop + slot.yPos);
			}
		}
		int unfilledSlots = Math.max(0, ContainerRuneChainAltar.SLOTS_PER_PAGE - (this.tile.getMaxChainLength() - this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE));
		for(int i = ContainerRuneChainAltar.SLOTS_PER_PAGE - unfilledSlots; i < ContainerRuneChainAltar.SLOTS_PER_PAGE; i++) {
			this.drawSlotCoverStone(this.guiLeft + ContainerRuneChainAltar.SLOT_POSITIONS[i][0], this.guiTop + ContainerRuneChainAltar.SLOT_POSITIONS[i][1]);
		}

		this.revertSlabTransform();

		for(int i = 0; i < ContainerRuneChainAltar.SLOTS_PER_PAGE; i++) {
			int slotIndex = this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE + TileEntityRuneChainAltar.NON_INPUT_SLOTS + i;

			SlotRuneChainAltarInput slot = (SlotRuneChainAltarInput) this.inventorySlots.getSlot(slotIndex);

			if(slot.getHasStack() && x >= this.guiLeft + slot.xPos - 4 && x <= this.guiLeft + slot.xPos + 16 + 3 && y >= this.guiTop + slot.yPos - 10 && y < this.guiTop + slot.yPos + 16 + 10) {
				if(this.container.getShiftHoleSlot(slotIndex, false) >= 0) {
					if(i >= ContainerRuneChainAltar.SLOTS_PER_PAGE / 2) {
						this.drawShiftLeftArrow(this.guiLeft + slot.xPos + 2, this.guiTop + slot.yPos - 9, x, y, this.setSlabTransform());
						this.revertSlabTransform();
					} else {
						this.drawShiftRightArrow(this.guiLeft + slot.xPos + 2, this.guiTop + slot.yPos - 9, x, y, this.setSlabTransform());
						this.revertSlabTransform();
					}
				}

				if(this.container.getShiftHoleSlot(slotIndex, true) >= 0) {
					if(i >= ContainerRuneChainAltar.SLOTS_PER_PAGE / 2) {
						this.drawShiftRightArrow(this.guiLeft + slot.xPos + 2, this.guiTop + slot.yPos + 9 + 9, x, y, this.setSlabTransform());
						this.revertSlabTransform();
					} else {
						this.drawShiftLeftArrow(this.guiLeft + slot.xPos + 2, this.guiTop + slot.yPos + 9 + 9, x, y, this.setSlabTransform());
						this.revertSlabTransform();
					}
				}
			}
		}

		for(IRuneGui gui : this.openRuneGuis.values()) {
			gui.drawBackground(x, y);
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		boolean handled = false;

		if(this.swapAnimationTicks == 0) {
			if(mouseButton == 0) {
				if(!handled) {
					int relX = mouseX - (this.guiLeft + this.xSize + 2);
					int relY = mouseY - (this.guiTop + 20);
					if(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24) {
						this.movePage(-1);
						handled = true;
					}
				}

				if(!handled) {
					int relX = mouseX - (this.guiLeft + this.xSize + 2);
					int relY = mouseY - (this.guiTop + 100);
					if(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24) {
						this.movePage(1);
						handled = true;
					}
				}

				if(!handled) {
					for(int i = 0; i < ContainerRuneChainAltar.SLOTS_PER_PAGE; i++) {
						int slotIndex = this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE + TileEntityRuneChainAltar.NON_INPUT_SLOTS + i;

						SlotRuneChainAltarInput slot = (SlotRuneChainAltarInput) this.inventorySlots.getSlot(slotIndex);

						if(slot.getHasStack() && mouseX >= this.guiLeft + slot.xPos - 4 && mouseX < this.guiLeft + slot.xPos + 16 + 3 && mouseY >= this.guiTop + slot.yPos - 10 && mouseY < this.guiTop + slot.yPos + 16 + 10) {
							if(this.container.getShiftHoleSlot(slotIndex, false) >= 0) {
								int relX = mouseX - (this.guiLeft + slot.xPos + 2);
								int relY = mouseY - (this.guiTop + slot.yPos - 9);
								if(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7) {
									this.container.shiftSlot(slotIndex, false);
									TheBetweenlands.networkWrapper.sendToServer(new MessageShiftRuneChainAltarSlot(slotIndex, false));
									this.mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.RUNE_SLOT_SHIFT, rand.nextFloat() * 0.066F + 0.933F, 1.0F));
									handled = true;
									break;
								}
							}

							if(this.container.getShiftHoleSlot(slotIndex, true) >= 0) {
								int relX = mouseX - (this.guiLeft + slot.xPos + 2);
								int relY = mouseY - (this.guiTop + slot.yPos + 9 + 9);
								if(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7) {
									this.container.shiftSlot(slotIndex, true);
									TheBetweenlands.networkWrapper.sendToServer(new MessageShiftRuneChainAltarSlot(slotIndex, true));
									this.mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.RUNE_SLOT_SHIFT, rand.nextFloat() * 0.066F + 0.933F, 1.0F));
									handled = true;
									break;
								}
							}
						}
					}
				}
			}

			super.mouseClicked(mouseX, mouseY, mouseButton);
		}

		if(!handled) {
			IRuneGui primaryRuneGui = this.openRuneGuis.get(RuneMenuType.PRIMARY);

			if(primaryRuneGui != null) {
				for(IGuiRuneMark mark : primaryRuneGui.getInputMarks()) {
					if(mark.isInteractable() && mark.isInside(mark.getCenterX(), mark.getCenterY(), mouseX, mouseY)) {
						if(mouseButton == 0) {
							if(!primaryRuneGui.onStartMarkLinking(mark, mouseX, mouseY)) {
								this.draggingMark = mark;
								handled = true;
								break;
							}
						} else if(mouseButton == 1) {
							if(!primaryRuneGui.onStartMarkUnlinking(mark, mouseX, mouseY)) {
								IRuneLink unlinked = this.container.unlink(primaryRuneGui.getContainer().getContext().getRuneIndex(), mark.getMarkIndex());
								if(unlinked != null) {
									//Send message to link on server side too
									TheBetweenlands.networkWrapper.sendToServer(new MessageUnlinkRuneChainAltarRune(primaryRuneGui.getContainer().getContext().getRuneIndex(), mark.getMarkIndex()));
								}
								break;
							}
						}
					}
				}
			}
		}

		for(IRuneGui gui : this.openRuneGuis.values()) {
			handled |= gui.onMouseClicked(mouseX, mouseY, mouseButton, handled);
		}
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int state) {
		super.mouseReleased(mouseX, mouseY, state);

		boolean handled = false;

		if(this.draggingMark != null) {
			this.recentlyLinked = null;

			if(this.container.getSelectedRuneIndex() >= 0 && this.linkingDropdownMenuSlot >= 0) {
				int linkingMarkIndex = this.getLinkingDropdownMenuMarkIndex(mouseX, mouseY);

				if(linkingMarkIndex >= 0) {
					if(this.container.link(this.container.getSelectedRuneIndex(), this.draggingMark.getMarkIndex(), this.linkingDropdownMenuSlot - this.tile.getChainStart(), linkingMarkIndex)) {
						//Send message to link on server side too
						TheBetweenlands.networkWrapper.sendToServer(new MessageLinkRuneChainAltarRune(this.container.getSelectedRuneIndex(), this.draggingMark.getMarkIndex(), this.linkingDropdownMenuSlot - this.tile.getChainStart(), linkingMarkIndex));

						IRuneLink link = this.container.getLink(this.container.getSelectedRuneIndex(), this.draggingMark.getMarkIndex());
						if(link != null) {
							this.recentlyLinked = new Tuple<>(this.draggingMark, link);
						}
					}
				}
			}

			if(this.recentlyLinked == null) {
				this.linkingDropdownMenuSlot = -1;
			}

			this.draggingMark = null;

			handled = true;
		}

		for(IRuneGui gui : this.openRuneGuis.values()) {
			handled |= gui.onMouseReleased(mouseX, mouseY, state, handled);
		}
	}

	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();

		int scroll = Mouse.getEventDWheel();

		if(scroll < 0) {
			this.movePage(1);
		} else if(scroll > 0) {
			this.movePage(-1);
		}

		for(IRuneGui gui : this.openRuneGuis.values()) {
			gui.onMouseInput();
		}
	}

	protected int movePage(int i) {
		if(i > 0 && this.container.getCurrentPage().index < this.container.getPages() - 1 && this.swapAnimationTicks < SWAP_ANIMATION_HALF_DURATION) {
			int start = this.newPage >= 0 ? this.newPage : this.container.getCurrentPage().index;
			this.newPage = Math.min(start + i, this.container.getPages() - 1);
			TheBetweenlands.networkWrapper.sendToServer(new MessageSetRuneChainAltarPage(this.newPage));
			if(this.swapAnimationTicks <= 0) {
				this.swapAnimationTicks = 1;
			}
			return this.newPage - start;
		} else if(i < 0 && this.container.getCurrentPage().index > 0 && this.swapAnimationTicks < SWAP_ANIMATION_HALF_DURATION) {
			int start = this.newPage >= 0 ? this.newPage : this.container.getCurrentPage().index;
			this.newPage = Math.max(start + i, 0);
			TheBetweenlands.networkWrapper.sendToServer(new MessageSetRuneChainAltarPage(this.newPage));
			if(this.swapAnimationTicks <= 0) {
				this.swapAnimationTicks = 1;
			}
			return this.newPage - start;
		}
		return 0;
	}

	@Override
	public void updateScreen() {
		//Make sure that the currently selected and secondary rune GUI is always correct
		if(this.container.getSelectedRuneIndex() >= 0) {
			this.updateSelectedRuneGui(this.container.getSelectedRuneIndex());
		}
		this.updateSecondaryRuneGui();

		super.updateScreen();

		this.updateCounter++;

		this.lastSwapAnimationTicks = this.swapAnimationTicks;
		if(this.swapAnimationTicks > 0) {
			if(this.swapAnimationTicks == 1) {
				this.mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.RUNE_SLATE_MOVE, rand.nextFloat() * 0.066F + 0.933F, 1.0F));
				this.container.getCurrentPage().interactable = false;
			}
			if(this.swapAnimationTicks == SWAP_ANIMATION_HALF_DURATION && this.newPage >= 0) {
				this.container.getCurrentPage().interactable = true;
				this.container.setCurrentPage(this.newPage);
				this.newPage = -1;
				this.container.getCurrentPage().interactable = false;
			}
			if(this.swapAnimationTicks >= SWAP_ANIMATION_HALF_DURATION * 2) {
				this.container.getCurrentPage().interactable = true;
				this.swapAnimationTicks = 0;
			} else {
				this.swapAnimationTicks++;
			}
		}

		this.updateSlotHoverTicks();

		this.updateLinkingDropdownMenus();

		for(IRuneGui gui : this.openRuneGuis.values()) {
			gui.update();
		}
	}

	protected void updateSlotHoverTicks() {
		for(Slot slot : this.container.inventorySlots) {
			if(slot instanceof SlotRuneChainAltarInput) {
				SlotRuneChainAltarInput slotRune = (SlotRuneChainAltarInput) slot;

				slotRune.prevHoverTicks = slotRune.hoverTicks;

				if(slotRune.slotNumber == this.container.getSelectedSlot()) {
					if(slotRune.hoverTicks < 7) {
						slotRune.hoverTicks++;
					} else {
						slotRune.hoverTicks = 7;
					}
				} else {
					if(slotRune.hoverTicks > 0) {
						slotRune.hoverTicks--;
					} else {
						slotRune.hoverTicks = 0;
					}
				}
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		super.keyTyped(typedChar, keyCode);

		boolean handled = false;
		for(IRuneGui gui : this.openRuneGuis.values()) {
			handled |= gui.onKeyTyped(typedChar, keyCode, handled);
		}
	}

	@Override
	public void setGuiSize(int w, int h) {
		super.setGuiSize(w, h);

		for(IRuneGui gui : this.openRuneGuis.values()) {
			gui.onParentSizeSet(w, h);
		}
	}

	@Override
	public void setWorldAndResolution(Minecraft mc, int width, int height) {
		super.setWorldAndResolution(mc, width, height);

		for(IRuneGui gui : this.openRuneGuis.values()) {
			gui.onParentSizeSet(width, height);
		}
	}

	protected List<Integer> getCurrentlyLinkableOutputs(int outputRuneIndex, INodeConfiguration outputConfiguration) {
		IRuneGui primaryGui = this.openRuneGuis.get(RuneMenuType.PRIMARY);
		if(primaryGui != null && this.draggingMark != null) {
			IConfigurationInput input = primaryGui.getContainer().getContext().getConfiguration().getInputs().get(this.draggingMark.getMarkIndex());
			if(input != null) {
				return this.getLinkableOutputs(input, outputRuneIndex, outputConfiguration);
			}
		}
		return Collections.emptyList();
	}

	protected List<Integer> getLinkableOutputs(IConfigurationInput input, int outputRuneIndex, INodeConfiguration outputConfiguration) {
		List<Integer> linkableOutputs = new ArrayList<>();

		int outputIndex = 0;
		for(IConfigurationOutput output : outputConfiguration.getOutputs()) {
			List<IType> inputTypes = this.getInputTypes(outputRuneIndex, outputConfiguration);

			if(output.isEnabled(inputTypes) && input.test(output, output.getType(inputTypes))) {
				linkableOutputs.add(outputIndex);
			}

			outputIndex++;
		}

		return linkableOutputs;
	}

	protected List<IType> getInputTypes(int runeIndex, INodeConfiguration configuration) {
		List<IConfigurationInput> inputs = configuration.getInputs();

		List<IType> inputTypes = new ArrayList<>();

		for(int i = 0; i < inputs.size(); i++) {
			IRuneLink link = this.container.getLink(runeIndex, i);

			if(link != null) {
				INodeConfiguration inputConfiguration = this.container.getRuneContainer(link.getOutputRune()).getContext().getConfiguration();
				inputTypes.add(this.getOutputType(link.getOutputRune(), inputConfiguration, inputConfiguration.getOutputs().get(link.getOutput())));
			} else {
				inputTypes.add((IType) null);
			}
		}

		return inputTypes;
	}

	protected IType getOutputType(int runeIndex, INodeConfiguration configuration, IConfigurationOutput output) {
		List<IConfigurationInput> inputs = configuration.getInputs();

		List<IType> inputTypes = new ArrayList<>();

		for(int i = 0; i < inputs.size(); i++) {
			IRuneLink link = this.container.getLink(runeIndex, i);

			if(link != null) {
				INodeConfiguration inputConfiguration = this.container.getRuneContainer(link.getOutputRune()).getContext().getConfiguration();
				inputTypes.add(this.getOutputType(link.getOutputRune(), inputConfiguration, inputConfiguration.getOutputs().get(link.getOutput())));
			} else {
				inputTypes.add((IType) null);
			}
		}

		return output.getType(inputTypes);
	}

	protected void updateLinkingDropdownMenus() {
		int mouseX = Mouse.getX() * this.width / this.mc.displayWidth;
		int mouseY = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;

		if(this.linkingDropdownMenuSlot >= 0) {
			Slot slot = this.inventorySlots.getSlot(this.linkingDropdownMenuSlot);

			if(slot instanceof SlotRuneChainAltarInput)  {
				IRuneContainer container = this.container.getRuneContainer(this.linkingDropdownMenuSlot - this.tile.getChainStart());

				if(container != null) {
					INodeConfiguration configuration = container.getContext().getConfiguration();
					int outputs;
					if(this.draggingMark != null) {
						outputs = this.getCurrentlyLinkableOutputs(this.linkingDropdownMenuSlot - this.tile.getChainStart(), configuration).size();
					} else {
						outputs = configuration.getOutputs().size();
					}

					if(!this.isInsideLinkingDropdownMenuArea(slot, outputs, mouseX, mouseY, true)) {
						this.linkingDropdownMenuSlot = -1;
						this.recentlyLinked = null;
					}
				} else {
					this.linkingDropdownMenuSlot = -1;
					this.recentlyLinked = null;
				}
			} else {
				this.linkingDropdownMenuSlot = -1;
				this.recentlyLinked = null;
			}
		} else {
			this.linkingDropdownMenuSlot = -1;
			this.recentlyLinked = null;
		}

		if(this.draggingMark != null && this.linkingDropdownMenuSlot < 0) {
			IRuneGui primaryRuneGui = this.openRuneGuis.get(RuneMenuType.PRIMARY);

			if(primaryRuneGui != null) {
				for(Slot slot : this.container.inventorySlots) {
					if(slot instanceof SlotRuneChainAltarInput && slot.isEnabled() && slot.slotNumber - this.tile.getChainStart() < primaryRuneGui.getContainer().getContext().getRuneIndex()) {
						IRuneContainer container = this.container.getRuneContainer(slot.slotNumber - this.tile.getChainStart());

						if(container != null) {
							INodeConfiguration configuration = container.getContext().getConfiguration();
							int outputs = this.getCurrentlyLinkableOutputs(slot.slotNumber - this.tile.getChainStart(), configuration).size();

							if(outputs > 0) {
								int sx = slot.xPos - 3;
								int sy = slot.yPos - 3;

								int minX = this.guiLeft + sx;
								int minY = this.guiTop + sy;
								int maxX = this.guiLeft + sx + 16 + 6;
								int maxY = this.guiTop + sy + 16 + 6;

								if(mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY) {
									this.linkingDropdownMenuSlot = slot.slotNumber;
								}
							}
						}
					}
				}
			}
		}

		if(this.swapAnimationTicks > 0) {
			this.linkingDropdownMenuSlot = -1;
			this.recentlyLinked = null;
		}
	}

	protected boolean isInsideLinkingDropdownMenu(int mouseX, int mouseY, boolean onlyLinkable) {
		if(this.linkingDropdownMenuSlot >= 0) {
			IRuneContainer container = this.container.getRuneContainer(this.linkingDropdownMenuSlot - this.tile.getChainStart());

			if(container != null) {
				INodeConfiguration configuration = container.getContext().getConfiguration();

				int outputs;
				if(onlyLinkable) {
					outputs = this.getCurrentlyLinkableOutputs(this.linkingDropdownMenuSlot - this.tile.getChainStart(), configuration).size();
				} else {
					outputs = configuration.getOutputs().size();
				}

				return this.isInsideLinkingDropdownMenuArea(this.inventorySlots.getSlot(this.linkingDropdownMenuSlot), outputs, mouseX, mouseY, false);
			}
		}

		return false;
	}

	protected boolean isInsideLinkingDropdownMenuArea(Slot slot, int outputs, int mouseX, int mouseY, boolean includeSlot) {
		int sx = slot.xPos - 3;
		int sy = slot.yPos - 3;

		int minX = this.guiLeft + sx;
		int minY = this.guiTop + sy + (!includeSlot ? 18 : 0);
		int maxX = this.guiLeft + sx + 22;
		int maxY = this.guiTop + sy + outputs * 18 + 6 + 17;

		return mouseX >= minX && mouseX < maxX && mouseY >= minY && mouseY < maxY;
	}

	protected int getLinkingDropdownMenuMarkIndex(int mouseX, int mouseY) {
		if(this.linkingDropdownMenuSlot >= 0 && this.isInsideLinkingDropdownMenu(mouseX, mouseY, true)) {
			IRuneContainer container = this.container.getRuneContainer(this.linkingDropdownMenuSlot - this.tile.getChainStart());
			Slot slot = this.inventorySlots.getSlot(this.linkingDropdownMenuSlot);

			if(container != null) {
				IRuneGui secondaryRuneGui = this.openRuneGuis.get(RuneMenuType.SECONDARY);

				if(secondaryRuneGui != null) {
					INodeConfiguration configuration = container.getContext().getConfiguration();
					List<Integer> outputs = this.getCurrentlyLinkableOutputs(this.linkingDropdownMenuSlot - this.tile.getChainStart(), configuration);

					int sx = slot.xPos - 3;
					int sy = slot.yPos - 3;

					int yOff = 0;
					for(int output : outputs) {
						int cx = this.guiLeft + sx + 3 + 8;
						int cy = this.guiTop + sy + 3 + yOff + 8 + 18;

						IGuiRuneMark mark = secondaryRuneGui.getOutputMark(output);

						if(mark.isInside(cx, cy, mouseX, mouseY)) {
							return output;
						}

						yOff += 18;
					}
				}
			}
		}

		return -1;
	}

	protected void drawLinkingDropdownMenus(int mouseX, int mouseY) {
		if(this.linkingDropdownMenuSlot >= 0) {
			this.drawDropdownMenu(this.inventorySlots.getSlot(this.linkingDropdownMenuSlot), mouseX, mouseY, true);
		}
	}

	protected void drawDropdownMenu(Slot slot, int mouseX, int mouseY, boolean onlyLinkable) {
		IRuneContainer container = this.container.getRuneContainer(slot.slotNumber - this.tile.getChainStart());

		if(container != null) {
			INodeConfiguration configuration = container.getContext().getConfiguration();
			List<Integer> outputs;
			if(onlyLinkable) {
				outputs = this.getCurrentlyLinkableOutputs(slot.slotNumber - this.tile.getChainStart(), configuration);
			} else {
				outputs = IntStream.range(0, configuration.getOutputs().size()).boxed().collect(Collectors.toList());
			}

			if(outputs.size() > 0) {
				int sx = slot.xPos - 3;
				int sy = slot.yPos - 3;

				this.mc.getTextureManager().bindTexture(GUI_RUNE_CHAIN_ALTAR);

				this.zLevel = 270;
				this.drawDrowndownMenuBackground(sx, sy, outputs.size() * 18 + 3 + 18);
				this.zLevel = 0;

				GlStateManager.pushMatrix();
				GlStateManager.translate(0, 0, 120);

				this.drawSlot(slot);

				GlStateManager.popMatrix();

				IRuneGui secondaryGui = this.openRuneGuis.get(RuneMenuType.SECONDARY);

				if(secondaryGui != null) {
					Tuple<IGuiRuneMark, IRuneLink> link = this.getHoveredOnLink(mouseX, mouseY);

					if(link == null) {
						link = this.recentlyLinked;
					}

					GlStateManager.pushMatrix();
					GlStateManager.translate(-this.guiLeft, -this.guiTop, 271);

					int yOff = 0;
					for(int output : outputs) {
						int cx = this.guiLeft + sx + 3 + 8;
						int cy = this.guiTop + sy + 3 + yOff + 8 + 18;

						IGuiRuneMark mark = secondaryGui.getOutputMark(output);

						RuneMenuDrawingContext.Mark drawingContext;
						boolean isLinked = link != null && link.getSecond().getOutput() == output;
						boolean isConnecting = this.draggingMark != null;
						if(isLinked && isConnecting) {
							drawingContext = RuneMenuDrawingContext.Mark.DROPDOWN_CONNECTION_AND_CONNECTING;
						} else if(isLinked) {
							drawingContext = RuneMenuDrawingContext.Mark.DROPDOWN_CONNECTION;
						} else if(isConnecting) {
							drawingContext = RuneMenuDrawingContext.Mark.DROPDOWN_CONNECTING;
						} else {
							drawingContext = RuneMenuDrawingContext.Mark.DROPDOWN;
						}

						secondaryGui.drawMark(mark, cx, cy, drawingContext);

						yOff += 18;
					}

					if(this.draggingMark != null) {
						yOff = 0;
						for(int output : outputs) {
							int cx = this.guiLeft + sx + 3 + 8;
							int cy = this.guiTop + sy + 3 + yOff + 8 + 18;

							IGuiRuneMark mark = secondaryGui.getOutputMark(output);

							if(mark.isInside(cx, cy, mouseX, mouseY)) {
								secondaryGui.drawMarkTooltip(mark, cx, cy, mouseX, mouseY, link != null && link.getSecond().getOutput() == output ? RuneMenuDrawingContext.Tooltip.DROPDOWN_CONNECTION_AND_CONNECTING : RuneMenuDrawingContext.Tooltip.DROPDOWN_CONNECTING);
							}

							yOff += 18;
						}
					}

					GlStateManager.popMatrix();
				}

				GlStateManager.color(1, 1, 1, 1);
			}
		}
	}

	protected void drawDrowndownMenuBackground(int x, int y, int height) {
		height -= 5;

		//Top left corner
		this.drawTexturedModalRect512(x, y, 388, 94, 3, 3);
		//Top bar
		this.drawTexturedModalRect512(x + 3, y, 391, 94, 16, 3);
		//Top right corner
		this.drawTexturedModalRect512(x + 3 + 16, y, 407, 94, 3, 3);
		//Right bar
		this.drawTexturedModalRect512(x + 3 + 16, y + 3, 407, 97, 3, height);
		//Bottom right corner
		this.drawTexturedModalRect512(x + 3 + 16, y + 3 + height, 407, 313, 3, 3);
		//Bottom bar
		this.drawTexturedModalRect512(x + 3, y + 3 + height, 391, 313, 16, 3);
		//Bottom left corner
		this.drawTexturedModalRect512(x, y + 3 + height, 388, 313, 3, 3);
		//Left bar
		this.drawTexturedModalRect512(x, y + 3, 388, 97, 3, height);

		//Background
		this.drawTexturedModalRect512(x + 3, y + 3, 391, 97, 16, height);
	}

	@Nullable
	protected Tuple<IGuiRuneMark, IRuneLink> getHoveredOnLink(int mouseX, int mouseY) {
		IRuneGui primaryRuneGui = this.openRuneGuis.get(RuneMenuType.PRIMARY);

		if(primaryRuneGui != null) {
			for(IGuiRuneMark mark : primaryRuneGui.getInputMarks()) {
				if(mark.isInteractable() && mark.isInside(mark.getCenterX(), mark.getCenterY(), mouseX, mouseY)) {
					IRuneLink link = this.container.getLink(primaryRuneGui.getContainer().getContext().getRuneIndex(), mark.getMarkIndex());

					if(link != null) {
						return new Tuple<>(mark, link);
					}
				}
			}
		}

		return null;
	}

	protected void drawRuneMarkConnections(int mouseX, int mouseY) {
		if(isShiftKeyDown()) {
			this.setSlabTransform();

			for(int runeIndex = 0; runeIndex < this.container.getRuneInventorySize(); runeIndex++) {
				Slot runeSlot = this.container.getRuneSlot(runeIndex);

				if(runeSlot instanceof SlotRuneChainAltarInput && ((SlotRuneChainAltarInput) runeSlot).isEnabled() && !this.container.getRuneItemStack(runeIndex).isEmpty()) {
					Page runeSlotPage = ((SlotRuneChainAltarInput) runeSlot).getPage();
					
					Set<Integer> visitedRunes = new HashSet<>();

					for(int linkedInput : this.container.getLinkedInputs(runeIndex)) {
						IRuneLink link = this.container.getLink(runeIndex, linkedInput);

						if(visitedRunes.add(link.getOutputRune())) {
							Slot linkedSlot = this.container.getRuneSlot(link.getOutputRune());

							if(linkedSlot instanceof SlotRuneChainAltarInput) {
								Page linkedSlotPage = ((SlotRuneChainAltarInput) linkedSlot).getPage();
								
								boolean isTopHalf = runeIndex >= runeSlotPage.index * ContainerRuneChainAltar.SLOTS_PER_PAGE && runeIndex < runeSlotPage.index * ContainerRuneChainAltar.SLOTS_PER_PAGE + ContainerRuneChainAltar.SLOTS_PER_PAGE / 2;
								boolean isLinkedTopHalf = link.getOutputRune() >= linkedSlotPage.index * ContainerRuneChainAltar.SLOTS_PER_PAGE && link.getOutputRune() < linkedSlotPage.index * ContainerRuneChainAltar.SLOTS_PER_PAGE + ContainerRuneChainAltar.SLOTS_PER_PAGE / 2;
								
								if(((SlotRuneChainAltarInput) linkedSlot).isEnabled()) {
									DefaultRuneGui.drawHangingRope(this.updateCounter + runeIndex * 50, runeSlot.xPos + 8, runeSlot.yPos + 8, linkedSlot.xPos + 8, linkedSlot.yPos + 8, !isTopHalf && isLinkedTopHalf ? -14.0F : 14.0F, this.zLevel);
								} else {
									DefaultRuneGui.drawHangingRope(this.updateCounter + runeIndex * 50, runeSlot.xPos + 8, runeSlot.yPos + 8, 88, 70, !isTopHalf ? -6.0F : 0.0F, this.zLevel);
								}
							}
						}
					}
				}
			}

			this.revertSlabTransform();

			this.mc.getTextureManager().bindTexture(GUI_RUNE_CHAIN_ALTAR);
		}

		IRuneGui primaryRuneGui = this.openRuneGuis.get(RuneMenuType.PRIMARY);

		if(primaryRuneGui != null) {
			Tuple<IGuiRuneMark, IRuneLink> link = this.getHoveredOnLink(mouseX, mouseY);

			if(link == null) {
				link = this.recentlyLinked;
			}

			if(link != null) {
				IRuneGui secondaryRuneGui = this.openRuneGuis.get(RuneMenuType.SECONDARY);
				IGuiRuneMark secondaryGuiRuneMark = null;

				if(secondaryRuneGui != null) {
					IGuiRuneMark mark = secondaryRuneGui.getOutputMark(link.getSecond().getOutput());
					if(mark.isInteractable()) {
						secondaryGuiRuneMark = mark;
					}
				}

				Slot linkedSlot = this.inventorySlots.getSlot(link.getSecond().getOutputRune() + this.tile.getChainStart());

				if(this.swapAnimationTicks == 0 && linkedSlot instanceof SlotRuneChainAltarInput && linkedSlot.isEnabled()) {
					this.drawDropdownMenu(linkedSlot, mouseX, mouseY, false);

					int sy = this.guiTop + linkedSlot.yPos - 3;

					int cx = this.guiLeft + linkedSlot.xPos - 3 + 12;
					int cy = sy + link.getSecond().getOutput() * 18 + 11 + 18;

					GlStateManager.pushMatrix();
					GlStateManager.translate(-this.guiLeft, -this.guiTop, 120);

					primaryRuneGui.drawMarkConnection(link.getFirst(), cx, cy, RuneMenuDrawingContext.Connection.VIA_DROPDOWN);

					if(secondaryGuiRuneMark != null) {
						secondaryRuneGui.drawMarkConnection(secondaryGuiRuneMark, cx, cy, RuneMenuDrawingContext.Connection.VIA_DROPDOWN);

						secondaryRuneGui.drawMarkTooltip(secondaryGuiRuneMark, secondaryGuiRuneMark.getCenterX(), secondaryGuiRuneMark.getCenterY(), secondaryGuiRuneMark.getCenterX(), secondaryGuiRuneMark.getCenterY(), RuneMenuDrawingContext.Tooltip.CONNECTION_END);
					}

					GlStateManager.popMatrix();
				} else if(secondaryGuiRuneMark != null) {
					GlStateManager.pushMatrix();
					GlStateManager.translate(-this.guiLeft, -this.guiTop, 120);

					primaryRuneGui.drawMarkConnection(link.getFirst(), secondaryGuiRuneMark.getCenterX(), secondaryGuiRuneMark.getCenterY(), RuneMenuDrawingContext.Connection.DIRECTLY);

					secondaryRuneGui.drawMarkTooltip(secondaryGuiRuneMark, secondaryGuiRuneMark.getCenterX(), secondaryGuiRuneMark.getCenterY(), secondaryGuiRuneMark.getCenterX(), secondaryGuiRuneMark.getCenterY(), RuneMenuDrawingContext.Tooltip.CONNECTION_END);

					GlStateManager.popMatrix();
				}
			}
		}
	}

	protected void drawSlotCoverStone(float x, float y) {
		this.drawTexturedModalRect512(x, y, 216, 17, 16, 16);
	}

	protected void drawUpArrow(float x, float y, int mouseX, int mouseY, float alpha) {
		int relX = mouseX - (int) x;
		int relY = mouseY - (int) y;
		if(this.swapAnimationTicks != 0 || !(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24)) {
			GlStateManager.color(0.7F, 0.7F, 0.7F, alpha);
		}
		this.drawTexturedModalRect512(x, y, 187, 16, 15, 24);
		GlStateManager.color(1, 1, 1);
	}

	protected void drawDownArrow(float x, float y, int mouseX, int mouseY, float alpha) {
		int relX = mouseX - (int) x;
		int relY = mouseY - (int) y;
		if(this.swapAnimationTicks != 0 || !(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24)) {
			GlStateManager.color(0.7F, 0.7F, 0.7F, alpha);
		}
		this.drawTexturedModalRect512(x, y, 187, 108, 15, 24);
		GlStateManager.color(1, 1, 1);
	}

	protected void drawShiftLeftArrow(float x, float y, int mouseX, int mouseY, float alpha) {
		int relX = mouseX - (int) x;
		int relY = mouseY - (int) y;
		if(this.swapAnimationTicks != 0 || !(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7)) {
			GlStateManager.color(0.7F, 0.7F, 0.7F, alpha);
		}
		this.drawTexturedModalRect512(x, y, 452, 0, 13, 7);
		GlStateManager.color(1, 1, 1);
	}

	protected void drawShiftRightArrow(float x, float y, int mouseX, int mouseY, float alpha) {
		int relX = mouseX - (int) x;
		int relY = mouseY - (int) y;
		if(this.swapAnimationTicks != 0 || !(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7)) {
			GlStateManager.color(0.7F, 0.7F, 0.7F, alpha);
		}
		this.drawTexturedModalRect512(x, y, 452, 29, 13, 7);
		GlStateManager.color(1, 1, 1);
	}

	protected void drawSlabForeground(float x, float y) {
		this.drawTexturedModalRect512(x, y, 0, 224, 198, 134);
	}

	protected void drawSlabBackground(float x, float y) {
		this.drawTexturedModalRect512(x, y, 0, 357, 198, 134);
	}

	protected void drawCordPiece(float slabX, float slabY, int piece) {
		this.drawCordPiecePositioned(CORD_PIECE_SLOT_UVs[piece][0] + slabX, CORD_PIECE_SLOT_UVs[piece][1] - 224 + slabY, piece);
	}

	protected void drawCordPiecePositioned(float x, float y, int piece) {
		this.drawTexturedModalRect512(x, y, CORD_PIECE_UVs[piece][0], CORD_PIECE_UVs[piece][1], CORD_PIECE_SIZES[piece][0], CORD_PIECE_SIZES[piece][1]);
	}

	/**
	 * Same as {@link #drawTexturedModalRect(int, int, int, int, int, int)} but for 512x512 textures
	 */
	protected void drawTexturedModalRect512(float x, float y, int minU, int minV, int width, int height) {
		float scale = 0.001953125F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
		bufferbuilder.pos((double)(x + 0), (double)(y + height), (double)this.zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + height) * scale)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + height), (double)this.zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + height) * scale)).endVertex();
		bufferbuilder.pos((double)(x + width), (double)(y + 0), (double)this.zLevel).tex((double)((float)(minU + width) * scale), (double)((float)(minV + 0) * scale)).endVertex();
		bufferbuilder.pos((double)(x + 0), (double)(y + 0), (double)this.zLevel).tex((double)((float)(minU + 0) * scale), (double)((float)(minV + 0) * scale)).endVertex();
		tessellator.draw();
	}

	@Override
	public Collection<IRuneGui> getOpenRuneGuis() {
		return this.openRuneGuis.values();
	}

	@Override
	public int getMinX() {
		return this.guiLeft;
	}

	@Override
	public int getMinY() {
		return this.guiTop;
	}

	@Override
	public int getMaxX() {
		return this.guiLeft + this.xSize;
	}

	@Override
	public int getMaxY() {
		return this.guiTop + this.ySize;
	}

	@Override
	public boolean isRuneSlotInteractable(int runeIndex) {
		if(this.swapAnimationTicks != 0) {
			return false;
		}
		Slot slot = this.container.getRuneSlot(runeIndex);
		return slot instanceof SlotRuneChainAltarInput ? ((SlotRuneChainAltarInput) slot).isEnabled() : false;
	}
}

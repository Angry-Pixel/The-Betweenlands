package thebetweenlands.client.gui.inventory;

import java.io.IOException;
import java.util.Random;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

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
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import thebetweenlands.api.rune.INodeBlueprint;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.impl.RuneChainComposition.RuneExecutionContext;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.inventory.container.ContainerRuneChainAltar;
import thebetweenlands.common.inventory.slot.SlotRune;
import thebetweenlands.common.item.herblore.ItemRune;
import thebetweenlands.common.network.serverbound.MessageSetRuneChainAltarPage;
import thebetweenlands.common.network.serverbound.MessageShiftRuneChainAltarSlot;
import thebetweenlands.common.registries.SoundRegistry;
import thebetweenlands.common.tile.TileEntityRuneChainAltar;
import thebetweenlands.util.ColoredItemRenderer;

@SideOnly(Side.CLIENT)
public class GuiRuneChainAltar extends GuiContainer {
	private static final Random rand = new Random();

	private static final ResourceLocation GUI_RUNE_CHAIN_ALTAR = new ResourceLocation("thebetweenlands:textures/gui/rune_chain_altar.png");
	private static final ResourceLocation GUI_RUNE_CHAIN_ALTAR_ROPE = new ResourceLocation("thebetweenlands:textures/gui/rune_chain_altar_rope.png");

	private final TileEntityRuneChainAltar tile;
	private final ContainerRuneChainAltar container;
	private final EntityPlayer player;

	private static final int SWAP_ANIMATION_HALF_DURATION = 7;
	private int lastSwapAnimationTicks = 0;
	private int swapAnimationTicks = 0;
	private int newPage = -1;

	private int updateCounter;

	private boolean drawHoveringSlots = false;

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
		super(new ContainerRuneChainAltar(player.inventory, tile));
		this.container = (ContainerRuneChainAltar) this.inventorySlots;
		this.tile = tile;
		this.allowUserInput = false;
		this.xSize = 176;
		this.ySize = 224;
		this.player = player;
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		super.drawScreen(mouseX, mouseY, partialTicks);

		if(this.swapAnimationTicks > 0 && this.hoveredSlot != null && this.isSlabSlot(this.hoveredSlot)) {
			this.hoveredSlot = null;
		}

		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	public void drawSlot(Slot slot) {
		if(slot instanceof SlotRune) {
			SlotRune slotRune = (SlotRune) slot;

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

		if(this.container.getSelectedSlot() >= 0) {
			Slot selectedSlot = this.container.getSlot(this.container.getSelectedSlot());
			ItemStack selectedStack = selectedSlot.getStack();

			if(!selectedStack.isEmpty() && selectedSlot instanceof SlotRune) {
				this.drawSubMenu(-185, this.container.getSelectedSlot(), selectedStack);

				//TODO use correct slot & stack
				this.drawSubMenu(this.xSize + 19, this.container.getSelectedSlot(), selectedStack);
			}
		}
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

		for(int i = this.tile.getChainLength() + 1; i < this.tile.getMaxChainLength(); i++) {
			SlotRune slot = (SlotRune) this.inventorySlots.getSlot(i + TileEntityRuneChainAltar.NON_INPUT_SLOTS);

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

			SlotRune slot = (SlotRune) this.inventorySlots.getSlot(slotIndex);

			if(slot.getHasStack() && x >= this.guiLeft + slot.xPos - 4 && x <= this.guiLeft + slot.xPos + 16 + 3 && y >= this.guiTop + slot.yPos - 10 && y <= this.guiTop + slot.yPos + 16 + 10) {
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

		if(this.container.getSelectedSlot() >= 0) {
			Slot selectedSlot = this.container.getSlot(this.container.getSelectedSlot());
			ItemStack selectedStack = selectedSlot.getStack();

			if(!selectedStack.isEmpty() && selectedSlot instanceof SlotRune) {
				this.drawSubMenuBackground(this.guiLeft - 185, this.container.getSelectedSlot(), selectedStack);

				//TODO use correct slot & stack
				this.drawSubMenuBackground(this.guiLeft + this.xSize + 19, this.container.getSelectedSlot(), selectedStack);
			}
		}
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		if(this.swapAnimationTicks == 0) {
			if(mouseButton == 0) {
				{
					int relX = mouseX - (this.guiLeft + this.xSize + 2);
					int relY = mouseY - (this.guiTop + 20);
					if(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24) {
						this.movePage(-1);
						return;
					}
				}
				{
					int relX = mouseX - (this.guiLeft + this.xSize + 2);
					int relY = mouseY - (this.guiTop + 100);
					if(relX >= 0 && relX <= 15 && relY >= 0 && relY <= 24) {
						this.movePage(1);
						return;
					}
				}

				for(int i = 0; i < ContainerRuneChainAltar.SLOTS_PER_PAGE; i++) {
					int slotIndex = this.container.getCurrentPage().index * ContainerRuneChainAltar.SLOTS_PER_PAGE + TileEntityRuneChainAltar.NON_INPUT_SLOTS + i;

					SlotRune slot = (SlotRune) this.inventorySlots.getSlot(slotIndex);

					if(slot.getHasStack() && mouseX >= this.guiLeft + slot.xPos - 4 && mouseX <= this.guiLeft + slot.xPos + 16 + 3 && mouseY >= this.guiTop + slot.yPos - 10 && mouseY <= this.guiTop + slot.yPos + 16 + 10) {
						if(this.container.getShiftHoleSlot(slotIndex, false) >= 0) {
							int relX = mouseX - (this.guiLeft + slot.xPos + 2);
							int relY = mouseY - (this.guiTop + slot.yPos - 9);
							if(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7) {
								this.container.shift(slotIndex, false);
								TheBetweenlands.networkWrapper.sendToServer(new MessageShiftRuneChainAltarSlot(slotIndex, false));
								this.mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.RUNE_SLOT_SHIFT, rand.nextFloat() * 0.066F + 0.933F, 1.0F));
								return;
							}
						}

						if(this.container.getShiftHoleSlot(slotIndex, true) >= 0) {
							int relX = mouseX - (this.guiLeft + slot.xPos + 2);
							int relY = mouseY - (this.guiTop + slot.yPos + 9 + 9);
							if(relX >= 0 && relX <= 13 && relY >= 0 && relY <= 7) {
								this.container.shift(slotIndex, true);
								TheBetweenlands.networkWrapper.sendToServer(new MessageShiftRuneChainAltarSlot(slotIndex, true));
								this.mc.getSoundHandler().playSound(PositionedSoundRecord.getRecord(SoundRegistry.RUNE_SLOT_SHIFT, rand.nextFloat() * 0.066F + 0.933F, 1.0F));
								return;
							}
						}
					}
				}
			}

			super.mouseClicked(mouseX, mouseY, mouseButton);
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
	}

	protected void updateSlotHoverTicks() {
		for(Slot slot : this.container.inventorySlots) {
			if(slot instanceof SlotRune) {
				SlotRune slotRune = (SlotRune) slot;

				slotRune.prevHoverTicks = slotRune.hoverTicks;

				if(slotRune.getSlotIndex() == this.container.getSelectedSlot()) {
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

	//TODO
	protected void drawSubMenuBackground(int x, int runeSlot, ItemStack stack) {
		int width = 166;
		int height = 216;

		int y = this.guiTop + 70 - height / 2;

		//Top left corner
		this.drawTexturedModalRect512(x, y, 212, 94, 3, 3);
		//Top bar
		this.drawTexturedModalRect512(x + 3, y, 215, 94, width - 6, 3);
		//Top right corner
		this.drawTexturedModalRect512(x + 3 + width - 6, y, 383, 94, 3, 3);
		//Right bar
		this.drawTexturedModalRect512(x + 3 + width - 6, y + 3, 383, 97, 3, height - 6);
		//Bottom right corner
		this.drawTexturedModalRect512(x + 3 + width - 6, y + 3 + height - 6, 383, 313, 3, 3);
		//Bottom bar
		this.drawTexturedModalRect512(x + 3, y + 3 + height - 6, 215, 313, width - 6, 3);
		//Bottom left corner
		this.drawTexturedModalRect512(x, y + 3 + height - 6, 212, 313, 3, 3);
		//Left bar
		this.drawTexturedModalRect512(x, y + 3, 212, 97, 3, height - 6);

		//Background
		this.drawTexturedModalRect512(x + 3, y + 3, 215, 97, width - 6, height - 6);

		ColoredItemRenderer.renderItemAndEffectIntoGUI(this.itemRender, this.mc.player, stack, x + 4, y + 4, 1, 1, 1, 1);

		this.fontRenderer.drawString(TextFormatting.UNDERLINE + stack.getDisplayName(), x + 24, y + 8, 0xFF404040);

		INodeBlueprint<?, RuneExecutionContext> bp = ((ItemRune)stack.getItem()).getRuneBlueprint(stack);

		this.fontRenderer.drawString("Configs: " + bp.getConfigurations().size(), x + 4, y + 22, 0xFF404040);
		int i = 1;
		for(INodeConfiguration config : bp.getConfigurations()) {
			this.fontRenderer.drawString(" " + config.getId() + ") Marks: " + config.getInputs().size() + "/" + config.getOutputs().size(), x + 4, y + 22 + i * 10, 0xFF404040);
			i++;
		}

		GlStateManager.color(1, 1, 1, 1);
		this.mc.getTextureManager().bindTexture(GUI_RUNE_CHAIN_ALTAR);
	}

	protected void drawSubMenu(int x, int runeSlot, ItemStack stack) {
		int width = 166;
		int height = 216;

		int y = 70 - height / 2;

		GlStateManager.color(1, 1, 1, 1);

		if(x < 0) {
			int mx = Mouse.getX()* this.width / this.mc.displayWidth;
			int my = this.height - Mouse.getY() * this.height / this.mc.displayHeight - 1;

			this.zLevel = 280.0F;
			this.drawHangingRope(x + width - 10, y + height - 10, mx - 10 - this.guiLeft, my - 10 - this.guiTop);
			this.zLevel = 0.0F;
		}

		this.mc.getTextureManager().bindTexture(GUI_RUNE_CHAIN_ALTAR);
	}

	protected void drawHangingRope(float sx, float sy, float ex, float ey) {
		this.mc.getTextureManager().bindTexture(GUI_RUNE_CHAIN_ALTAR_ROPE);

		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder buffer = tessellator.getBuffer();

		float x1 = sx;
		float y1 = sy;

		float x3 = ex;
		float y3 = ey;

		if(x1 - x3 >= 0.0F && x1 - x3 < 1.0F) {
			x3 = x1 + 1;
		} else if (x1 - x3 < 0.0F && x1 - x3 > -1.0F) {
			x3 = x1 - 1;
		}

		float x2 = (x1 + x3) / 2.0F;
		float y2 = Math.max(y1, y3) + 15.0F + (float)Math.sin((this.updateCounter + this.mc.getRenderPartialTicks()) / 15.0F) * 2.5f;

		//Fit parabola
		float a1 = -x1*x1 + x2*x2;
		float b1 = -x1 + x2;
		float d1 = -y1 + y2;
		float a2 = -x2*x2 + x3*x3;
		float b2 = -x2 + x3;
		float d2 = -y2 + y3;
		float b3 = -b2 / b1;
		float a3 = b3 * a1 + a2;
		float d = b3 * d1 + d2;
		float a = d / a3;
		float b = (d1 - a1 * a) / b1;
		float c = y1 - a * x1*x1 - b * x1;

		float px = x1;
		float py = y1;

		float width = 2F;

		float pxc1 = x1 - width;
		float pyc1 = y1;
		float pxc2 = x1 + width;
		float pyc2 = y1;

		boolean isTowardsRight = x1 < x3;

		float ropeV1 = isTowardsRight ? 0 : 0.5F;
		float ropeV2 = isTowardsRight ? 0.5F : 0;

		float endV1 = isTowardsRight ? 0.5F : 1.0F;
		float endV2 = isTowardsRight ? 1.0F : 0.5F;

		float endU11 = isTowardsRight ? 0 : 1.0F;
		float endU12 = isTowardsRight ? 0.5F : 0.5F;

		float endU21 = isTowardsRight ? 0.5F : 0.5F;
		float endU22 = isTowardsRight ? 1.0F : 0;

		float u = 0;

		buffer.begin(GL11.GL_TRIANGLE_STRIP, DefaultVertexFormats.POSITION_TEX);

		int pieces = 32;

		for(int i = -1; i <= pieces; i++) {
			float x = x1 + (x3 - x1) / (float) (pieces - 1) * i;
			float y = a * x*x + b * x + c;

			float sideX = y - py;
			float sideY = -(x - px);
			float sideDirLength = (float) Math.sqrt(sideX*sideX + sideY*sideY);
			sideX *= width / sideDirLength;
			sideY *= width / sideDirLength;

			float xc1 = x - sideX;
			float yc1 = y - sideY;
			float xc2 = x + sideX;
			float yc2 = y + sideY;

			if(i == 1) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				buffer.pos(pxc2 - offX, pyc2 - offY, this.zLevel).tex(endU11, endV1).endVertex();
				buffer.pos(pxc1 - offX, pyc1 - offY, this.zLevel).tex(endU11, endV2).endVertex();
				buffer.pos(pxc2, pyc2, this.zLevel).tex(endU12, endV1).endVertex();
				buffer.pos(pxc1, pyc1, this.zLevel).tex(endU12, endV2).endVertex();
			}

			if(i > 0) {
				buffer.pos(pxc2, pyc2, this.zLevel).tex(u, ropeV1).endVertex();
				buffer.pos(pxc1, pyc1, this.zLevel).tex(u, ropeV2).endVertex();

				u += (float) Math.sqrt((x-px)*(x-px) + (y-py)*(y-py)) / 16.0F;
			}

			if(i == pieces) {
				float offX = -sideY / width * 8.0F;
				float offY = sideX / width * 8.0F;

				buffer.pos(pxc2, pyc2, this.zLevel).tex(endU21, endV1).endVertex();
				buffer.pos(pxc1, pyc1, this.zLevel).tex(endU21, endV2).endVertex();
				buffer.pos(pxc2 + offX, pyc2 + offY, this.zLevel).tex(endU22, endV1).endVertex();
				buffer.pos(pxc1 + offX, pyc1 + offY, this.zLevel).tex(endU22, endV2).endVertex();
			}

			px = x;
			py = y;

			pxc1 = xc1;
			pyc1 = yc1;
			pxc2 = xc2;
			pyc2 = yc2;
		}

		tessellator.draw();
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
}

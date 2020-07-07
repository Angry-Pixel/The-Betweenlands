package thebetweenlands.client.gui.inventory.runeweavingtable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import thebetweenlands.api.rune.IGuiRuneToken;
import thebetweenlands.api.rune.INodeConfiguration;
import thebetweenlands.api.rune.IRuneContainer;
import thebetweenlands.api.rune.IRuneContainerContext;
import thebetweenlands.api.rune.IRuneGui;
import thebetweenlands.api.rune.RuneMenuDrawingContext;
import thebetweenlands.api.rune.RuneMenuType;
import thebetweenlands.api.rune.impl.RuneTokenDescriptors;
import thebetweenlands.client.handler.ItemTooltipHandler;
import thebetweenlands.common.TheBetweenlands;
import thebetweenlands.common.herblore.book.widgets.text.FormatTags;
import thebetweenlands.common.herblore.book.widgets.text.TextContainer;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.network.serverbound.MessageSetRuneWeavingTableConfiguration;
import thebetweenlands.util.ColoredItemRenderer;

public class RuneBranchingGui extends DefaultRuneGui {

	public RuneBranchingGui(RuneMenuType menu) {
		super(menu);
	}
	
	//TODO Add a way to link any number of marks or add configurations for different numbers of marks (and remove this gui)
}

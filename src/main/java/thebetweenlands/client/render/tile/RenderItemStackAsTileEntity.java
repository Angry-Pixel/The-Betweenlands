package thebetweenlands.client.render.tile;

import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;

import com.google.common.base.Preconditions;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.renderer.tileentity.TileEntityItemStackRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public class ItemRendererStackAsTileEntity extends TileEntityItemStackRenderer {
	private TileEntitySpecialRenderer<?> renderer;
	private Int2ObjectMap<TileEntitySpecialRenderer<?>> map;

	public ItemRendererStackAsTileEntity(Class<? extends TileEntity> te) {
		this.renderer = TileEntityRendererDispatcher.instance.getRenderer(te);
		this.map = null;
		Preconditions.checkNotNull(this.renderer, "TileEntity %s for ItemStack renderer does not have a renderer!", te.getName());
	}

	public ItemRendererStackAsTileEntity(Map<Integer, Class<? extends TileEntity>> map) {
		this.renderer = null;
		for(Entry<Integer, Class<? extends TileEntity>> entry : map.entrySet()) {
			this.add(entry.getKey(), entry.getValue());
		}
	}

	public ItemRendererStackAsTileEntity() {
		this.renderer = null;
		this.map = null;
	}

	public ItemRendererStackAsTileEntity(Consumer<ItemRendererStackAsTileEntity> constructor) {
		constructor.accept(this);
	}

	public ItemRendererStackAsTileEntity add(int meta, Class<? extends TileEntity> te) {
		if(this.map == null) {
			this.map = new Int2ObjectOpenHashMap<>();
		}
		TileEntitySpecialRenderer<?> renderer = TileEntityRendererDispatcher.instance.getRenderer(te);
		Preconditions.checkNotNull(renderer, "TileEntity %s for ItemStack renderer does not have a renderer!", te.getName());
		this.map.put(meta, renderer);
		return this;
	}

	@Override
	public void renderByItem(ItemStack stack, float partialTicks) {
		if(this.renderer != null) {
			this.renderer.render(null, 0, 0, 0, partialTicks, 0, 1);
		} else if(this.map != null) {
			TileEntitySpecialRenderer<?> renderer = this.map.get(stack.getMetadata());
			if(renderer != null) {
				renderer.render(null, 0, 0, 0, partialTicks, 0, 1);
			}
		}
	}
}

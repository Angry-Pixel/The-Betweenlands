package thebetweenlands.common.proxy;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return null;
	}

	public void preInit() {

	}

	public void init() {

	}

	public void postInit() {

	}

	public void registerEventHandlers() {

	}

	public EntityPlayer getClientPlayer() {
		return null;
	}

	public World getClientWorld() {
		return null;
	}
	
	public void registerDefaultBlockItemRenderer(Block block) {

	}

	public void registerDefaultItemRenderer(Item item) {

	}

	public void changeFileNames() {

	}

	public void registerItemAndBlockRenderers() {
		// TODO Auto-generated method stub
		
	}

	public void setCustomStateMap(Block block, StateMap stateMap) {
		// TODO Auto-generated method stub
		
	}
}

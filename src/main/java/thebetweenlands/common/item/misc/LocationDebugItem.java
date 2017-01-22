package thebetweenlands.common.item.misc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.SharedRegion;
import thebetweenlands.common.world.storage.world.shared.location.EnumLocationType;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

//MINE!!
public class LocationDebugItem extends Item {
	public LocationDebugItem() {
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
			if(playerIn.isSneaking()) {
				List<LocationStorage> locations = worldStorage.getSharedStorageAt(LocationStorage.class, location -> location.isInside(pos), pos.getX(), pos.getZ());
				for(LocationStorage location : locations) {
					worldStorage.removeSharedStorage(location);
				}
				playerIn.addChatMessage(new TextComponentString(String.format("Removed %s locations:",  locations.size())));
				for(LocationStorage location : locations) {
					playerIn.addChatMessage(new TextComponentString("  " + location.getName()));
				}
			} else {
				List<LocationStorage> locations = worldStorage.getSharedStorageAt(LocationStorage.class, location -> location.isInside(pos), pos.getX(), pos.getZ());
				if(locations.isEmpty()) {
					int rndID = world.rand.nextInt();
					LocationStorage location = new LocationStorage(worldStorage, UUID.randomUUID().toString(), SharedRegion.getFromBlockPos(pos), "Test Location ID: " + rndID, EnumLocationType.NONE);
					location.addBounds(new AxisAlignedBB(pos).expand(16, 16, 16));
					location.setSeed(world.rand.nextLong());
					location.linkChunks();
					location.setDirty(true);
					worldStorage.addSharedStorage(location);
					playerIn.addChatMessage(new TextComponentString(String.format("Added new location: %s", location.getName())));
				} else {
					List<EntityPlayerMP> watchers = new ArrayList<EntityPlayerMP>();
					for(LocationStorage location : locations) {
						location.setDirty(true, true);
						for(EntityPlayerMP watcher : location.getWatchers()) {
							if(!watchers.contains(watcher)) {
								watchers.add(watcher);
							}
						}
					}
					playerIn.addChatMessage(new TextComponentString(String.format("Marked %s locations as dirty and queued update packets to %s watchers:", locations.size(), watchers.size())));
					playerIn.addChatMessage(new TextComponentString("  Locations:"));
					for(LocationStorage location : locations) {
						playerIn.addChatMessage(new TextComponentString("    " + location.getName()));
						playerIn.addChatMessage(new TextComponentString(String.format("      Guarded at %s: %s", "X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ(), (location.getGuard() == null ? String.valueOf(false) : location.getGuard().isGuarded(world, playerIn, pos)))));
						playerIn.addChatMessage(new TextComponentString("      Watchers:"));
						for(EntityPlayerMP watcher : location.getWatchers()) {
							playerIn.addChatMessage(new TextComponentString("        " + watcher.getName()));
						}
					}
				}
			}
		}

		return EnumActionResult.SUCCESS;
	}
}

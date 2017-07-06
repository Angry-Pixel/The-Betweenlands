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
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import thebetweenlands.common.world.storage.world.global.BetweenlandsWorldData;
import thebetweenlands.common.world.storage.world.shared.SharedRegion;
import thebetweenlands.common.world.storage.world.shared.location.EnumLocationType;
import thebetweenlands.common.world.storage.world.shared.location.LocationStorage;

public class LocationDebugItem extends Item {
	public LocationDebugItem() {
		this.setMaxStackSize(1);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer playerIn, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			BetweenlandsWorldData worldStorage = BetweenlandsWorldData.forWorld(world);
			if(playerIn.isSneaking()) {
				List<LocationStorage> locations = worldStorage.getSharedStorageAt(LocationStorage.class, location -> location.isInside(pos), pos.getX(), pos.getZ());
				if(locations.isEmpty()) {
					int rndID = world.rand.nextInt();
					LocationStorage location = new LocationStorage(worldStorage, UUID.randomUUID().toString(), SharedRegion.getFromBlockPos(pos), "Test Location ID: " + rndID, EnumLocationType.NONE);
					location.addBounds(new AxisAlignedBB(pos).expand(16, 16, 16));
					location.setSeed(world.rand.nextLong());
					location.linkChunks();
					location.setDirty(true);
					worldStorage.addSharedStorage(location);
					playerIn.sendMessage(new TextComponentString(String.format("Added new location: %s", location.getName())));
				} else {
					for(LocationStorage location : locations) {
						worldStorage.removeSharedStorage(location);
					}
					playerIn.sendMessage(new TextComponentString(String.format("Removed %s locations:",  locations.size())));
					for(LocationStorage location : locations) {
						playerIn.sendMessage(new TextComponentString("  " + location.getName()));
					}
				}
			} else {
				List<LocationStorage> locations = worldStorage.getSharedStorageAt(LocationStorage.class, location -> location.isInside(pos), pos.getX(), pos.getZ());
				List<EntityPlayerMP> watchers = new ArrayList<EntityPlayerMP>();
				boolean guard = false;
				for(LocationStorage location : locations) {
					if(hand == EnumHand.OFF_HAND && location.getGuard() != null) {
						boolean guarded = location.getGuard().isGuarded(world, playerIn, pos);
						location.getGuard().setGuarded(world, pos, !guarded);
						playerIn.sendMessage(new TextComponentString(String.format("Set block guard to %s at %s for location %s", !guarded, "X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ(), location.getName())));
						location.setDirty(true, true);
						guard = true;
					}
				}
				if(!guard) {
					for(LocationStorage location : locations) {
						location.unlinkAllChunks();
						location.linkChunks();
						location.setDirty(true, true);
						for(EntityPlayerMP watcher : location.getWatchers()) {
							if(!watchers.contains(watcher)) {
								watchers.add(watcher);
							}
						}
					}
					playerIn.sendMessage(new TextComponentString(String.format("Marked %s locations as dirty and queued update packets to %s watchers:", locations.size(), watchers.size())));
					playerIn.sendMessage(new TextComponentString("  Locations:"));
					for(LocationStorage location : locations) {
						playerIn.sendMessage(new TextComponentString("    " + location.getName()));
						playerIn.sendMessage(new TextComponentTranslation("      Guarded at %s, %s: %s", new TextComponentTranslation(world.getBlockState(pos).getBlock().getUnlocalizedName() + ".name"), "X=" + pos.getX() + " Y=" + pos.getY() + " Z=" + pos.getZ(), (location.getGuard() == null ? String.valueOf(false) : location.getGuard().isGuarded(world, playerIn, pos))));
						playerIn.sendMessage(new TextComponentString("      Watchers:"));
						for(EntityPlayerMP watcher : location.getWatchers()) {
							playerIn.sendMessage(new TextComponentString("        " + watcher.getName()));
						}
					}
				}
			}
		}

		return EnumActionResult.SUCCESS;
	}
}

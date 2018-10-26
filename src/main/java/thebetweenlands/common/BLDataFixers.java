package thebetweenlands.common;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;
import thebetweenlands.common.lib.ModInfo;

public class BLDataFixers {
	private BLDataFixers() { }

	public static void register() {
		ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(ModInfo.ID, 1);

		fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileEntityNameFixer());
	}

	private static class TileEntityNameFixer implements IFixableData {
		//Don't add any new tile entities to this!
		private static final String[] NAMES = new String[] {
				"druid_altar",
				"purifier",
				"weedwood_workbench",
				"compost_bin",
				"loot_pot",
				"mob_spawner",
				"wisp",
				"sulfur_furnace",
				"sulfur_furnace_dual",
				"betweenlands_chest",
				"rubber_tap",
				"spike_trap",
				"possessed_block",
				"item_cage",
				"weedwood_sign",
				"mud_flower_pot",
				"gecko_cage",
				"infuser",
				"mortar",
				"animator",
				"alembic",
				"dug_soil",
				"item_shelf",
				"tar_beast_spawner",
				"tar_loot_pot_1",
				"tar_loot_pot_2",
				"tar_loot_pot_3",
				"syrmorite_hopper",
				"moss_bed",
				"aspect_vial",
				"aspectrus_crop",
				"repeller",
				"present"
		};

		private static final Map<String, String> MAP;

		static {
			ImmutableMap.Builder<String, String> names = ImmutableMap.builder();

			for(String name : NAMES) {
				names.put("minecraft:tile.thebetweenlands." + name, ModInfo.ID + ":" + name);
			}

			MAP = names.build();
		}

		@Override
		public int getFixVersion() {
			return 1;
		}

		@Override
		public NBTTagCompound fixTagCompound(NBTTagCompound nbt) {
			String id = nbt.getString("id");
			if(MAP.containsKey(id)) {
				nbt.setString("id", MAP.get(id));
			}
			return nbt;
		}
	}
}
package thebetweenlands.common;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.util.datafix.IFixableData;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.RegistryEvent.MissingMappings.Mapping;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import thebetweenlands.common.entity.mobs.EntityFirefly;
import thebetweenlands.common.entity.mobs.EntityGecko;
import thebetweenlands.common.lib.ModInfo;
import thebetweenlands.common.registries.ItemRegistry;

public class BLDataFixers {
	private BLDataFixers() { }

	public static void register() {
		ModFixs fixes = FMLCommonHandler.instance().getDataFixer().init(ModInfo.ID, 2);

		fixes.registerFix(FixTypes.BLOCK_ENTITY, new TileEntityNameFixer());
		fixes.registerFix(FixTypes.ITEM_INSTANCE, new ItemMobFixer());
	}

	@SubscribeEvent
	public static void onMissingMappings(RegistryEvent.MissingMappings<Item> event) {
		for(Mapping<Item> mapping : event.getMappings()) {
			if(new ResourceLocation("thebetweenlands:gecko").equals(mapping.key)) {
				mapping.remap(ItemRegistry.CRITTER);
			} else if(new ResourceLocation("thebetweenlands:firefly").equals(mapping.key)) {
				mapping.remap(ItemRegistry.CRITTER);
			}
		}
	}
	
	private static class ItemMobFixer implements IFixableData {
		@Override
		public int getFixVersion() {
			return 2;
		}

		@Override
		public NBTTagCompound fixTagCompound(NBTTagCompound nbt) {
			if(nbt.hasKey("id", Constants.NBT.TAG_STRING)) {
				String id = nbt.getString("id");

				if("thebetweenlands:gecko".equals(id)) {
					return ItemRegistry.CRITTER.capture(EntityGecko.class).writeToNBT(new NBTTagCompound());
				} else if("thebetweenlands:firefly".equals(id)) {
					return ItemRegistry.CRITTER.capture(EntityFirefly.class).writeToNBT(new NBTTagCompound());
				}
			}

			//sludge worm egg sac can be left as is because it still has the same reg name

			return nbt;
		}
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
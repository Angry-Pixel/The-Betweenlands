package thebetweenlands.recipes;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class BLMaterial extends Material {
	public BLMaterial(MapColor mapColor) {
		super(mapColor);
	}

	public static final ToolMaterial toolWeedWood = EnumHelper.addToolMaterial("WEEDWOOD", 0, 80, 2.0F, 0.0F, 15);
	public static final ToolMaterial toolBetweenstone = EnumHelper.addToolMaterial("BETWEENSTONE", 1, 320, 4.0F, 1.0F, 5);
	public static final ToolMaterial toolOctine = EnumHelper.addToolMaterial("OCTINE", 2, 900, 6.0F, 2.0F, 14);
	public static final ToolMaterial toolValonite = EnumHelper.addToolMaterial("VALONITE", 3, 2500, 8.0F, 3.0F, 10);
	public static final ToolMaterial toolLoot = EnumHelper.addToolMaterial("LOOT", 2, 250, 40.0F, 0.5F, 5);
	public static final ToolMaterial toolOfLegends = EnumHelper.addToolMaterial("LEGEND", 6, 10000, 16.0F, 6.0F, 20);

	public static final ArmorMaterial armorLurkerSkin = EnumHelper.addArmorMaterial("LURKERSKIN", 12, new int[]{1, 3, 2, 1}, 0);
	public static final ArmorMaterial armorBone = EnumHelper.addArmorMaterial("SLIMYBONE", 6, new int[] {2, 5, 3, 1}, 0);
	public static final ArmorMaterial armorOctine = EnumHelper.addArmorMaterial("OCTINE", 16, new int[] {2, 6, 5, 2}, 0);
	public static final ArmorMaterial armorValonite = EnumHelper.addArmorMaterial("VALONITE", 35, new int[] {3, 8, 6, 3}, 0);
	public static final ArmorMaterial armorRubber = EnumHelper.addArmorMaterial("RUBBER", 10, new int[] {0, 0, 0, 1}, 0);
	public static final ArmorMaterial armorOfLegends = EnumHelper.addArmorMaterial("LEGEND", 66, new int[] {6, 16, 12, 6}, 0);

	public static final Material tar = new MaterialLiquid(MapColor.mapColorArray[0]);
	public static final Material mud = new Material(MapColor.dirtColor) {
		@Override
		public boolean isOpaque() {
			return false;
		}
	};
	public static final Material sludge = new BLMaterial(MapColor.dirtColor).setRequiresTool();
}
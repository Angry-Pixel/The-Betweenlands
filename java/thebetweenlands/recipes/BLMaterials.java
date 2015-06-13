package thebetweenlands.recipes;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class BLMaterials {

	public static ToolMaterial toolWeedWood = EnumHelper.addToolMaterial("WEEDWOOD", 0, 59, 2.0F, 0.0F, 15);
	public static ToolMaterial toolBetweenstone = EnumHelper.addToolMaterial("BETWEENSTONE", 1, 131, 4.0F, 1.0F, 5);
	public static ToolMaterial toolOctine = EnumHelper.addToolMaterial("OCTINE", 2, 250, 6.0F, 2.0F, 14);
	public static ToolMaterial toolValonite = EnumHelper.addToolMaterial("VALONITE", 3, 1561, 8.0F, 3.0F, 10);
	
	public static ArmorMaterial armorLurkerSkin = EnumHelper.addArmorMaterial("LURKERSKIN", 5, new int[] {1, 3, 2, 1}, 0);
	public static ArmorMaterial armorBone = EnumHelper.addArmorMaterial("SLIMYBONE", 5, new int[] {2, 5, 3, 1}, 0);
	public static ArmorMaterial armorOctine = EnumHelper.addArmorMaterial("OCTINE", 15, new int[] {2, 6, 5, 2}, 0);
	public static ArmorMaterial armorValonite = EnumHelper.addArmorMaterial("VALONITE", 33, new int[] {3, 8, 6, 3}, 0);
	public static ArmorMaterial armorRubber = EnumHelper.addArmorMaterial("RUBBER", 8, new int[] {0, 0, 0, 1}, 0);
	
	public static Material tar = new MaterialLiquid(MapColor.mapColorArray[0]);
}
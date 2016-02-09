package thebetweenlands.recipes;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraftforge.common.util.EnumHelper;

public class BLMaterial extends Material {
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
	public static final Material stagnantWater = new MaterialLiquid(MapColor.mapColorArray[0]);
	public static final Material mud = new Material(MapColor.dirtColor) {
		@Override
		public boolean isOpaque() {
			return false;
		}
	};
	public static final Material sludge = new BLMaterial(MapColor.dirtColor).setRequiresTool();
	public static final Material wisp = new BLMaterial(MapColor.airColor){
		@Override
		public boolean isSolid() {
			return false;
		}

		@Override
		public boolean getCanBlockGrass() {
			return false;
		}

		@Override
		public boolean blocksMovement() {
			return false;
		}
	}.setReplaceable().setTranslucent().setNoPushMobility();

	private boolean isTranslucent;
	private boolean requiresNoTool;
	private boolean canBurn;
	private boolean replaceable;
	private int mobilityFlag;
	private boolean isAdventureModeExempt;

	public BLMaterial(MapColor mapColor) {
		super(mapColor);
	}

	/**
	 * Marks the material as translucent
	 */
	private BLMaterial setTranslucent()
	{
		this.isTranslucent = true;
		return this;
	}

	/**
	 * Makes blocks with this material require the correct tool to be harvested.
	 */
	@Override
	protected BLMaterial setRequiresTool()
	{
		this.requiresNoTool = false;
		return this;
	}

	/**
	 * Set the canBurn bool to True and return the current object.
	 */
	@Override
	protected BLMaterial setBurning()
	{
		this.canBurn = true;
		return this;
	}

	/**
	 * Returns if the block can burn or not.
	 */
	@Override
	public boolean getCanBurn()
	{
		return this.canBurn;
	}

	/**
	 * Sets {@link #replaceable} to true.
	 */
	@Override
	public BLMaterial setReplaceable()
	{
		this.replaceable = true;
		return this;
	}

	/**
	 * Returns whether the material can be replaced by other blocks when placed - eg snow, vines and tall grass.
	 */
	@Override
	public boolean isReplaceable()
	{
		return this.replaceable;
	}

	/**
	 * Indicate if the material is opaque
	 */
	@Override
	public boolean isOpaque()
	{
		return this.isTranslucent ? false : this.blocksMovement();
	}

	/**
	 * Returns true if the material can be harvested without a tool (or with the wrong tool)
	 */
	@Override
	public boolean isToolNotRequired()
	{
		return this.requiresNoTool;
	}

	/**
	 * Returns the mobility information of the material, 0 = free, 1 = can't push but can move over, 2 = total
	 * immobility and stop pistons.
	 */
	@Override
	public int getMaterialMobility()
	{
		return this.mobilityFlag;
	}

	/**
	 * This type of material can't be pushed, but pistons can move over it.
	 */
	@Override
	protected BLMaterial setNoPushMobility()
	{
		this.mobilityFlag = 1;
		return this;
	}

	/**
	 * This type of material can't be pushed, and pistons are blocked to move.
	 */
	@Override
	protected BLMaterial setImmovableMobility()
	{
		this.mobilityFlag = 2;
		return this;
	}

	/**
	 * @see #isAdventureModeExempt()
	 */
	@Override
	protected BLMaterial setAdventureModeExempt()
	{
		this.isAdventureModeExempt = true;
		return this;
	}

	/**
	 * Returns true if blocks with this material can always be mined in adventure mode.
	 */
	@Override
	public boolean isAdventureModeExempt()
	{
		return this.isAdventureModeExempt;
	}
}
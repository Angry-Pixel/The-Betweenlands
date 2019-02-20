package thebetweenlands.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;

public class BLBlockMaterial {
	//TODO 1.13 Materials are missing some currently private/protected properties
	
	//notOpaque
	public static final Material MUD = new Material.Builder(MaterialColor.DIRT).build();
	//blocksLight, blocksMovement, translucent, pushDestroys
	public static final Material WISP = new Material.Builder(MaterialColor.AIR).notSolid().replaceable().build();
	public static final Material TAR = new Material.Builder(MaterialColor.BLACK).liquid().build();
	public static final Material RUBBER = new Material.Builder(MaterialColor.WATER).liquid().build();
	//requiresTool
	public static final Material SLUDGE = new Material.Builder(MaterialColor.DIRT).build();
}

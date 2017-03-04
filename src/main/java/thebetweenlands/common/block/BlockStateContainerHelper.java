package thebetweenlands.common.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockStateContainerHelper {
	/**
	 * Combines the new properties with the properties of the original block state
	 * @param original
	 * @param newProperties
	 * @param newUnlistedProperties
	 * @return
	 */
	public static ExtendedBlockState extendBlockstateContainer(ExtendedBlockState original, IProperty<?>[] newProperties, IUnlistedProperty<?>[] newUnlistedProperties) {
		Collection<IProperty<?>> properties = new ArrayList<IProperty<?>>();
		properties.addAll(original.getProperties());
		properties.addAll(Arrays.asList(newProperties));
		Collection<IUnlistedProperty<?>> unlistedProperties = new ArrayList<IUnlistedProperty<?>>();
		unlistedProperties.addAll(original.getUnlistedProperties());
		unlistedProperties.addAll(Arrays.asList(newUnlistedProperties));
		return new ExtendedBlockState(original.getBlock(), properties.toArray(new IProperty[0]), unlistedProperties.toArray(new IUnlistedProperty[0]));
	}
	
	/**
	 * Combines the new properties with the properties of the original block state
	 * @param original
	 * @param newProperties
	 * @param newUnlistedProperties
	 * @return
	 */
	public static ExtendedBlockState extendBlockstateContainer(BlockStateContainer original, IProperty<?>[] newProperties, IUnlistedProperty<?>[] newUnlistedProperties) {
		Collection<IProperty<?>> properties = new ArrayList<IProperty<?>>();
		properties.addAll(original.getProperties());
		properties.addAll(Arrays.asList(newProperties));
		Collection<IUnlistedProperty<?>> unlistedProperties = new ArrayList<IUnlistedProperty<?>>();
		unlistedProperties.addAll(Arrays.asList(newUnlistedProperties));
		return new ExtendedBlockState(original.getBlock(), properties.toArray(new IProperty[0]), unlistedProperties.toArray(new IUnlistedProperty[0]));
	}
	
	/**
	 * Combines the new properties with the properties of the original block state
	 * @param original
	 * @param newProperties
	 * @return
	 */
	public static BlockStateContainer extendBlockstateContainer(BlockStateContainer original, IProperty<?>... newProperties) {
		Collection<IProperty<?>> properties = new ArrayList<IProperty<?>>();
		properties.addAll(original.getProperties());
		properties.addAll(Arrays.asList(newProperties));
		return new BlockStateContainer(original.getBlock(), properties.toArray(new IProperty[0]));
	}}

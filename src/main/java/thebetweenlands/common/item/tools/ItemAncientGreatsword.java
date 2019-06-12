package thebetweenlands.common.item.tools;

import com.google.common.collect.Multimap;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import thebetweenlands.api.item.IExtendedReach;
import thebetweenlands.client.tab.BLCreativeTabs;
import thebetweenlands.common.item.BLMaterialRegistry;

public class ItemAncientGreatsword extends ItemBLSword implements IExtendedReach {

    public ItemAncientGreatsword() {
        super(BLMaterialRegistry.TOOL_OCTINE);
        setCreativeTab(BLCreativeTabs.GEARS);
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        target.knockBack(attacker, 0.8F, (double) MathHelper.sin(attacker.rotationYaw * 0.017453292F), (double)(-MathHelper.cos(attacker.rotationYaw * 0.017453292F)));
        return super.hitEntity(stack, target, attacker);
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        Multimap<String, AttributeModifier> modifiers = super.getAttributeModifiers(slot, stack);
        if (slot == EntityEquipmentSlot.MAINHAND) {
            modifiers.removeAll(SharedMonsterAttributes.ATTACK_SPEED.getName());
            modifiers.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", -3.0D, 0));
        }
        return modifiers;
    }

    @Override
    public int getMinRepairFuelCost(ItemStack stack) {
        return BLMaterialRegistry.getMinRepairFuelCost(BLMaterialRegistry.TOOL_LEGEND);
    }

    @Override
    public int getFullRepairFuelCost(ItemStack stack) {
        return BLMaterialRegistry.getFullRepairFuelCost(BLMaterialRegistry.TOOL_LEGEND);
    }

    @Override
    public int getMinRepairLifeCost(ItemStack stack) {
        return BLMaterialRegistry.getMinRepairLifeCost(BLMaterialRegistry.TOOL_LEGEND);
    }

    @Override
    public int getFullRepairLifeCost(ItemStack stack) {
        return BLMaterialRegistry.getFullRepairLifeCost(BLMaterialRegistry.TOOL_LEGEND);
    }

    @Override
    public double getReach() {
        return 5.5;
    }
}

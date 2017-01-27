package thebetweenlands.common.item.tools;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import thebetweenlands.client.tab.BLCreativeTabs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemLootSword extends ItemBLSword {
    private List<Class<? extends EntityLivingBase>> instantKills = new ArrayList<>();

    public ItemLootSword(ToolMaterial material) {
        super(material);
        setCreativeTab(BLCreativeTabs.SPECIALS);
    }

    @SafeVarargs
	public final ItemLootSword addInstantKills(Class<? extends EntityLivingBase>... instantKills) {
        this.instantKills.addAll(Arrays.asList(instantKills));
        return this;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase attacked, EntityLivingBase attacker) {
        if (!this.instantKills.isEmpty() && this.instantKills.contains(attacked.getClass())) {
            attacked.attackEntityFrom(DamageSource.magic, attacked.getMaxHealth());
        }
        return super.hitEntity(stack, attacked, attacker);
    }
}

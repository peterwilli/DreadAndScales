package io.github.ultrusbot.moborigins;

import io.github.ultrusbot.moborigins.enchantment.HeatProtectionEnchantment;
import io.github.ultrusbot.moborigins.entity.EntityRegistry;
import io.github.ultrusbot.moborigins.power.MobOriginPowerFactories;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DreadAndScaledOriginsMod implements ModInitializer {
    @SuppressWarnings("SpellCheckingInspection")
    public static final String MOD_ID = "dreadandscalesorigins";
    public static Enchantment HEAT_PROTECTION = new HeatProtectionEnchantment();
    @Override
    public void onInitialize() {
        CustomPlayerConditions.register();
        CustomEntityActions.register();
        MobOriginPowerFactories.register();
        EntityRegistry.register();
        FabricDefaultAttributeRegistry.register(EntityRegistry.ORIGIN_SLIME, HostileEntity.createHostileAttributes());

        Registry.register(Registry.ENCHANTMENT, new Identifier(MOD_ID, "heat_protection"), HEAT_PROTECTION);
    }
}

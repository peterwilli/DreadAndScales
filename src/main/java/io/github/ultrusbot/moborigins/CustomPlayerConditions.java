package io.github.ultrusbot.moborigins;

import io.github.apace100.origins.origin.Origin;
import io.github.apace100.origins.power.factory.condition.ConditionFactory;
import io.github.apace100.origins.registry.ModComponents;
import io.github.apace100.origins.registry.ModRegistries;
import io.github.apace100.origins.util.Comparison;
import io.github.apace100.origins.util.SerializableData;
import io.github.apace100.origins.util.SerializableDataType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;

import java.util.NoSuchElementException;

public class CustomPlayerConditions {
    private static final String MOD_ID = DreadAndScaledOriginsMod.MOD_ID;
    public static void register() {
        register(new ConditionFactory<>(new Identifier(MOD_ID, "biome_temperature"), new SerializableData()
                .add("comparison", SerializableDataType.COMPARISON)
                .add("compare_to", SerializableDataType.FLOAT),
                (data, player) -> ((Comparison)data.get("comparison")).compare(player.world.getBiome(player.getBlockPos()).getTemperature(), data.getFloat("compare_to"))));

        register(new ConditionFactory<>(new Identifier(MOD_ID, "is_full_moon"), new SerializableData(), (data, player) -> player.world.getMoonSize() == 1.0));

        register(new ConditionFactory<>(new Identifier(MOD_ID, "time_since_rest"), new SerializableData()
                .add("comparison", SerializableDataType.COMPARISON)
                .add("compare_to", SerializableDataType.INT),
                (data, player) -> ((Comparison)data.get("comparison")).compare(MathHelper.clamp(((ServerPlayerEntity)player).getStatHandler().getStat(Stats.CUSTOM.getOrCreateStat(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE), data.getInt("compare_to"))));

        register(new ConditionFactory<>(new Identifier(MOD_ID, "nearby_entities"), new SerializableData()
                .add("entity_type", SerializableDataType.ENTITY_TYPE)
                .add("player_box_multiplier", SerializableDataType.FLOAT)
                .add("comparison", SerializableDataType.COMPARISON)
                .add("compare_to", SerializableDataType.INT),
                (data, player) -> {
                    EntityType<?> entityType = (EntityType<?>)data.get("entity_type");
                    Float playerBoxMultiplier = (Float)data.get("player_box_multiplier");
                    int amount = player.world.getOtherEntities(player, player.getBoundingBox().expand(playerBoxMultiplier), entity -> {
                        return entity.getType() == entityType;
                    }).size();
                    Comparison comparison = ((Comparison)data.get("comparison"));
                    int compareTo = data.getInt("compare_to");

                    return comparison.compare(amount, compareTo);
                }));

        register(new ConditionFactory<>(new Identifier(MOD_ID, "nearby_origins"), new SerializableData()
                .add("entity_type", SerializableDataType.ENTITY_TYPE)
                .add("player_box_multiplier", SerializableDataType.FLOAT)
                .add("origin_type", SerializableDataType.STRING)
                .add("comparison", SerializableDataType.COMPARISON)
                .add("compare_to", SerializableDataType.INT),
                (data, player) -> {
                    EntityType<?> entityType = (EntityType<?>)data.get("entity_type");
                    Float playerBoxMultiplier = (Float)data.get("player_box_multiplier");
                    String originType = (String)data.get("origin_type");
                    int amount = player.world.getOtherEntities(player, player.getBoundingBox().expand(playerBoxMultiplier), entity -> {
                        try {
                            boolean foundOriginType = false;
                            for(Origin o : ModComponents.ORIGIN.get(entity).getOrigins().values()) {
                                String originID = o.getIdentifier().toString();
                                if(originType.equals(originID)) {
                                    foundOriginType = true;
                                    break;
                                }
                                System.out.println("originID: " + originID + " foundOriginType: " + foundOriginType);
                            }
                            return entity.getType() == entityType && foundOriginType;
                        }
                        catch (NoSuchElementException e) {
                            return false;
                        }
                    }).size();
//                    System.out.println("nearby_entities Amount: " + amount);
                    Comparison comparison = ((Comparison)data.get("comparison"));
                    int compareTo = data.getInt("compare_to");

                    return comparison.compare(amount, compareTo);
                }));


    }


    private static void register(ConditionFactory<LivingEntity> conditionFactory) {
        Registry.register(ModRegistries.ENTITY_CONDITION, conditionFactory.getSerializerId(), conditionFactory);
    }
    
}

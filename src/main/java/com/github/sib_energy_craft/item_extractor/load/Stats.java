package com.github.sib_energy_craft.item_extractor.load;

import com.github.sib_energy_craft.energy_api.utils.Identifiers;
import com.github.sib_energy_craft.sec_utils.load.ModRegistrar;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.stat.StatFormatter;
import net.minecraft.util.Identifier;

import static net.minecraft.stat.Stats.CUSTOM;


/**
 * @since 0.0.1
 * @author sibmaks
 */
public final class Stats implements ModRegistrar {
    public static final Identifier INTERACT_ITEM_EXTRACTOR;

    static {
        INTERACT_ITEM_EXTRACTOR = register(Identifiers.asString("interact_with_item_extractor"), StatFormatter.DEFAULT);
    }

    private static Identifier register(String id, StatFormatter formatter) {
        var identifier = new Identifier(id);
        Registry.register(Registries.CUSTOM_STAT, id, identifier);
        CUSTOM.getOrCreateStat(identifier, formatter);
        return identifier;
    }
}

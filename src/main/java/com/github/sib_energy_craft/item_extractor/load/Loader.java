package com.github.sib_energy_craft.item_extractor.load;

import lombok.extern.slf4j.Slf4j;
import net.fabricmc.api.ModInitializer;

import static com.github.sib_energy_craft.sec_utils.load.LoadUtils.load;


/**
 * @since 0.0.1
 * @author sibmaks
 */
@Slf4j
public class Loader implements ModInitializer {

    @Override
    public void onInitialize() {
        var classLoader = getClass().getClassLoader();
        load(classLoader, "mod-pipes-item-extractor", Loader.class.getPackageName());
    }
}

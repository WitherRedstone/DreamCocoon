package com.chinaex123.dream_cocoon;

import com.chinaex123.dream_cocoon.config.DCIServerConfig;
import com.chinaex123.dream_cocoon.config.LootConfigLoader;
import com.chinaex123.dream_cocoon.init.DCICreativeTabs;
import com.chinaex123.dream_cocoon.init.DCItems;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod(DreamCocoon.MOD_ID)
public class DreamCocoon {
    public static final String MOD_ID = "dream_cocoon";
    public static final Logger LOGGER = LogUtils.getLogger();

    public DreamCocoon(IEventBus modEventBus, ModContainer modContainer) {

        DCItems.register(modEventBus);
        DCICreativeTabs.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, DCIServerConfig.SPEC);
        LootConfigLoader.loadLootConfig(FMLPaths.CONFIGDIR.get());
    }
}

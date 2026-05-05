package com.chinaex123.dream_cocoon;

import com.chinaex123.dream_cocoon.config.CommonConfig;
import com.chinaex123.dream_cocoon.config.LootConfigLoader;
import com.chinaex123.dream_cocoon.init.ModCreativeTabs;
import com.chinaex123.dream_cocoon.init.ModItems;
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

        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.SPEC);
        LootConfigLoader.loadLootConfig(FMLPaths.CONFIGDIR.get());
    }
}

package com.chinaex123.dream_cocoon.data;

import com.chinaex123.dream_cocoon.DreamCocoon;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = DreamCocoon.MOD_ID)
public class ModDataGenerator {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent.Client event) {

        event.createProvider(ModModelsProvider::new);

    }
}

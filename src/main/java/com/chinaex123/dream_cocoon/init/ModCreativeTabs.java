package com.chinaex123.dream_cocoon.init;

import com.chinaex123.dream_cocoon.DreamCocoon;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, DreamCocoon.MOD_ID);


    public static final Supplier<CreativeModeTab> DREAM_COCOON_TAB =
            CREATIVE_MODE_TAB.register("dream_cocoon_tab", () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(ModItems.DREAM_BAG.get()))
                    .title(Component.translatable("itemGroup.dream_cocoon"))
                    .displayItems((parameters, output) -> {

                        output.accept(ModItems.GOODIE_BAG.get()); // 好梦包
                        output.accept(ModItems.SWEET_BAG.get()); // 甜梦包
                        output.accept(ModItems.DREAM_BAG.get()); // 美梦包

                    })
                    .build());

    // 注册到NeoForge事件总线里
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}

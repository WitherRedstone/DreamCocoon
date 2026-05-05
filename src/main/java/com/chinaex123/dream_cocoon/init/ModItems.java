package com.chinaex123.dream_cocoon.init;

import com.chinaex123.dream_cocoon.DreamCocoon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

public class ModItems {
    public static final DeferredRegister.Items ITEMS_REGISTER = DeferredRegister.createItems(DreamCocoon.MOD_ID);

    // 好梦包
    public static final DeferredItem<@NotNull Item> GOODIE_BAG = ITEMS_REGISTER.registerSimpleItem("goodie_bag",
            props -> props.stacksTo(16).rarity(Rarity.UNCOMMON));
    // 甜梦包
    public static final DeferredItem<@NotNull Item> SWEET_BAG = ITEMS_REGISTER.registerSimpleItem("sweet_bag",
            props -> props.stacksTo(16).rarity(Rarity.RARE));
    // 美梦包
    public static final DeferredItem<@NotNull Item> DREAM_BAG = ITEMS_REGISTER.registerSimpleItem("dream_bag",
            props -> props.stacksTo(16).rarity(Rarity.EPIC));

    public static void register(IEventBus eventBus){
        ITEMS_REGISTER.register(eventBus);
    }
}

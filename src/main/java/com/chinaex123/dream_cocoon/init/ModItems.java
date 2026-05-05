package com.chinaex123.dream_cocoon.init;

import com.chinaex123.dream_cocoon.DreamCocoon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS_REGISTER = DeferredRegister.createItems(DreamCocoon.MOD_ID);

    // 好梦包
    public static final DeferredItem<Item> GOODIE_BAG = ITEMS_REGISTER.register("goodie_bag", () ->
            new Item(new Item.Properties().stacksTo(16).rarity(Rarity.UNCOMMON)));
    // 甜梦包
    public static final DeferredItem<Item> SWEET_BAG = ITEMS_REGISTER.register("sweet_bag", () ->
            new Item(new Item.Properties().stacksTo(16).rarity(Rarity.RARE)));
    // 美梦包
    public static final DeferredItem<Item> DREAM_BAG = ITEMS_REGISTER.register("dream_bag", () ->
            new Item(new Item.Properties().stacksTo(16).rarity(Rarity.EPIC)));

    public static void register(IEventBus eventBus){
        ITEMS_REGISTER.register(eventBus);
    }
}

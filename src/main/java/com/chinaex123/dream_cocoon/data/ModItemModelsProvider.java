package com.chinaex123.dream_cocoon.data;

import com.chinaex123.dream_cocoon.DreamCocoon;
import com.chinaex123.dream_cocoon.init.ModItems;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelsProvider extends ItemModelProvider {
    public ModItemModelsProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DreamCocoon.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {

        basicItem(ModItems.GOODIE_BAG.get()); // 好梦包
        basicItem(ModItems.SWEET_BAG.get()); // 甜梦包
        basicItem(ModItems.DREAM_BAG.get()); // 美梦包

    }
}

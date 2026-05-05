package com.chinaex123.dream_cocoon.data;

import com.chinaex123.dream_cocoon.DreamCocoon;
import com.chinaex123.dream_cocoon.init.ModItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.data.PackOutput;

public class ModModelsProvider extends ModelProvider {
    public ModModelsProvider(PackOutput output) {
        super(output, DreamCocoon.MOD_ID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {

        itemModels.generateFlatItem(ModItems.GOODIE_BAG.get(), ModelTemplates.FLAT_HANDHELD_ITEM); // 好梦包
        itemModels.generateFlatItem(ModItems.SWEET_BAG.get(), ModelTemplates.FLAT_HANDHELD_ITEM); // 甜梦包
        itemModels.generateFlatItem(ModItems.DREAM_BAG.get(), ModelTemplates.FLAT_HANDHELD_ITEM); // 美梦包

    }
}

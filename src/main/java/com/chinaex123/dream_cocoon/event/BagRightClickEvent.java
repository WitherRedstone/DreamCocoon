package com.chinaex123.dream_cocoon.event;

import com.chinaex123.dream_cocoon.DreamCocoon;
import com.chinaex123.dream_cocoon.config.CommonConfig;
import com.chinaex123.dream_cocoon.config.LootConfigLoader;
import com.chinaex123.dream_cocoon.init.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

import java.util.List;
import java.util.Random;

/**
 * 处理玩家右键使用包裹物品的逻辑
 * <p>
 * 功能：
 * - 监听右键点击好梦包、甜梦包或美梦包事件
 * - 从JSON配置文件中读取掉落列表，按权重随机选择物品
 * - 将选中的物品添加到玩家背包
 * - 非创造模式下消耗一个包裹
 * - 播放开包音效和手部动画
 */
@EventBusSubscriber(modid = DreamCocoon.MOD_ID)
public class BagRightClickEvent {
    private static final Random RANDOM = new Random();

    /**
     * 监听玩家右键点击物品事件，处理包裹类物品的开包逻辑
     * <p>
     * 功能：
     * - 检测玩家是否右键了美梦包、甜梦包或好梦包
     * - 根据包裹类型调用对应的处理方法
     *
     * @param event 玩家右键物品事件对象
     */
    @SubscribeEvent
    public static void onBagRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack stack = event.getItemStack();
        Item item = stack.getItem();

        if (item == ModItems.DREAM_BAG.get()) {
            handleBagUse(event, "dream_bag", stack);
        } else if (item == ModItems.SWEET_BAG.get()) {
            handleBagUse(event, "sweet_bag", stack);
        } else if (item == ModItems.GOODIE_BAG.get()) {
            handleBagUse(event, "goodie_bag", stack);
        }
    }

    /**
     * 处理包裹物品的使用逻辑，包括发放奖励、播放音效和消耗物品
     * <p>
     * 功能：
     * - 从JSON配置加载掉落列表并按权重随机选择物品
     * - 将奖励物品添加到玩家背包
     * - 非创造模式下消耗一个包裹
     * - 播放开包音效并触发动画
     *
     * @param event 玩家右键物品事件对象
     * @param bagName 包裹名称标识（用于加载对应配置）
     * @param bagStack 玩家手中的包裹物品堆叠
     */
    private static void handleBagUse(PlayerInteractEvent.RightClickItem event, String bagName, ItemStack bagStack) {
        var level = event.getEntity().level();

        if (level.isClientSide) {
            return;
        }

        // 从配置文件加载该包裹的掉落物品列表
        List<LootConfigLoader.LootEntry> lootList = LootConfigLoader.getLootForBag(bagName);

        // 如果配置为空则不执行任何操作
        if (lootList.isEmpty()) {
            return;
        }

        // 获取触发事件的玩家实体
        var player = event.getEntity();

        // 根据包裹类型获取本次开包的物品数量
        int lootAmount = getLootAmount(bagStack.getItem());

        // 循环发放指定数量的奖励物品
        for (int i = 0; i < lootAmount; i++) {
            // 按权重随机选择一个掉落项
            LootConfigLoader.LootEntry selectedEntry = selectWeightedEntry(lootList);

            if (selectedEntry != null) {
                // 解析掉落项为实际的物品堆叠（支持组件）
                ItemStack rewardStack = parseItemStack(selectedEntry, level.registryAccess());

                // 将奖励物品添加到玩家背包
                if (!rewardStack.isEmpty()) {
                    player.getInventory().add(rewardStack);
                }
            }
        }

        // 非创造模式下消耗一个包裹物品
        if (!player.isCreative()) {
            bagStack.shrink(1);
        }

        // 播放开包音效
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.NOTE_BLOCK_BELL.value(), SoundSource.PLAYERS, 0.5F, 1.0F);

        // 触发包裹使用的手部摆动动画
        player.swing(event.getHand(), true);

        // 取消默认的右键交互行为
        event.setCanceled(true);
    }

    /**
     * 根据权重从掉落列表中随机选择一个物品项
     * <p>
     * 算法说明：
     * - 计算所有物品的总权重
     * - 在总权重范围内生成随机数
     * - 遍历列表累加权重，找到随机数所在的区间
     *
     * @param lootList 包含权重信息的掉落物品列表
     * @return 选中的掉落项，如果列表为空则返回 null
     */
    private static LootConfigLoader.LootEntry selectWeightedEntry(List<LootConfigLoader.LootEntry> lootList) {
        // 计算所有掉落项的总权重
        int totalWeight = lootList.stream().mapToInt(entry -> entry.weight).sum();

        // 如果总权重无效，返回列表第一项或 null
        if (totalWeight <= 0) {
            return lootList.isEmpty() ? null : lootList.getFirst();
        }

        // 在总权重范围内生成随机数
        int randomValue = RANDOM.nextInt(totalWeight);
        // 当前累计权重值
        int currentWeight = 0;

        // 遍历列表查找随机数对应的物品
        for (LootConfigLoader.LootEntry entry : lootList) {
            // 累加当前物品的权重
            currentWeight += entry.weight;
            // 如果随机数落在当前区间，返回该物品
            if (randomValue < currentWeight) {
                return entry;
            }
        }

        // 兜底返回最后一项（理论上不会执行到这里）
        return lootList.getLast();
    }

    /**
     * 将掉落配置项解析为实际的物品堆叠对象
     * <p>
     * 功能：
     * - 在最小和最大数量之间随机生成物品数量
     * - 优先尝试解析带组件的物品字符串（如 minecraft:sword[damage=5]）
     * - 如果组件解析失败，回退到基础物品创建
     * - 确保最终物品的数量符合配置要求
     *
     * @param entry 包含物品ID、数量和组件信息的掉落配置项
     * @param registryAccess 注册表访问对象，用于解析物品和组件
     * @return 解析后的物品堆叠，如果解析失败则返回空堆叠
     */
    private static ItemStack parseItemStack(LootConfigLoader.LootEntry entry, net.minecraft.core.RegistryAccess registryAccess) {
        try {
            // 获取配置中的物品ID字符串
            String itemString = entry.item;

            // 计算随机数量：在 minCount 和 maxCount 之间
            int count = entry.minCount;
            if (entry.maxCount > entry.minCount) {
                count = entry.minCount + RANDOM.nextInt(entry.maxCount - entry.minCount + 1);
            }

            // 初始化物品堆叠为空
            ItemStack stack = ItemStack.EMPTY;

            // 检测是否包含组件语法（[ 或 {），尝试使用 NBT 解析
            if (itemString.contains("[") || itemString.contains("{")) {
                try {
                    // 构造完整的 NBT 标签字符串
                    var nbtString = "{id:\"" + itemString + "\",Count:" + count + "b}";
                    // 解析 NBT 标签
                    var compoundTag = net.minecraft.nbt.TagParser.parseTag(nbtString);
                    // 使用注册表解析为物品堆叠
                    stack = ItemStack.parse(registryAccess, compoundTag).orElse(ItemStack.EMPTY);
                } catch (Exception parseEx) {
                    // 组件解析失败时记录警告并回退
                    DreamCocoon.LOGGER.warn("组件解析失败，回退到基础物品: {}", itemString);
                }
            }

            // 如果之前解析失败，使用基础方式创建物品
            if (stack.isEmpty()) {
                // 去除组件部分，只保留物品ID
                ResourceLocation location = ResourceLocation.tryParse(itemString.split("\\[")[0].split("\\{")[0]);
                if (location != null) {
                    // 从注册表获取物品对象
                    Item item = BuiltInRegistries.ITEM.get(location);
                    // 创建基础物品堆叠
                    if (item != Items.AIR) {
                        stack = new ItemStack(item, count);
                    }
                }
            } else if (stack.getCount() != count) {
                // 如果解析成功但数量不对，强制设置为配置的数量
                stack.setCount(count);
            }

            return stack;
        } catch (Exception e) {
            // 捕获所有异常，记录错误并返回空堆叠
            DreamCocoon.LOGGER.error("解析物品失败: {}", entry.item, e);
            return ItemStack.EMPTY;
        }
    }

    /**
     * 根据包裹类型获取本次开包应发放的物品数量
     * <p>
     * 功能：
     * - 从 CommonConfig 中读取对应包裹的配置值
     * - 支持美梦包、甜梦包和好梦包三种类型
     * - 未知类型默认返回 1
     *
     * @param bagItem 包裹物品对象
     * @return 该包裹对应的奖励物品数量
     */
    private static int getLootAmount(Item bagItem) {
        // 美梦包的奖励数量
        if (bagItem == ModItems.DREAM_BAG.get()) {
            return CommonConfig.DREAM_BAG_LOOT_AMOUNT.get();
        } else if (bagItem == ModItems.SWEET_BAG.get()) {
            // 甜梦包的奖励数量
            return CommonConfig.SWEET_BAG_LOOT_AMOUNT.get();
        } else if (bagItem == ModItems.GOODIE_BAG.get()) {
            // 好梦包的奖励数量
            return CommonConfig.GOODIE_BAG_LOOT_AMOUNT.get();
        }
        // 未知类型默认返回 1
        return 1;
    }
}

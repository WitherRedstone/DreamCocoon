package com.chinaex123.dream_cocoon.config;

import com.chinaex123.dream_cocoon.DreamCocoon;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 包裹掉落物品的JSON配置加载器
 * <p>
 * 功能：
 * - 在 config/dream_cocoon/ 目录下自动创建和管理三个包裹的掉落配置文件
 * - 读取JSON格式的掉落列表，支持物品权重和数量范围配置
 * - 提供默认配置，如果文件不存在则自动生成
 */
public class LootConfigLoader {

    // 用于格式化输出 JSON 文件的 Gson 实例
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    // 定义 LootEntry 列表的泛型类型，用于 JSON 反序列化
    private static final Type LOOT_LIST_TYPE = new TypeToken<List<LootEntry>>() {}.getType();

    // 缓存已加载的包裹掉落配置，key 为包裹名称，value 为掉落项列表
    private static final Map<String, List<LootEntry>> lootCache = new HashMap<>();

    /**
     * 单个掉落物品的配置数据类
     */
    public static class LootEntry {
        // 物品 ID 字符串，支持组件语法（如 minecraft:sword[damage=5]）
        public String item;
        // 权重值，数值越大被选中的概率越高
        public int weight;
        // 最小掉落数量
        public int minCount;
        // 最大掉落数量
        public int maxCount;
        // 物品组件映射表，键为组件名，值为组件数据
        public Map<String, Object> components;

        /**
         * 无参构造函数，供 Gson 反序列化使用
         */
        public LootEntry() {}

        /**
         * 创建不带组件的掉落项
         *
         * @param item 物品 ID
         * @param weight 权重
         * @param minCount 最小数量
         * @param maxCount 最大数量
         */
        public LootEntry(String item, int weight, int minCount, int maxCount) {
            this.item = item;
            this.weight = weight;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.components = null;
        }

        /**
         * 创建带组件的掉落项
         *
         * @param item 物品 ID
         * @param weight 权重
         * @param minCount 最小数量
         * @param maxCount 最大数量
         * @param components 物品组件映射表
         */
        public LootEntry(String item, int weight, int minCount, int maxCount, Map<String, Object> components) {
            this.item = item;
            this.weight = weight;
            this.minCount = minCount;
            this.maxCount = maxCount;
            this.components = components;
        }
    }

    /**
     * 加载所有包裹的掉落配置文件，并在文件不存在时自动生成默认配置
     * <p>
     * 功能：
     * - 清空现有的缓存数据
     * - 在 config/dream_cocoon/ 目录下创建配置文件夹
     * - 依次加载好梦包、甜梦包和美梦包的 JSON 配置
     *
     * @param configDir 模组配置文件的根目录路径
     */
    public static void loadLootConfig(Path configDir) {
        // 清空内存中的掉落配置缓存，准备重新加载
        lootCache.clear();

        // 拼接模组的专属配置目录路径
        Path modConfigDir = configDir.resolve(DreamCocoon.MOD_ID);

        try {
            // 如果目录不存在则自动创建，确保后续文件写入正常
            Files.createDirectories(modConfigDir);
        } catch (Exception e) {
            // 记录目录创建失败的错误日志
            DreamCocoon.LOGGER.error("创建配置奖励json失败", e);
        }

        // 加载好梦包配置（若文件不存在则使用默认配置并生成文件）
        loadBagLoot(modConfigDir, "goodie_bag.json", createGoodieBagLoot());
        // 加载甜梦包配置
        loadBagLoot(modConfigDir, "sweet_bag.json", createSweetBagLoot());
        // 加载美梦包配置
        loadBagLoot(modConfigDir, "dream_bag.json", createDreamBagLoot());
    }

    /**
     * 加载单个包裹的掉落配置，支持从 JSON 文件读取或生成默认配置
     * <p>
     * 功能：
     * - 检查配置文件是否存在，若存在则解析并缓存
     * - 若文件不存在或解析失败，则使用默认配置并自动生成 JSON 文件
     * - 确保模组在首次运行或配置损坏时仍能正常工作
     *
     * @param configDir 配置文件夹路径
     * @param fileName 配置文件名（如 goodie_bag.json）
     * @param defaultLoot 该包裹的默认掉落项列表
     */
    private static void loadBagLoot(Path configDir, String fileName, List<LootEntry> defaultLoot) {
        // 拼接完整的配置文件路径
        Path filePath = configDir.resolve(fileName);

        try {
            // 检查配置文件是否已存在
            if (Files.exists(filePath)) {
                // 使用 FileReader 读取文件内容
                try (FileReader reader = new FileReader(filePath.toFile())) {
                    // 将 JSON 字符串反序列化为 LootEntry 列表
                    List<LootEntry> lootList = GSON.fromJson(reader, LOOT_LIST_TYPE);
                    // 如果解析成功且列表不为空，存入缓存并返回
                    if (lootList != null && !lootList.isEmpty()) {
                        lootCache.put(fileName.replace(".json", ""), lootList);
                        return;
                    }
                }
            }

            // 文件不存在或内容为空时，写入默认配置到磁盘
            writeDefaultConfig(filePath, defaultLoot);
            // 将默认配置存入内存缓存
            lootCache.put(fileName.replace(".json", ""), defaultLoot);

        } catch (Exception e) {
            // 捕获所有异常（如 IO 错误或格式错误），记录日志并回退到默认配置
            DreamCocoon.LOGGER.error("加载配置文件 {} 失败，使用默认配置", fileName, e);
            lootCache.put(fileName.replace(".json", ""), defaultLoot);
        }
    }

    /**
     * 将默认的掉落配置列表写入 JSON 文件
     * <p>
     * 功能：
     * - 使用 Gson 将 LootEntry 对象序列化为格式化的 JSON 字符串
     * - 自动创建新文件或覆盖现有文件
     *
     * @param filePath 目标文件的完整路径
     * @param loot 要写入的默认掉落项列表
     * @throws Exception 如果文件写入过程中发生 IO 错误
     */
    private static void writeDefaultConfig(Path filePath, List<LootEntry> loot) throws Exception {
        // 创建 FileWriter 并使用 try-with-resources 确保流自动关闭
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            // 将掉落列表序列化为 JSON 并写入文件
            GSON.toJson(loot, writer);
        }
    }

    /**
     * 创建好梦包（Goodie Bag）的默认掉落配置列表
     * <p>
     * 功能：
     * - 定义好梦包在未找到配置文件时的初始奖励内容
     * - 包含基础食物、材料和少量矿物，权重分布较为均衡
     *
     * @return 包含多个 LootEntry 对象的掉落列表
     */
    private static List<LootEntry> createGoodieBagLoot() {
        // 初始化掉落列表
        List<LootEntry> loot = new ArrayList<>();

        loot.add(new LootEntry("minecraft:bread", 40, 6, 12)); // 面包
        loot.add(new LootEntry("minecraft:apple", 30, 4, 8)); // 苹果
        loot.add(new LootEntry("minecraft:cookie", 20, 4, 8)); // 饼干
        loot.add(new LootEntry("minecraft:leather", 20, 4, 8)); // 皮革
        loot.add(new LootEntry("minecraft:feather", 20, 4, 8)); // 羽毛
        loot.add(new LootEntry("minecraft:egg", 20, 2, 8)); // 鸡蛋
        loot.add(new LootEntry("minecraft:redstone", 20, 2, 5)); // 红石粉
        loot.add(new LootEntry("minecraft:ink_sac", 20, 2, 5)); // 墨囊
        loot.add(new LootEntry("minecraft:glow_ink_sac", 10, 2, 8)); // 荧光墨囊
        loot.add(new LootEntry("minecraft:iron_ingot", 10, 4, 5)); // 铁锭
        loot.add(new LootEntry("minecraft:golden_carrot", 10, 4, 8)); // 金胡萝卜
        loot.add(new LootEntry("minecraft:quartz", 5, 4, 8)); // 下界石英
        return loot;
    }

    /**
     * 创建甜梦包（Sweet Bag）的默认掉落配置列表
     * <p>
     * 功能：
     * - 定义甜梦包在未找到配置文件时的初始奖励内容
     * - 包含钻石、金苹果等高价值物品和稀有材料，品质优于好梦包
     *
     * @return 包含多个 LootEntry 对象的掉落列表
     */
    private static List<LootEntry> createSweetBagLoot() {
        // 初始化掉落列表
        List<LootEntry> loot = new ArrayList<>();

        loot.add(new LootEntry("minecraft:diamond", 50, 2, 5)); // 钻石
        loot.add(new LootEntry("minecraft:golden_apple", 40, 1, 2)); // 金苹果
        loot.add(new LootEntry("minecraft:honeycomb", 30, 1, 3)); // 蜜脾
        loot.add(new LootEntry("minecraft:echo_shard", 20, 1, 2)); // 回响碎片
        loot.add(new LootEntry("minecraft:nautilus_shell", 20, 1, 1)); // 鹦鹉螺壳
        loot.add(new LootEntry("minecraft:emerald", 20, 1, 8)); // 绿宝石
        loot.add(new LootEntry("minecraft:blaze_rod", 20, 1, 4)); // 烈焰棒
        loot.add(new LootEntry("minecraft:chorus_fruit", 10, 1, 4)); // 紫颂果
        loot.add(new LootEntry("minecraft:wind_charge", 5, 1, 1)); // 风弹
        return loot;
    }

    /**
     * 创建美梦包（Dream Bag）的默认掉落配置列表
     * <p>
     * 功能：
     * - 定义美梦包在未找到配置文件时的初始奖励内容
     * - 包含下界之星、不死图腾等顶级稀有物品，是最高品质的奖励
     *
     * @return 包含多个 LootEntry 对象的掉落列表
     */
    private static List<LootEntry> createDreamBagLoot() {
        // 初始化掉落列表
        List<LootEntry> loot = new ArrayList<>();

        loot.add(new LootEntry("minecraft:nether_star", 20, 1, 1)); // 下界之星
        loot.add(new LootEntry("minecraft:armadillo_scute", 20, 1, 2)); // 犰狳鳞甲
        loot.add(new LootEntry("minecraft:turtle_scute", 20, 1, 2)); // 海龟鳞甲
        loot.add(new LootEntry("minecraft:heart_of_the_sea", 10, 1, 1)); // 海洋之心
        loot.add(new LootEntry("minecraft:shulker_shell", 10, 1, 1)); // 潜影壳
        loot.add(new LootEntry("minecraft:totem_of_undying", 10, 1, 1)); // 不死图腾
        loot.add(new LootEntry("minecraft:enchanted_golden_apple", 10, 1, 2)); // 附魔金苹果
        loot.add(new LootEntry("minecraft:netherite_ingot", 5, 1, 1)); // 下界合金锭
        return loot;
    }

    /**
     * 根据包裹名称获取对应的掉落配置列表
     * <p>
     * 功能：
     * - 从内存缓存中查询指定包裹的掉落项
     * - 如果缓存中不存在该包裹，则返回空列表以避免空指针异常
     *
     * @param bagName 包裹名称标识（如 "goodie_bag"）
     * @return 对应的掉落项列表，若未找到则返回空列表
     */
    public static List<LootEntry> getLootForBag(String bagName) {
        // 尝试从缓存获取数据，若不存在则返回新的空 ArrayList
        return lootCache.getOrDefault(bagName, new ArrayList<>());
    }
}

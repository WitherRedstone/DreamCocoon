# 梦境包裹(Dream Cocoon) 功能分类文档

[English](#english) | [中文](#中文)

---

# **English**

# Dream Cocoon

## Mod Description

Dream Cocoon is a sleep reward mod that encourages players to maintain consistent sleeping habits. 
Each night you sleep, you accumulate consecutive days and receive reward bags of varying quality (Goodie Bag, Sweet Bag, Dream Bag).

## Configuration

- **Loot Configuration**: `config/dream_cocoon/*.json` - Customize drop items for each bag
- **General Configuration**: `config/dream_cocoon-common.toml` - Adjust probabilities, thresholds, and quantities

## Key Features

- **Consecutive Sleep Tracking**: Automatically records and persists each player's sleep progress
- **Three-Tier Reward System**: Goodie Bag → Sweet Bag → Dream Bag, quality increases with consecutive days
- **Highly Configurable**: Supports JSON customization of drop items, weights, quantities, and item components
- **Anti-Cheat Mechanism**: Detects time command modifications and automatically resets abnormal records
- **Data Persistence**: Sleep progress is saved in world data, survives game restarts

### Highly Configurable

#### JSON Loot Configuration
- Configuration location: `config/dream_cocoon/`
- Three separate files:
    - `goodie_bag.json` - Goodie Bag loot table
    - `sweet_bag.json` - Sweet Bag loot table
    - `dream_bag.json` - Dream Bag loot table
- **Custom Drops**: Players can freely add, remove, or modify drop items
- **Weight System**: Control drop probability through weight values
- **Quantity Range**: Support setting minimum and maximum drop quantities
- **Item Components**: Supports item component syntax

#### TOML General Configuration
- Configuration location: `config/dream_cocoon-common.toml`
- Configurable options:
    - Number of items per bag opening
    - Quality boost threshold (consecutive days to trigger high-quality rewards)
    - Maximum consecutive days threshold
    - Drop probability for each bag quality (percentage-based)

---

**Sweet dreams every night! **


---

# **中文**

## 模组简介

梦境包裹是一个睡眠奖励模组，鼓励玩家保持连续睡觉习惯。
每晚睡觉都会累积连续天数，根据天数发放不同品质的奖励包裹（好梦包、甜梦包、美梦包）。

## 配置说明

- **掉落配置**：`config/dream_cocoon/*.json` - 自定义每个包裹的掉落物品
- **通用配置**：`config/dream_cocoon-common.toml` - 调整概率、阈值和数量
- 
## 主要特性

- **连续睡觉追踪**：自动记录并持久化保存每位玩家的睡觉进度
- **三阶奖励系统**：好梦包 → 甜梦包 → 美梦包，品质随连续天数提升
- **高度可配置**：支持 JSON 自定义掉落物品、权重、数量和物品组件
- **防作弊机制**：检测时间命令修改，自动重置异常记录
- **数据持久化**：睡觉进度保存在世界存档中，重启不丢失
- **防作弊机制**：使用 `/day set` 或 `/time set` 命令修改时间会自动重置连续记录

### 高度可配置

#### JSON 掉落配置
- 配置文件位置：`config/dream_cocoon/`
- 支持三个独立文件：
    - `goodie_bag.json` - 好梦包掉落列表
    - `sweet_bag.json` - 甜梦包掉落列表
    - `dream_bag.json` - 美梦包掉落列表
- **自定义掉落**：玩家可以自由添加、删除或修改掉落物品
- **权重系统**：通过权重值控制物品掉落概率
- **数量范围**：支持设置最小和最大掉落数量
- **物品组件**：支持物品组件语法

#### TOML 通用配置
- 配置文件位置：`config/dream_cocoon-common.toml`
- 可配置项：
    - 各包裹的开包物品数量
    - 品质提升阈值（连续多少天触发高品质奖励）
    - 最大连续天数阈值
    - 各品质包裹的掉落概率（百分制）

---

**祝你每晚都有好梦！*
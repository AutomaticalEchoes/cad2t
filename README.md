# 区块加成(ChunkAddition)
[![Build Status](https://img.shields.io/badge/MinecraftForge-1.20.x-brightgreen)](https://github.com/MinecraftForge/MinecraftForge?branch=1.20.x)

_阅前提示：本人喜欢用<sub title="如果影响你观看就先给你道个歉啦！>-<" >**`注`**</sub>来添加注释。_
## 介绍

- 本模组提供了方法让玩家可以通过编写json或yaml文件使区块获得一些额外效果，例如：生物进入含村庄结构的区块后获得某些buff效果等。
- 本模组可以仅安装在服务端。
- **需要注意的是：** 这里的区块不是指MC广义上在XoZ平面上划分的二维坐标(x,z)区块，而是包含了Y轴的、MC存储时计算的三维坐标(x,y,z)区块(即16x16x16的空间).

## 文件、目录与命名。
1. 想要配置自定义加成，首先需要在Minecraft的根目录('.minecraft')下创建一个名为'additions'的文件夹。  
2. 服务端模组加载时会自带读取该文件夹下的json文件，其中文件名会作为该加成类型的注册名，因此**文件的命名应符合编程命名规范**。
3. 注册名是加成类型的唯一标识，命名重复的两个文件，即便实现的内容不同，模组依然会认为发生了重复注册，进而不注册后者；
   同时，虽然文件命名时对大小写不敏感，但是作为注册名时会统一转化为小写，若两个文件分别命名为 'cat' 和 'Cat' 时就会产生上述问题。
   因此文件命名时请尽可能使用小写字母和下划线命名法。
   
## 文件的编写
### 注册名元素的编写格式
- 与数据包一样，编写文件时也是通过标签（tag) 或 注册名（key）寻找Minecraft相关注册表中的元素。由于模组同时支持yml和json格式的文件，因此有两种写法，  
  这两种写法同时适用两种文件类型，并不是独占的。**有些地方会限制写法**
- Yaml的数组有多种写法，所有写法都是可以的，这里展示用了其中一种。
- ```json
   // json写法
   "structures": [
     "tag/minecraft:village" // 第一种
     "key/minecraft:shipwreck" // 第一种
    ],
   "biomes":[
     {"key":"minecraft:taiga"}, //第二种
     {"key":"minecraft:desert"} //第二种
   ]
  ```
- ```yaml
  # yml 写法
   structures:
    - tag: minecraft:village # 第二种
    - key: minecraft:shipwreck # 第二种
   biomes:
    - key/minecraft:taiga  # 第一种
    - key: minecraft:desert # 第二种
  ```
- 上述两个例子展示了两种写法，Json文件更推荐使用第一种写法，Yaml文件两种写法都可以。
### 加成持有区域
- 即：指定持有该加成效果的区域、目标处于指定类型区域时触发该加成效果。  
- 文件中至少要指定一个持有该加成的区域类型，模组提供了两个区域大类，一个是结构类(structures)另一个是群系类(biomes)。
  两者既可单独出现，又可共存。共存时两者之间的关系为逻辑或。
- 指定区域大类后，可以在其中根据MC原版的注册名或标签指定特定的结构类型或是群系类型，参数类型为数组，可以指定多个类型。
- 仅指定区域大类时，默认该大类的所有区域类型都持有该加成效果，有需求时直接写一个空数组即可。  
  同时，由于结构（structure）本质上是在群系（biome）的基础上细分的区域，指定持有者为全部群系类型后就包括了全部结构类型，因此可以不用写结构相关的类型指定。
- 若你缺乏对MC原版注册名的相关知识，可以通过原版创造模式下的‘/locate’指令查阅相关注册名和标签，带“#”的是标签，没有则是注册名。
- json示例：
  ``` json
  // 1. 处于针叶林群系 或 含任意种类的村庄的区域时触发
  "structures": ["tag/minecraft:village"],
  "biomes": ["key/minecraft:taiga"]
  
  // 2. 处于沙漠群系时触发
  "biomes": ["key/minecraft:desert"]
  
  // 3. 处于针叶林群系 或 沙漠群系 或 所有含结构的区域时触发
  "structures": [],
  "biomes": ["key/minecraft:taiga","key/minecraft:desert"]
  
  // 4. 所有区域
  "biomes": []
  ```
- yaml示例：
  ``` yaml
  # 1. 处于针叶林群系 或 含任意种类的村庄的区域时触发
  structures: 
    - tag/minecraft:village
  biomes: 
    - key/minecraft:taiga
  
  # 2. 处于沙漠群系时触发
  biomes: 
    - key/minecraft:desert
  
  # 3. 处于针叶林群系 或 沙漠群系 或 所有含结构的区域时触发
  structures: [] # 这里用上文提到的yaml数组的另一种写法写一个空数组。
  biomes: 
    - key/minecraft:taiga
    - key/minecraft:desert
  
  # 4. 所有区域
  biomes: []
  ```
  
### 加成行为
- 即指定该加成实现的效果。
- 模组在早期开发时提供一个加成类实现多个效果的方法，后来作者对模组定性，所有的效果应该通过向生物/玩家添加Buff间接实现，这样玩家便可以通过Buff栏直观地了解到该区域具有哪些加成，  
  因此删减了该部分功能，同时遗留下了一些编写时的格式问题。~~真不是我偷懒~~
- 指定加成行为的最外层为"actions"，综上所述，虽然为复数且参数为数组，但实际上数组中只有第一个元素是有效的。
   ``` json
   "actions": [{e1},{e2}] //e1,e2代指后续内容，其中e2无效
   ```
   ``` yaml
   actions: [{e1},{e2}] //e1,e2代指后续内容，其中e2无效
   # yaml这里常见应该是这样的，因为后续还要写数组元素（element）的子参数：
   actions: 
     - e1
     - e2 
   ```
- 编写加成行为时，需要指定加成的类型、加成效果作用的目标。  
  这里仅展示"cad2t:apply_effect"也就是添加Buff类效果的撰写方式。
  
#### 加成行为种类("type")：
##### 1. 添加Buff("cad2t:apply_effect")
   ``` json
   {
     "type": "cad2t:apply_effect", //指定加成类型，不指定时默认为apply_effect，所以这里可以删掉省略。
      "action_targets": [
        "key/minecraft:player" //必填，指定加成作用的目标，参数为数组，可指定多个目标类型。
      ],
      "effects": [  //必填，指定添加的Buff，该关键字为'apply_effect'独有，参数为数组，可添加多个Buff。
        {
          "key":"minecraft:luck", //必填，指定添加的Buff类型，仅支持通过注册名添加，可以使用原版的'/effect'指令了解相关注册名
          "level": "1" // 选填，指定Buff的等级，默认为一级，因此这里其实可以删掉。
        }
      ]
   }
   ```
   ``` yaml
     type: cad2t:apply_effect 
     action_targets: 
       - key/minecraft:player 
     effects:  
       -
         key: minecraft:luck # 上面太长在这里补充：由于要指定Buff的等级，所以这里的注册名指定第二种写法，而且不支持tag
         level: 1 
   }
   ```
   指定加成行为完整的攥写格式:
   ``` json
   //向玩家添加一个等级一的幸运Buff
    "actions": [
    {
      "type": "cad2t:apply_effect",
      "action_targets": [
        "key/minecraft:player" // 这里需要注意的是添加BUFF的作用目标只能为生物实体，但是由于MC原版所有实体在同一个注册表里，
                               // 因此如果这里填写非生物实体，例如箭、盔甲架等，文件加载时不会报错，但实际上无法生效。
      ],
      "effects": [
        {
          "key":"minecraft:luck",
          "level": "1"
        }
      ],
    }
   ]
   ```
   ``` yaml
   //向玩家添加一个等级一的幸运Buff
    actions:
      - 
        action_targets:
          - key/minecraft:player 
        effects: 
          key: minecraft:luck
          level: 1
      # 这里直接省掉了type
   ```
### 进阶相关
[**过滤器(filter)**](README_FILTER.md)

## 调试指令  

一般而言，模组加载时服务端会打印加载日志。但是为了方便玩家在单人模式中调试，模组提供几个简单指令：
- 输出加载日志：```/cad2t log load_info ```
- 输出注册表中所有区域加成（注册名）：```/cad2t log registry_info ```
- 输出当前区块含有的加成（注册名）：```/cad2t log additions_in_chunk ```
___
**非专业moder,望大家多多海涵.  
如果你发现了什么问题或者有什么建议,可以发邮件给我.~~回不回复随缘~~  
email:AutomaticalEchoes@outlook.com.**

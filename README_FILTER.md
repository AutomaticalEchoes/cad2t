# 过滤器(filter)
- 过滤器的作用是添加加成行为生效的条件。
- 仅有添加Buff行为("cad2t:apply_effect")支持使用过滤器。<sub title="其他的都被我删啦！" >**`注`**</sub>
- 过滤器应写在加成行为中，即：
  ``` json
   //向玩家添加一个等级一的幸运Buff
    "actions": [
    {
      "type": "cad2t:apply_effect",
      "action_targets": [...],
      "effects": [{...},{...}],
      "filter": { // 过滤器本身选填，不需要时这段删掉即可
       "logic": "and" //选填，筛选器之间的逻辑关系，默认为逻辑与(&)，即‘且’，因此这里可以删掉。
       "predicates": [ // 必填，筛选器组，参数为数组，可以添加多个筛选器。其实过滤、筛选同义，主要是为了层级关系以及子筛选器间的逻辑关系。
         {           // 子筛选器p1，定义p1为一个筛选器组，元素中指定”logic“参数时会认定为一个筛选器组。
           "logic": "or", //同上，这里为逻辑或(||)。子筛选器组通常和父筛选器组的逻辑关系相反，不然我为什么要分一个子筛选器组？
           "predicates": [{a1}, {a2}] //同上，p1 = a1 或 a2;
         },
         {p2}// 子筛选器p2
        ]// 最终结果 = p1 & p2  = (a1 || a2) & p2
      }
    }
   ]
   ```
## 筛选器类型(predicate:type)
### 装备筛选器("equip_check")
- 作用是检视目标身上穿戴的装备是否在指定的物品种类范围内。
- 可选填六个属性对应原版六个装备格，即：头盔("head")、胸甲("chest")、腿甲("legs")、鞋子("feet")、主手("mainhand")、副手("offhand")。不需要筛选的部位不写即可。
- 六个装备格元素的参数皆为数组，数组中的元素逻辑关系为逻辑或。
- ```json 
  {
    "type":"equip_check",
    "mainhand":["key/minecraft:torch","tag/forge:tools"] // 当主手手持火把 或 手持含forge提供的工具标签的物品时。
    "offhand":["key/minecraft:shield"] // 当副手手持盾牌时。其实盾牌也含有forge:tools标签，所以这里不管是哪只手，手持盾牌就行。
  } // 最终生效条件为：当副手手持盾牌 &（主手手持火把 || 主手手持木剑时）。  
  ```
- 想要更复杂的判断时可以结合上述提到的筛选器组写多个装备筛选器：
  ```json 
  {
    "logic":"or"
    "predicates": [
      {
        "type":"equip_check",
         "mainhand":["key/minecraft:torch","key/minecraft:wooden_sword"] // 当主手手持火把 或 手持木剑时。
      }, 
      {
        "type":"equip_check",
        "offhand":["key/minecraft:shield"] // 当副手手持盾牌时。
      }
    ] 
  } // 最终生效条件为：当副手手持盾牌 || 主手手持火把 || 主手手持木剑时。  
  ```


### 属性筛选器(”attribute_check“)
- 作用是检视生物实体对应的属性值是否在数值范围内或是在集合中。
- 模组同样提供了六种可检视的属性，分别是生命值（”health“，攻击力（”attack_damage"),攻击速度（“attack_speed”）,  
  盔甲值（“armor”），移动速度（“movement_speed")，最大生命值（”max_health")。
- 与装备筛选器一样，每个属性筛选器能含有关多个关系为逻辑与的属性元素。
- 属性元素的参数亦为数组，但需要注意的是，**当数组仅有两个元素且右边的元素大于左边时候，会识别为数值域而非集合**。
- ```json
  {
     "type":"attribute_check",
     "max_health": ["20","10"], //生命值上限为20或10
     "health": ["5","10"], //5 < 10 这里是数值域，即：生命值大于等于5且小于等于10
     "attack_damage": ["1","9","10"], //攻击力为1 或 9 或 10
  } // 同样的，要满足上述所有条件才会触发加成效果。
  ```
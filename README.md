# TimePlanTest
 红岩期中作业，写的一个类似于任务清单的软件
 
---
## 一、软件介绍
>主要有一下控件及界面：  
>1、登录，2、任务清单，3、日历，4、时间轴选取任务   
>主要有以下功能：  
>1、登录的账号保存，2、任务的后台提醒，3、日历与时间轴的互动  
### First界面
第一个界面用于设置当天的任务清单  
#### 1、任务清单
使用ExpandListView使用的二级菜单（RecyclerView用来实现二级菜单过于复杂），二级菜单的任务可以点击弹开dialog进行设置。 
#### 2、FloatingActionButton
右下角使用的FloatingActionButton，点击弹出dialog添加任务
### Second界面
第二个界面用时间轴的方式设置所有天数的所有任务  
#### 1、日历
用的寒假考核时写的自定义View，实现与底部的时间轴的互动，外面包括了ViewPager2，解决了与外部ViewPager2的滑动冲突。（这次还解决了ViewPager2不能得到内部实例的问题，详见上次我传在群里的ViewPager2缓存机制）
#### 2、时间轴
一个用于滑动选取时间段的自定义View，详见我的寒假考核的Readme [TimePlaning](https://github.com/985892345/TimePlaning.git "TimePlan")
### Third界面
第三个界面本来时用于一些设置，因时间原因，未写
### 侧滑界面
侧滑界面除了头像能点击外，其他按钮不能点击
### 登录界面
登录界面因时间原因只有一个按钮用于登录  

---
## 二、其他
目前有一些问题无法解决
1. 部分手机发送通知时不震动
2. 小米版本12.5.3出现发送通知时自己锁屏的问题
3. 第二个界面的时间轴与外面的ViewPager2的滑动冲突问题（此问题较难解决，因为ViewPager2是final无法改写）

---
### 三、感想
#### 1、本次软件感想
>这次期中考核对于我来说算比较幸运的，内容刚好与我的寒假考核做的内容较为相似，除了使用以前造的轮子外
>自己也在这三天中学到了其他东西，比如：Service的使用、Broadcast的使用
#### 2、学习的感想
>不知不觉中自己在红岩学了一个半学期了，从最开始的一个什么都不会的菜鸡到现在三天写出一个软件，确实感觉自己学了很多东西，
>在此，也十分的感谢学长们的陪伴，让我少走了许多弯路


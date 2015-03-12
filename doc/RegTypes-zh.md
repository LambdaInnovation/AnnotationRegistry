各注册类型及用法
===
Annotation Registry中实现了很多注册类型。每个类型包括一个Annotation和一个负责注册的class（包含```@RegistryTypeDecl```注解）两部分。要了解详细的注册机理请参考```cn.annoreg.mc```包的代码。以下只对各类型进行最简单的用法介绍。

Block
---
在以Block或Block的继承类为类型的public static字段上使用```@RegBlock```。最简单的用法为：
```java
@RegBlock
public static BlockXXX xxx = new BlockXXX();
```
可以通过item指定ItemBlock的class。可以添加```@RegBlock.OreDict```来将其注册到矿物字典。可以添加```@RegBlock.BTName```来在注册后设置该Block的unlocalized name和icon name。例如：
```java
@RegBlock
@RegBlock.OreDict("oreTin")
@RegBlock.BTName("tinore")
public static BlockOre oreTin = new BlockOre();
```

ChestContent
---
在WeightedRandomChestContent类型的字段上使用```@RegChestContent```。例如：
```java
@RegChestContent(0, 1, 2, 3)
public static WeightedRandomChestContent record0 = new WeightedRandomChestContent(new ItemStack(MyItems.record0), 1, 1, 5);
```
！注意：这个类型的注册接口还不完善，可能会修改。

Command
---
在实现了ICommand的class上使用```@RegCommand```。注意需要在serverStarting时调用
```java
RegistrationManager.INSTANCE.registerAll(this, "StartServer");
```

Entity
---
在Entity的继承类上使用```@RegEntity```。可以使用```@RegEntity.HasRender```和```@RegEntity.Render```联合指定Renderer，例如：
```
@RegistrationClass
@RegEntity
@RegEntity.HasRender
public class MyEntity {
    @RegEntity.Render
    @SideOnly(Side.CLIENT)
    public static MyRender renderer;
}
```

EventHandler
---
在EventHandler的类或字段中使用```@RegEventHandler```。如果在类上使用，会自动创建一个新实例。如果在字段上使用，则直接使用类的值进行注册。默认情况下会向FML和Forge两个EventBus注册，可以使用value参数指定。

Item
---
参考Block的用法。

MessageHandler
---
在实现了IMessageHandler的类上使用```@RegMessageHandler```。例如：
```java
@RegMessageHandler(msg = MyMessage.class, side = RegMessageHandler.Side.CLIENT)
class MyMessageHandler implements IMessageHandler<MyMessage, IMessage> {
```
通过side指定Handler是在服务器端或是在客户端。

PreloadTexture
---
强制在注册时加载资源。使用```@ForcePreloadTexture```在包含public static ResourceLocation字段的class上。例如：
```java
@RegistrationClass
@ForcePreloadTexture
public class Resources {
    //这个png会在注册时加载
    public static ResourceLocation texture1 = new ResourceLocation("example:textures/models/texture1.png");
}
```

SubmoduleInit
---
在Mod的init时向其他class发送一个init指令。可以用在类上（调用public static的init函数）或字段上（调用public的init函数）例如：
```java
@RegistrationClass
@RegSubmoduleInit
class MySubmodule {
    public static void init() {
        //这个函数会在init阶段被调用
    }
}
```

TileEntity
---
在继承TileEntity的类上使用```@RegTileEntity```。可以指定Renderer，方法参见Entity。

WorldGen
---
在以实现了IWorldGenerator接口的类为类型的字段上使用```@RegWorldGen```，调用GameRegistry.registerWorldGenerator注册一个IWorldGenerator。

GuiHandler
---
在以GuiHandlerBase的继承类为类型的字段上使用```@RegGuiHandler```。要实现功能，需要实现GuiHandlerBase中getClientGui，或者getClientContainer和getServerContainer函数。前者使GuiHandlerBase可以开启一个基于Container的Gui，后者则可以开启一个仅存在于客户端的Gui。例如用在AcademyCraft中的：
```java
@RegistrationClass
public class GuiHandlers {
	
	@RegGuiHandler
	public static GuiHandlerBase handlerPresetSettings = new GuiHandlerBase() {
		@Override
		@SideOnly(Side.CLIENT)
		protected GuiScreen getClientGui() {
			return new GuiPresetSettings();
		}
	};

}
```
要开启这个Gui，只需要在任意地方（必须在客户端）调用```GuiHandlers.handlerPresetSettings.openClientGui()```即可。

NetworkCall
---
！注意：这个类型还不完善。
NetworkCall可以允许客户端触发服务器端的一个函数，或者反之。函数的参数通过Serialization模块实现。一个简单的例子如下：
```java
@RegNetworkCall(side = Side.SERVER)
public static void myNetworkCall(@StorageOption.Data Integer i) {
    System.out.print(i);
}
```
在客户端直接调用这个myNetworkCall函数，就会在服务器端输出给定的参数。一个典型的用法是在服务器端响应客户端Gui上的事件。

目前只支持从客户端到服务器端的static函数和少数几种参数类型。
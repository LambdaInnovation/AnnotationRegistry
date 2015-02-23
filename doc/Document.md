Annotation Registry Document
===

Introduction
---
AnnotationRegistry is a Minecraft coremod, with which other programmers can use annotation-based registry in their own mod. For big mods, this is especially preferred, as there may be a lot of Blocks, Items, TileEntities and other kinds of game objects that needs to be registered when the mod is loaded. With AnnotationRegistry, you no longer need a huge class to handle those registration, but can write your registration wherever you need.

This mod is currently developed under Forge 1.7.2-10.12.2.1121.

Setup
---
In development environment, put the code files under src/main/java in your src/main/java folder or include them in your eclipse. To load the core plugin, put jar/AnnotationRegistry.jar into eclipse/mods. The mod that uses AnnotationRegistry does not need to be coremod.

Basic usage
---
To use the registry system, you need to follow these steps:

1. Add ```@RegistrationMod``` annotation on your main class. For example, AcademyCraft uses the following:
    ```java
    @RegistrationMod(pkg = "cn.academy.", res = "academy", prefix = "ac_")
    ```
    This allow the registry system to identify all classes that belongs to this mod, and get some basic information for this mod.

2. Find out the classes that contains the registration data (annotations on the class, its fields, or its inner class), use annotation ```@RegistrationClass``` on the class. This will make the registry system identify these classes at a very early stage of loading (but do not really load them).

3. Add Reg annotations (i.e. ```@RegBlock``` for blocks) on the class or its field(s).

4. [Deprecated] Call ```registerAll``` function on ```RegistrationManager``` in the mod class, at the right time. For example, this is used in init stage of the mod, to register all blocks:
    ```java
    RegistrationManager.INSTANCE.registerAll(this, "Block");
    ```

5. Each ```RegistryType``` belongs to a registration stage. A list of stages available can be found in ```cn.annoreg.core.LoadStage```. Use LoadStage to register all RegisterType is recommended, like this:

    ```java
    RegistrationManager.INSTANCE.registerAll(this, "Init");
    ```


Registry types
---
Each RegistryType represents a kind of game objects in Minecraft, including:

* Command.<br/>
    Server command. Use ```RegCommand``` on class that implements ```ICommand```.
* ResourceLocation.
* Model
* GUI
* EventHandler<br/>
    Forge and FML event handler. Use ```RegEventHandler``` on classes or public static fields.
* MessageHandler<br/>
    Network message handler. The mod must have a ```SimpleNetworkWrapper``` field (no matter static or not) with ```@RegMessageHandler.WrapperInstance``` on it. The registry system use IDs starting from 100, so the mod itself can also register IMessageHandler.
* SubmoduleInit<br/>
    Submodule in the mod that have an ```init``` function to be called when the mod is loadded. Use ```RegSubmoduleInit``` on classes or public static fields.
* Entity<br/>
    Entity in Minecraft. Use ```RegEntity``` on Entity class.
* Item<br/>
    Item in Minecraft. Use ```RegItem``` on public static field with the type of your Item class.
* Block<br/>
    Block in Minecraft. Use ```RegBlock``` on public static field with the type of your Block class.
* TileEntity<br/>
    TileEntity in Minecraft. Use ```RegTileEntity``` on the TileEntity class.

New instance in registry
---
For annotations that can be used on fields, sometimes new instance is created by registry system. 

If the system needs an not-null object (which is usually the case), the system will try to create a new instance if the field has null value when the registration happens. The class used is always the Type of the field, and the constructor used is always the one that takes no parameters. If such constructor does not exist, the registration failes. The user has two choice:
* Use the empty constructor, which does not allow multiple instance with different settings (such as block's sub id).
* Use an assignment directly on the field or in the static block, so that the system does not need to create the instance. In this case, an annonymous inner class is sometimes useful.

New registry type
---
In AnnotationRegistry, the core system and the implementation is separated. And it is also very easy for other mods to add new RegistryType. Use ```@RegistryTypeDecl``` on class implementating one base class in package ```cn.annoreg.base```. Refer to classes in ```cn.annoreg.mc```.

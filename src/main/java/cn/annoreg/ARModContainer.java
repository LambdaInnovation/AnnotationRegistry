/**
 * Copyright (c) Lambda Innovation, 2013-2015
 * 本作品版权由Lambda Innovation所有。
 * http://www.li-dev.cn/
 *
 * This project is open-source, and it is distributed under
 * the terms of GNU General Public License. You can modify
 * and distribute freely as long as you follow the license.
 * 本项目是一个开源项目，且遵循GNU通用公共授权协议。
 * 在遵照该协议的情况下，您可以自由传播和修改。
 * http://www.gnu.org/licenses/gpl.html
 */
package cn.annoreg;

import java.io.File;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.annoreg.core.RegistrationManager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.MetadataCollection;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.ModContainer.Disableable;
import cpw.mods.fml.common.discovery.ASMDataTable;
import cpw.mods.fml.common.discovery.ModCandidate;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLLoadCompleteEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.DefaultArtifactVersion;
import cpw.mods.fml.common.versioning.VersionRange;

public class ARModContainer extends DummyModContainer {
	
	public static Logger log = LogManager.getLogger("AnnotationRegistry");
	public static final String MODID = "AnnoReg";

	private static ModMetadata getModMetadata() {
        HashMap def = new HashMap();
        def.put("name", MODID);
        def.put("version", "2.0a3");
        return AnnoRegCorePlugin.mc.getMetadataForId(MODID, def);
	}
	
	public ARModContainer() {
	    super(getModMetadata());
	}
	
    @Override
    public boolean registerBus(EventBus bus, LoadController controller)
    {
    	bus.register(this);
        return true;
    }

    @Subscribe
    public void constructMod(FMLConstructionEvent event) {
    	log.info("AnnotationRegistry is loading.");
    	
    	//Get annotation information from forge asm data table.
    	//This must be done before PreInit stage.
    	ASMDataTable dt = event.getASMHarvestedData();
    	RegistrationManager.INSTANCE.addRegistryTypes(dt.getAll("cn.annoreg.core.RegistryTypeDecl"));
    	RegistrationManager.INSTANCE.annotationList(dt.getAll("cn.annoreg.core.Registrant"));
    	RegistrationManager.INSTANCE.addAnnotationMod(dt.getAll("cn.annoreg.core.RegistrationMod"));
    }

    @Subscribe
    public void loadComplete(FMLLoadCompleteEvent event) {
    	log.info("AnnotationRegistry is loaded. Checking states.");
    	RegistrationManager.INSTANCE.checkLoadState();
    }
}

package cn.annoreg.core;

public enum LoadStage {
	
	PRE_INIT("PreInit"),
	INIT("Init"),
	POST_INIT("PostInit"),
	START_SERVER("StartServer");
	
	public final String name;
	
	private LoadStage(String name) {
		this.name = name;
	}
	
}

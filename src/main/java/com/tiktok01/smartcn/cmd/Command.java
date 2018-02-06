package com.tiktok01.smartcn.cmd;

public class Command {

	@Cmd(name="coremem")
	private String srcCoreMem;
	@Cmd(name="srcdict")
	private String srcDict;
	@Cmd(name="target")
	private String targetDir;
	
	public Command() {
	}

	public String getSrcCoreMem() {
		return srcCoreMem;
	}

	public void setSrcCoreMem(String srcCoreMem) {
		this.srcCoreMem = srcCoreMem;
	}

	public String getSrcDict() {
		return srcDict;
	}

	public void setSrcDict(String srcDict) {
		this.srcDict = srcDict;
	}

	public String getTargetDir() {
		return targetDir != null? targetDir : "";
	}

	public void setTargetDir(String targetDir) {
		this.targetDir = targetDir;
	}
}

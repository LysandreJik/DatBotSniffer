package fr.main.sniffer.tools.protocol;

import java.util.List;

public class Message {
	
	private String name;
	private String parent;
	private long protocolId;
	private List<Field> Fields;
	private String namespace;
	private boolean useHashFunc;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getParent() {
		return parent;
	}
	public void setParents(String parent) {
		this.parent = parent;
	}
	public long getProtocolId() {
		return protocolId;
	}
	public void setProtocolId(long l) {
		this.protocolId = l;
	}
	public List<Field> getFields() {
		return Fields;
	}
	public void setFields(List<Field> fields) {
		Fields = fields;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public boolean isUseHashFunc() {
		return useHashFunc;
	}
	public void setUseHashFunc(boolean useHashFunc) {
		this.useHashFunc = useHashFunc;
	}

}

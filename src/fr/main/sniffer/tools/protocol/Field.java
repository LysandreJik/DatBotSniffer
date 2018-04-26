package fr.main.sniffer.tools.protocol;

public class Field {

	private String name;
	private String type;
	private String writeMethod;
	private boolean isVector;
	private boolean isDynamicLength;
	private long length;
	private String writeLengthMethod;
	private boolean useTypeManager;
	private boolean useBBW;
	private long bbwPosition;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getWriteMethod() {
		return writeMethod;
	}
	public void setWriteMethod(String writeMethod) {
		this.writeMethod = writeMethod;
	}
	public boolean isVector() {
		return isVector;
	}
	public void setIsVector(boolean isVector) {
		this.isVector = isVector;
	}
	public boolean isDynamicLength() {
		return isDynamicLength;
	}
	public void setDynamicLength(boolean isDynamicLength) {
		this.isDynamicLength = isDynamicLength;
	}
	public long getLength() {
		return length;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public String getWriteLengthMethod() {
		return writeLengthMethod;
	}
	public void setWriteLengthMethod(String writeLengthMethod) {
		this.writeLengthMethod = writeLengthMethod;
	}
	public boolean isUseTypeManager() {
		return useTypeManager;
	}
	public void setUseTypeManager(boolean useTypeManager) {
		this.useTypeManager = useTypeManager;
	}
	public boolean isUseBBW() {
		return useBBW;
	}
	public void setUseBBW(boolean useBBW) {
		this.useBBW = useBBW;
	}
	public long getBbwPosition() {
		return bbwPosition;
	}
	public void setBbwPosition(long bbwPosition) {
		this.bbwPosition = bbwPosition;
	}

	@Override
	public String toString() {
		return "Field{" +
				"name='" + name + '\'' +
				", type='" + type + '\'' +
				", writeMethod='" + writeMethod + '\'' +
				", isVector=" + isVector +
				", isDynamicLength=" + isDynamicLength +
				", length=" + length +
				", writeLengthMethod='" + writeLengthMethod + '\'' +
				", useTypeManager=" + useTypeManager +
				", useBBW=" + useBBW +
				", bbwPosition=" + bbwPosition +
				'}';
	}
}

package fr.main.display;

public class Data {

    String reducedData = "";
    String extendedData = "";
    int id;
    String name;
    boolean showingTooltip = true;

    public Data(int id, String name, String reducedData, String extendedData) {
        this.reducedData = reducedData;
        this.extendedData = extendedData;
        this.id = id;
        this.name = name;
    }

    public String getReducedData() {
        return reducedData;
    }

    public void setReducedData(String reducedData) {
        this.reducedData = reducedData;
    }

    public String getExtendedData() {
        return extendedData;
    }

    public void setExtendedData(String extendedData) {
        this.extendedData = extendedData;
    }

    public Object[] getReducedDataObject(){
        return new Object[]{id, name, reducedData, "+", "remove "+id};
    }

    public Object[] getExtendedDataObject(){
        return new Object[]{id, name, extendedData, "+", "remove "+id};
    }

    public boolean isShowingTooltip(){
        return showingTooltip;
    }

    public void setShowingTooltip(boolean showingTooltip) {
        this.showingTooltip = showingTooltip;
    }
}

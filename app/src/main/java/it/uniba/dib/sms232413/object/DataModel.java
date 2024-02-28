package it.uniba.dib.sms232413.object;

import java.util.List;

public class DataModel {
    private final List<String> nestedList;
    private final String itemText;
    private boolean isExpandable;

    public DataModel(List<String> nestedList, String itemText) {
        this.nestedList = nestedList;
        this.itemText = itemText;
        isExpandable=false;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public List<String> getNestedList() {
        return nestedList;
    }

    public String getItemText() {
        return itemText;
    }

    public boolean isExpandable() {
        return isExpandable;
    }
}

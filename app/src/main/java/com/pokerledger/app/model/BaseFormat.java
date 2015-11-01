package com.pokerledger.app.model;

/**
 * Created by Catface Meowmers on 7/26/15.
 */
public abstract class BaseFormat { //make abstract class that GameFormat extends
    int baseFormatId = 0;
    String baseFormat = "";

    //setters

    public void setBaseFormatId(int baseFormatId) {
        this.baseFormatId = baseFormatId;
    }

    public void setBaseFormat(String baseFormat) {
        this.baseFormat = baseFormat;
    }

    //getters

    public int getBaseFormatId() {
        return this.baseFormatId;
    }

    public String getBaseFormat() {
        return this.baseFormat;
    }
}
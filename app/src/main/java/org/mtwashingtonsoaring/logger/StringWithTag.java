package org.mtwashingtonsoaring.logger;

/**
 * Created by Rickr on 7/23/2016.
 */
public class StringWithTag {

    public String string;
    public Object tag;

    public StringWithTag(String string, Object tag) {
        this.string = string;
        this.tag = tag;
    }

    @Override
    public String toString() {
        return string;
    }
}

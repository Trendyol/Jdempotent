package com.trendyol.jdempotent.core.model;

import java.lang.reflect.Field;

public class ChainData {
    private Field declaredField;
    private Object args;

    public ChainData(){
    }

    public ChainData(Field declaredField, Object args) {
        this.declaredField = declaredField;
        this.args = args;
    }

    public Field getDeclaredField() {
        return declaredField;
    }

    public void setDeclaredField(Field declaredField) {
        this.declaredField = declaredField;
    }

    public Object getArgs() {
        return args;
    }

    public void setArgs(Object args) {
        this.args = args;
    }
}

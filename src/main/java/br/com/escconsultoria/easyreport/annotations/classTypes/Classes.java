/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.escconsultoria.easyreport.annotations.classTypes;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author eders
 */
public enum Classes {

    STRING (String.class.getName()),
    DATE (Date.class.getName()),
    INTEGER (Integer.class.getName()),
    FLOAT (Float.class.getName()),
    LONG (Long.class.getName()),
    BIG_DECIMAL (BigDecimal.class.getName());

    private final String className;
    
    public String className() {
        return this.className;
    }

    private Classes(String className) {
        this.className = className;
    }
}

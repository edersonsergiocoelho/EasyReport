/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.escconsultoria.easyreport.annotations;

import br.com.escconsultoria.easyreport.annotations.classTypes.Classes;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author eders
 */

@Target (ElementType.FIELD)

@Retention (RetentionPolicy.RUNTIME)

public @interface ReportColumn {

    public String property() default "";

    public String title() default "Title";

    public Classes colClass() default Classes.STRING;

    public boolean groupingCriteria() default false;

    public boolean sumable() default false;
}

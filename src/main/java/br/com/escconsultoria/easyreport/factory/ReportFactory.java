/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.escconsultoria.easyreport.factory;

import ar.com.fdvs.dj.domain.AutoText;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.DynamicReport;
import ar.com.fdvs.dj.domain.builders.ColumnBuilder;
import ar.com.fdvs.dj.domain.builders.DynamicReportBuilder;
import ar.com.fdvs.dj.domain.builders.GroupBuilder;
import ar.com.fdvs.dj.domain.constants.Border;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.HorizontalAlign;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.constants.VerticalAlign;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import br.com.escconsultoria.easyreport.annotations.ReportColumn;
import ar.com.fdvs.dj.domain.Style;
import java.awt.Color;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.sf.jasperreports.engine.JasperExportManager;

/**
 *
 * @author eders
 */
public class ReportFactory<T> extends BaseDjReport<T> {
    
    private Class workingClass;
    private String title;
    private String subTitle;
    private String grandTotalTitle;
    private List<String> visibleFields = new ArrayList<String>();
    private HashMap<String, HashMap> visibleFieldsCgfs = new HashMap<String, HashMap>();

    public DynamicReport buildReport() throws Exception {

        Style detailStyle = new Style("detail");

        Style headerStyle = new Style("header");
        headerStyle.setFont(Font.ARIAL_MEDIUM_BOLD);
        headerStyle.setBorderBottom(Border.PEN_1_POINT());
        headerStyle.setBackgroundColor(new Color(94,148,93));
        headerStyle.setTextColor(Color.white);
        headerStyle.setHorizontalAlign(HorizontalAlign.CENTER);
        headerStyle.setVerticalAlign(VerticalAlign.MIDDLE);
        headerStyle.setTransparency(Transparency.OPAQUE);

        Style headerVariables = new Style("headerVariables");
        headerVariables.setFont(Font.ARIAL_BIG_BOLD);
        headerVariables.setBorderBottom(Border.THIN());
        headerVariables.setHorizontalAlign(HorizontalAlign.RIGHT);
        headerVariables.setVerticalAlign(VerticalAlign.TOP);

        Style groupVariables = new Style("groupVariables");
        groupVariables.setFont(Font.ARIAL_MEDIUM_BOLD);
        groupVariables.setTextColor(Color.BLUE);
        groupVariables.setBorderBottom(Border.THIN());
        groupVariables.setHorizontalAlign(HorizontalAlign.RIGHT);
        groupVariables.setVerticalAlign(VerticalAlign.BOTTOM);

        Style titleStyle = new Style("titleStyle");
        titleStyle.setFont(new Font(18, Font._FONT_VERDANA, true));
        Style importeStyle = new Style();
        importeStyle.setHorizontalAlign(HorizontalAlign.RIGHT);
        Style oddRowStyle = new Style();
        oddRowStyle.setBorder(Border.NO_BORDER());
        oddRowStyle.setBackgroundColor(Color.LIGHT_GRAY);
        oddRowStyle.setTransparency(Transparency.OPAQUE);
        
        DynamicReportBuilder drb = new DynamicReportBuilder();
        Integer margin = new Integer(20);
        drb.setTitleStyle(titleStyle).setTitle(getTitle()) //defines the title of the report
                .setSubtitle(getSubTitle())
                .setDetailHeight(new Integer(15))
                .setLeftMargin(margin)
                .setRightMargin(margin)
                .setTopMargin(margin)
                .setBottomMargin(margin)
                .setPrintBackgroundOnOddRows(true)
                .setOddRowBackgroundStyle(oddRowStyle);
        
        
        GroupBuilder groupBuilderSumGroupingCriteria = null;
        
        drb.setGlobalHeaderVariableHeight(new Integer(30));
        drb.setGlobalFooterVariableHeight(new Integer(30));
        for(Field f : getWorkingClass().getFields()){
            if(getVisibleFields().isEmpty()){
                if(f.isAnnotationPresent(ReportColumn.class)){
                    AbstractColumn column = null;
                    DJGroup g1 = null;
                    if(f.getAnnotation(ReportColumn.class).groupingCriteria()){
                        column = ColumnBuilder.getNew()
                            .setColumnProperty(f.getAnnotation(ReportColumn.class).property(), f.getAnnotation(ReportColumn.class).colClass().className())
                            .setTitle(f.getAnnotation(ReportColumn.class).title())
                            .setWidth(new Integer(85))
                            .setStyle(titleStyle)
                            .setHeaderStyle(titleStyle)
                            .build();

                        GroupBuilder gb1 = new GroupBuilder();
                        groupBuilderSumGroupingCriteria = gb1;

                        g1 = gb1.setCriteriaColumn((PropertyColumn) column)
                                .setGroupLayout(GroupLayout.VALUE_IN_HEADER) // tells the group how to be shown, there are manyposibilities, see the GroupLayout for more.
                                .setFooterVariablesHeight(new Integer(20))
                                .setFooterHeight(new Integer(50), true)
                                .setHeaderVariablesHeight(new Integer(35))
                                .build();
                        
                    } else {
                        column = ColumnBuilder.getNew()
                            .setColumnProperty(f.getAnnotation(ReportColumn.class).property(), f.getAnnotation(ReportColumn.class).colClass().className())
                            .setTitle(f.getAnnotation(ReportColumn.class).title())
                            .setWidth(new Integer(85))
                            .setStyle(detailStyle)
                            .setHeaderStyle(headerStyle)
                            .build();
                    }
                    if(f.getAnnotation(ReportColumn.class).sumable()){
                        DJGroupLabel dJGroupLabel = new DJGroupLabel("Valor", null);
                        groupBuilderSumGroupingCriteria.addFooterVariable(column, DJCalculation.SUM, null, null, dJGroupLabel);
                        
                        // Removendo O Valor Total Do Cabeçario
                        //drb.addGlobalHeaderVariable(column, DJCalculation.SUM, headerVariables);
                        drb.addGlobalFooterVariable(column, DJCalculation.SUM, headerVariables);
                        drb.setGrandTotalLegend(getGrandTotalTitle()).setGrandTotalLegendStyle(headerVariables);
                    }
                    drb.addColumn(column);
                    if(g1 != null) drb.addGroup(g1);
                }
            } else {
                if(getVisibleFields().contains(f.getName())){
                    AbstractColumn column = null;
                    DJGroup g1 = null;
                    if((getVisibleFieldsCgfs() != null && !getVisibleFieldsCgfs().isEmpty())
                            && getVisibleFieldsCgfs().containsKey(f.getName())){
                        boolean extGroup = false;
                        boolean extSum = false;
                        String property = "";
                        String className = "";
                        String title = "";
                        
                        if(getVisibleFieldsCgfs().get(f.getName()).containsKey("groupingCriteria"))
                            extGroup = (Boolean) getVisibleFieldsCgfs().get(f.getName()).get("groupingCriteria");
                        if(getVisibleFieldsCgfs().get(f.getName()).containsKey("sumable"))
                            extSum = (Boolean) getVisibleFieldsCgfs().get(f.getName()).get("sumable");
                        if(getVisibleFieldsCgfs().get(f.getName()).containsKey("property"))
                            property = (String) getVisibleFieldsCgfs().get(f.getName()).get("property");
                        else if(f.isAnnotationPresent(ReportColumn.class))
                            property = f.getAnnotation(ReportColumn.class).property();
                        if(getVisibleFieldsCgfs().get(f.getName()).containsKey("className"))
                            className = (String) getVisibleFieldsCgfs().get(f.getName()).get("className");
                        else if(f.isAnnotationPresent(ReportColumn.class))
                            className = f.getAnnotation(ReportColumn.class).colClass().className();
                        if(getVisibleFieldsCgfs().get(f.getName()).containsKey("title"))
                            title = (String) getVisibleFieldsCgfs().get(f.getName()).get("title");
                        else if(f.isAnnotationPresent(ReportColumn.class))
                            title = f.getAnnotation(ReportColumn.class).title();

                        if(extGroup){
                            column = ColumnBuilder.getNew()
                                .setColumnProperty(property, className)
                                .setTitle(title)
                                .setWidth(new Integer(85))
                                .setStyle(titleStyle)
                                .setHeaderStyle(titleStyle)
                                .build();

                            GroupBuilder gb1 = new GroupBuilder();

                            g1 = gb1.setCriteriaColumn((PropertyColumn) column)
                                    .setGroupLayout(GroupLayout.VALUE_IN_HEADER) // tells the group how to be shown, there are manyposibilities, see the GroupLayout for more.
                                    .setFooterVariablesHeight(new Integer(20))
                                    .setFooterHeight(new Integer(50), true)
                                    .setHeaderVariablesHeight(new Integer(35))
                                    .build();
                        } else {
                            column = ColumnBuilder.getNew()
                                .setColumnProperty(property, className)
                                .setTitle(title)
                                .setWidth(new Integer(85))
                                .setStyle(detailStyle)
                                .setHeaderStyle(headerStyle)
                                .build();
                        }
                        if(extSum){
                            drb.addGlobalHeaderVariable(column, DJCalculation.SUM, headerVariables);
                            drb.addGlobalFooterVariable(column, DJCalculation.SUM, headerVariables);
                            drb.setGrandTotalLegend(getGrandTotalTitle()).setGrandTotalLegendStyle(headerVariables);
                        }

                    } else {
                        if(f.getAnnotation(ReportColumn.class).groupingCriteria()){
                            column = ColumnBuilder.getNew()
                                .setColumnProperty(f.getAnnotation(ReportColumn.class).property(), f.getAnnotation(ReportColumn.class).colClass().className())
                                .setTitle(f.getAnnotation(ReportColumn.class).title())
                                .setWidth(new Integer(85))
                                .setStyle(titleStyle)
                                .setHeaderStyle(titleStyle)
                                .build();

                            GroupBuilder gb1 = new GroupBuilder();

                            g1 = gb1.setCriteriaColumn((PropertyColumn) column)
                                    .setGroupLayout(GroupLayout.VALUE_IN_HEADER) // tells the group how to be shown, there are manyposibilities, see the GroupLayout for more.
                                    .setFooterVariablesHeight(new Integer(20))
                                    .setFooterHeight(new Integer(50), true)
                                    .setHeaderVariablesHeight(new Integer(35))
                                    .build();
                        } else {
                            column = ColumnBuilder.getNew()
                                .setColumnProperty(f.getAnnotation(ReportColumn.class).property(), f.getAnnotation(ReportColumn.class).colClass().className())
                                .setTitle(f.getAnnotation(ReportColumn.class).title())
                                .setWidth(new Integer(85))
                                .setStyle(detailStyle)
                                .setHeaderStyle(headerStyle)
                                .build();
                        }
                        if(f.getAnnotation(ReportColumn.class).sumable()){
                            drb.addGlobalHeaderVariable(column, DJCalculation.SUM, headerVariables);
                            drb.addGlobalFooterVariable(column, DJCalculation.SUM, headerVariables);
                            drb.setGrandTotalLegend(getGrandTotalTitle()).setGrandTotalLegendStyle(headerVariables);
                        }
                    }
                    drb.addColumn(column);
                    if(g1 != null) drb.addGroup(g1);
                }
            }
        }

        drb.setUseFullPageWidth(true);
        drb.addAutoText(AutoText.AUTOTEXT_PAGE_X_SLASH_Y, AutoText.POSITION_FOOTER, AutoText.ALIGNMENT_RIGHT);

        DynamicReport dr = drb.build();
        return dr;
    }

    public void generateReport(List<T> cols, Class myClass) throws Exception {
        ReportFactory test = new ReportFactory();
        test.setWorkingClass(myClass);
        test.setColuns(cols);
        test.setTitle(getTitle());
        test.setSubTitle(getSubTitle());
        test.setGrandTotalTitle(getGrandTotalTitle());
        test.testReport();
        test.exportToJRXML();
        JasperExportManager.exportReportToPdfFile(test.jp, "groupdynamicreport.pdf");
//        JasperViewer.viewReport(test.jp);
    }

    public void generateReport(List<T> cols, List<String> visFields, Class myClass) throws Exception {
        ReportFactory test = new ReportFactory();
        test.setWorkingClass(myClass);
        test.setColuns(cols);
        test.setVisibleFields(visFields);
        test.setTitle(getTitle());
        test.setSubTitle(getSubTitle());
        test.setGrandTotalTitle(getGrandTotalTitle());
        test.testReport();
        test.exportToJRXML();
        JasperExportManager.exportReportToPdfFile(test.jp, "groupdynamicreport.pdf");
    }

    public void generateReport(List<T> cols, List<String> visFields, HashMap<String, HashMap> visFieldsCgfs, Class myClass) throws Exception {
        ReportFactory test = new ReportFactory();
        test.setWorkingClass(myClass);
        test.setColuns(cols);
        test.setVisibleFields(visFields);
        test.setVisibleFieldsCgfs(visFieldsCgfs);
        test.setTitle(getTitle());
        test.setSubTitle(getSubTitle());
        test.setGrandTotalTitle(getGrandTotalTitle());
        test.testReport();
        test.exportToJRXML();
        JasperExportManager.exportReportToPdfFile(test.jp, "groupdynamicreport.pdf");
    }

    /**
     * @return the workingClass
     */
    public Class getWorkingClass() {
        return workingClass;
    }

    /**
     * @param workingClass the workingClass to set
     */
    public void setWorkingClass(Class workingClass) {
        this.workingClass = workingClass;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the subTitle
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * @param subTitle the subTitle to set
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    /**
     * @return the grandTotalTitle
     */
    public String getGrandTotalTitle() {
        return grandTotalTitle;
    }

    /**
     * @param grandTotalTitle the grandTotalTitle to set
     */
    public void setGrandTotalTitle(String grandTotalTitle) {
        this.grandTotalTitle = grandTotalTitle;
    }

    /**
     * @return the visibleFields
     */
    public List<String> getVisibleFields() {
        if(visibleFields == null)
            return new ArrayList<String>();
        else
            return visibleFields;
    }

    /**
     * @param visibleFields the visibleFields to set
     */
    public void setVisibleFields(List<String> visibleFields) {
        this.visibleFields = visibleFields;
    }

    /**
     * @return the visibleFieldsCgfs
     */
    public HashMap<String, HashMap> getVisibleFieldsCgfs() {
        if(visibleFieldsCgfs == null)
            return new HashMap<String, HashMap>();
        else
            return visibleFieldsCgfs;
    }

    /**
     * @param visibleFieldsCgfs the visibleFieldsCgfs to set
     */
    public void setVisibleFieldsCgfs(HashMap<String, HashMap> visibleFieldsCgfs) {
        this.visibleFieldsCgfs = visibleFieldsCgfs;
    }
}

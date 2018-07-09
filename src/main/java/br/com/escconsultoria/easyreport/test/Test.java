/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.escconsultoria.easyreport.test;

import br.com.escconsultoria.easyreport.factory.ReportFactory;
import br.com.escconsultoria.easyreport.test.dao.TestRepositoryProducts;
import br.com.escconsultoria.easyreport.test.domain.Product;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author eders
 */
public class Test {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        ReportFactory<Product> rf = new ReportFactory<Product>();

        rf.setTitle("Relatorio de testes");
        rf.setSubTitle("Apenas um teste qualquer de relatorio");
        rf.setGrandTotalTitle("Totais");

        rf.generateReport(TestRepositoryProducts.getDummyCollection(), Product.class);

        List<String> fields = new ArrayList<String>();
        fields.add("state");
        fields.add("productLine");
        fields.add("quantity");
        fields.add("id"); /* adicionando um campo que não estava originalmente configurado */

        HashMap<String,Boolean> customCfg = new HashMap<String, Boolean>();
        customCfg.put("groupingCriteria", Boolean.FALSE);

        HashMap<String,Boolean> customCfg2 = new HashMap<String, Boolean>();
        customCfg2.put("sumable", Boolean.FALSE);

        HashMap<String,Boolean> customCfg3 = new HashMap<String, Boolean>();
        customCfg3.put("groupingCriteria", Boolean.FALSE);

        /* adicionando um campo que não estava originalmente configurado */
        HashMap<String,String> customMapping = new HashMap<String, String>();
        customMapping.put("property", "id");
        customMapping.put("className", Long.class.getName());
        customMapping.put("title", "ID");

        HashMap<String, HashMap> customFieldsConfs = new HashMap<String, HashMap>();
        customFieldsConfs.put("productLine", customCfg);
        customFieldsConfs.put("quantity", customCfg2);
        customFieldsConfs.put("state", customCfg3);
        customFieldsConfs.put("id", customMapping);/* adicionando um campo que não estava originalmente configurado */

        //rf.generateReport(TestRepositoryProducts.getDummyCollection(), fields, Product.class);
        //rf.generateReport(TestRepositoryProducts.getDummyCollection(), fields, customFieldsConfs, Product.class);
    }
}

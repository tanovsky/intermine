package org.intermine.bio.dataconversion;

/*
 * Copyright (C) 2002-2014 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Set;

import org.intermine.dataconversion.ItemsTestCase;
import org.intermine.dataconversion.MockItemWriter;
import org.intermine.metadata.Model;
import org.intermine.model.fulldata.Item;

public class EnsemblSnpGFF3RecordHandlerTest extends ItemsTestCase
{
    EnsemblSnpGFF3RecordHandler handler;
    GFF3Converter converter;

    MockItemWriter writer = new MockItemWriter(new LinkedHashMap<String, Item>());
    String seqClsName = "Chromosome";
    String taxonId = "9606";
    String dataSetTitle = "dbSNP data set";
    String dataSourceName = "Ensembl";

    public EnsemblSnpGFF3RecordHandlerTest(String arg) {
        super(arg);
    }

    public void setUp() throws Exception {
        Model tgtModel = Model.getInstanceByName("genomic");
        handler = new EnsemblSnpGFF3RecordHandler(tgtModel);
        EnsemblSnpGFF3RecordHandler seqHandler = new EnsemblSnpGFF3RecordHandler(tgtModel);
        converter = new GFF3Converter(writer, seqClsName, taxonId,
                dataSourceName, dataSetTitle, tgtModel,
                handler, null);
    }

    public void tearDown() throws Exception {
        converter.close();
    }

    public void testParse() throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("Homo_sapiens_incl_consequences.gvf")));
        converter.parse(reader);
        converter.storeAll();

        // uncomment to write a new items xml file
        writeItemsFile(writer.getItems(), "ensembl-snp_items.xml");

        Set<?> expected = readItemSet("EnsemblSnpGFF3RecordHandlerTest.xml");
        assertEquals(expected, writer.getItems());
    }
}

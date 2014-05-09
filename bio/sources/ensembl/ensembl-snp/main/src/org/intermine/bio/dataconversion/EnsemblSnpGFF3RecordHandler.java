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

import java.util.List;

import org.intermine.bio.io.gff3.GFF3Record;
import org.intermine.metadata.Model;
import org.intermine.xml.full.Item;

/**
 * A converter/retriever for the EnsemblSnp dataset via GFF files.
 */

public class EnsemblSnpGFF3RecordHandler extends GFF3RecordHandler
{

    /**
     * Create a new EnsemblSnpGFF3RecordHandler for the given data model.
     * @param model the model for which items will be created
     */
    public EnsemblSnpGFF3RecordHandler (Model model) {
        super(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void process(GFF3Record record) {
             Item snp = getFeature();
             List<String> dbxrefs = record.getAttributes().get("Dbxref");
             String identifier = getIdentifier(dbxrefs);
             if (identifier != null) {
                 snp.setAttribute("primaryIdentifier", identifier);
             }

             List<String> variantSeqs = record.getAttributes().get("Variant_seq");
             if (variantSeqs != null && variantSeqs.size() > 0) {
                 snp.setAttribute("variantSequence", variantSeqs.get(0));
             }
             List<String> referenceSeqs = record.getAttributes().get("Reference_seq");
             if (referenceSeqs != null && referenceSeqs.size() > 0) {
                 snp.setAttribute("referenceSequence", referenceSeqs.get(0));
             }

             List<String> variantEffects = record.getAttributes().get("Variant_effect");
             if (variantEffects == null || variantEffects.isEmpty()) {
                 return;
             }
             for (String effect : variantEffects) {
                 // Variant_effect=upstream_gene_variant 0 transcript ENST00000519787
                 String transcriptIdentifier = getTranscriptIdentifier(effect);
                 Item transcript = converter.createItem("Transcript");
                 transcript.setAttribute("primaryIdentifier", transcriptIdentifier);
                 addItem(transcript);

                 Item consequence = converter.createItem("Consequence");
                 consequence.setAttribute("description", effect);
                 consequence.setReference("transcript", transcript);
                 addItem(consequence);
             }
    }

    private String getIdentifier(List<String> xrefs) {
        if (xrefs == null) {
            return null;
        }
        for (String xref : xrefs) {
            String bits[] = xref.split(":");
            if (xref.length() == 2) {
                String identifier = bits[1];
                if (identifier != null && identifier.startsWith("rs")) {
                    return identifier;
                }
            }
        }
        return null;
    }

    private String getTranscriptIdentifier(String description) {
        if (description == null) {
            return null;
        }
        String bits[] = description.split(" ");
        for (String word : bits) {
            if (word != null && word.startsWith("ENST")) {
                return word;
            }
        }
        return null;
    }
}

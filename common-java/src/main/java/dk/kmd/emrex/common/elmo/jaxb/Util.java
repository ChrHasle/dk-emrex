/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.kmd.emrex.common.elmo.jaxb;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import https.github_com.emrex_eu.elmo_schemas.tree.v1.Elmo;
import https.github_com.emrex_eu.elmo_schemas.tree.v1.LearningOpportunitySpecification;
import lombok.extern.slf4j.Slf4j;

/**
 *
 * @author salum
 */
@Slf4j
public class Util {
    public static List<LearningOpportunitySpecification> getAllLearningOpportunities(LearningOpportunitySpecification los, List<LearningOpportunitySpecification> losList) {
        if (los != null) {
            losList.add(los);
            List<LearningOpportunitySpecification.HasPart> hasParts = los.getHasPart();
            for (LearningOpportunitySpecification.HasPart hasPart : hasParts) {
                getAllLearningOpportunities(hasPart.getLearningOpportunitySpecification(), losList);
            }
            if (hasParts != null) {
                //log.debug("deleting parts: " + hasParts.size());
                hasParts.clear();
            }
        }
        return losList;
    }

    private static Elmo getElmo(String elmoString) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("https.github_com.emrex_eu.elmo_schemas.tree.v1");
        Unmarshaller u = jc.createUnmarshaller();
        Elmo elmo = (Elmo) u.unmarshal(new StringReader(elmoString));
        return elmo;
    }

    private static String marshalElmo(Elmo elmo) throws JAXBException {
        JAXBContext jc = JAXBContext.newInstance("https.github_com.emrex_eu.elmo_schemas.tree.v1");
        StringWriter out = new StringWriter();
        Marshaller m = jc.createMarshaller();
        //m.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new  ElmoNamespaceMapper());
        m.marshal(elmo, out);
        return out.toString();
    }

    public static String virtaJAXBParser(String elmoString) {
        int elmoIndex = 0;
        try {
            Elmo elmo = getElmo(elmoString);
            List<Elmo.Report> reports = elmo.getReport();
            ArrayList<LearningOpportunitySpecification> elmoLoSList = new ArrayList<LearningOpportunitySpecification>();
            //log.debug("reports: " + reports.size());
            for (Elmo.Report report : reports) {
                ArrayList<LearningOpportunitySpecification> losList = new ArrayList<LearningOpportunitySpecification>();
                List<LearningOpportunitySpecification> tempList = report.getLearningOpportunitySpecification();
                for (LearningOpportunitySpecification los : tempList) {
                    getAllLearningOpportunities(los, losList);
                }
                //log.debug("templist size: " + tempList.size() + "; losList size: " + losList.size());
                tempList.clear();
                //log.debug("templist cleared: " + tempList.size());
                tempList.addAll(losList);
                //log.debug("templist fixed: " + tempList.size());
                elmoLoSList.addAll(losList);
            }
            for (int i = 0; i < elmoLoSList.size(); i++) {
                LearningOpportunitySpecification los = elmoLoSList.get(i);
                List<LearningOpportunitySpecification.Identifier> identifierList = los.getIdentifier();
                LearningOpportunitySpecification.Identifier elmoID = new LearningOpportunitySpecification.Identifier();
                elmoID.setType("elmo");
                elmoID.setValue(String.valueOf(elmoIndex++));
                identifierList.add(elmoID);
            }
            //log.debug("courses count: " + elmoLoSList.size());

            //log.debug(toString);
            return marshalElmo(elmo);
        } catch (JAXBException ex) {
            log.error(ex.getMessage(), ex);
        }
        return null;
    }

    public static String getCourses(String elmoString, List<String> courses) {
        try {
            Elmo elmo = getElmo(elmoString);
            List<Elmo.Report> reports = elmo.getReport();
            log.debug("reports: " + reports.size());
            for (Elmo.Report report : reports) {
                ArrayList<LearningOpportunitySpecification> losList = new ArrayList<LearningOpportunitySpecification>();
                List<LearningOpportunitySpecification> tempList = report.getLearningOpportunitySpecification();
                for (LearningOpportunitySpecification los : tempList) {
                    getAllLearningOpportunities(los, losList);
                }

                //log.debug("templist size: " + tempList.size() + "; losList size: " + losList.size());
                tempList.clear();
                //log.debug("templist cleared: " + tempList.size());
                for (LearningOpportunitySpecification spec : losList) {
                    List<LearningOpportunitySpecification.Identifier> identifiers = spec.getIdentifier();
                    for (LearningOpportunitySpecification.Identifier id : identifiers) {
                        //if ("elmo".equals(id.getType()) && courses.contains(id.getValue())) {
                            tempList.add(spec);
                        //}
                    }
                }

            }
            return marshalElmo(elmo);
        } catch (JAXBException ex) {
        	log.error(ex.getMessage(), ex);
        }
        return null;

    }
}

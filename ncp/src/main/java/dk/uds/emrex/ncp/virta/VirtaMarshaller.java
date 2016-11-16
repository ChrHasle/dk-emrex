package dk.uds.emrex.ncp.virta;

import java.io.StringWriter;

//import org.purl.net.elmo.ElmoBase.ElmoBase;
//import https.github_com.emrex_eu.elmo_schemas.tree.v1.Elmo;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;

import fi.csc.tietovaranto.emrex.ELMOOpiskelijavaihtoResponse;
import https.github_com.emrex_eu.elmo_schemas.tree.v1.Elmo;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by marko.hollanti on 13/10/15.
 */
@Slf4j
public class VirtaMarshaller {

    public static final String NAMESPACE_URI = "https://github.com/emrex-eu/elmo-schemas/tree/v1";
    public static final String LOCAL_PART = "elmo";

    public static String marshal(ELMOOpiskelijavaihtoResponse response) throws JAXBException {
        final Marshaller m = JAXBContext.newInstance(Elmo.class).createMarshaller();
        m.setProperty(Marshaller.JAXB_FRAGMENT, true);
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
        final StringWriter sw = new StringWriter();
       m.marshal(new JAXBElement<>(new QName(NAMESPACE_URI, LOCAL_PART), Elmo.class, response.getElmo()), sw);
        return sw.toString();
    }
    
}

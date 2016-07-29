package dk.kmd.emrex.common.util;

import dk.kmd.emrex.common.model.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
/**
 * Created by jpentika on 28/10/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class ShibbolethHeaderHandlerTests {

    @Mock private HttpServletRequest mockHttpServletRequest;

    public static String studentHEIOIDHeaderName = "shib-unique-code";
    public static String studentHEIOIDHeaderContent = ": urn:mace:terena.org:schac:personalUniqueCode:fi:hy.fi:x8734";

    public static String personalIdHeaderName = "shib-unique-id";
    public static String personalIdHeaderContent = "urn:mace:terena.org:schac:personalUniqueID:fi:FIC:020896-358x";

    public static String OIDheaderName = "shib-SHIB_funetEduPersonLearnerId";
    public static String OIDHeaderContent = "1.2.246.562.24.17488477125";

    public static String homeOrganisationHeaderName ="shib-SHIB_schacHomeOrganization";
    public static String homeOrganisationHeaderContent = "oamk.fi";

    public static String birthdayHeaderName = "shib-SHIB_schacDateOfBirth";
    public static String birthdayHeaderContent = "19800201";

    @Test
    public void testHeaderParsing() throws Exception {
        Mockito.when(mockHttpServletRequest.getHeader(studentHEIOIDHeaderName)).thenReturn(studentHEIOIDHeaderContent);
        Mockito.when(mockHttpServletRequest.getHeader(personalIdHeaderName)).thenReturn(personalIdHeaderContent);
        Mockito.when(mockHttpServletRequest.getHeader(OIDheaderName)).thenReturn(OIDHeaderContent);
        Mockito.when(mockHttpServletRequest.getHeader(birthdayHeaderName)).thenReturn(birthdayHeaderContent);
        Mockito.when(mockHttpServletRequest.getHeader(homeOrganisationHeaderName)).thenReturn(homeOrganisationHeaderContent);



        ShibbolethHeaderHandler handler = new ShibbolethHeaderHandler(mockHttpServletRequest);
        Person testPerson = handler.generatePerson();
        assertEquals("x8734", handler.getHeiOid());
        assertEquals("17488477125", handler.getOID());
        assertEquals("020896-358x", handler.getPersonalID());
        assertEquals(homeOrganisationHeaderContent, handler.getHomeOrganization());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        assertEquals(birthdayHeaderContent, testPerson.getBirthDate().format(formatter));

    }

}



/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.uds.emrex.ncp;

import dk.uds.emrex.ncp.saml2.WayfUser;
import dk.kmd.emrex.common.elmo.ElmoParser;
import dk.kmd.emrex.common.util.ShibbolethHeaderHandler;
import dk.uds.emrex.ncp.virta.VirtaClient;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author salum
 */
@RestController
public class JsonController {

    final static Logger log = LoggerFactory.getLogger(JsonController.class);

    @Autowired
    private HttpServletRequest context;

    @Autowired
    private StudyFetcher studyFetcher;

    @RequestMapping(value = "/elmo", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> fetchElmoXml(HttpServletRequest request) throws Exception {

        Map<String, Object> model = new HashMap<>();
        model.put("returnUrl", context.getSession().getAttribute("returnUrl"));
        model.put("sessionId", context.getSession().getAttribute("sessionId"));

        ShibbolethHeaderHandler header = new ShibbolethHeaderHandler(request);
        header.stringifyHeader();
        String OID = header.getHeiOid();
        String PersonalID = header.getPersonalID();
//        log.info("Fetching data from Virta client OID: {} PersonalID {}", OID, PersonalID);
//        model.put("elmoXml", studyFetcher.fetchStudies(OID, PersonalID));
        // TODO

        return model;
    }

    @RequestMapping(value = "/ncp/api/fullelmo", method = RequestMethod.GET)
    @ResponseBody
    public String npcGetFullElmoJSON() throws Exception {
        return this.getFullElmoJSON();
    }

    @RequestMapping(value = "/api/fullelmo", method = RequestMethod.GET)
    @ResponseBody
    public String getFullElmoJSON() throws IOException {
        log.info("getting FullELmo");

        try {
            final WayfUser user = getCurrentUser();
            final String elmo = studyFetcher.fetchStudies(user.getCpr());
            final ElmoParser parser = ElmoParser.elmoParser(elmo);

//            ElmoParser parser = (ElmoParser) context.getSession().getAttribute("elmo");
            String xmlString;
            
            xmlString = parser.getCourseData();
            log.debug(xmlString);
            JSONObject json = XML.toJSONObject(xmlString);
            return json.toString();
        } catch (Exception e) {
            log.error(e.getMessage());
            StackTraceElement elements[] = e.getStackTrace();
            Map<String, Object> error = new HashMap<String, Object>();
            Map<String, Object> log = new HashMap<String, Object>();
            error.put("message", e.getMessage());
            for (int i = 0, n = elements.length; i < n; i++) {
                log.put(elements[i].getFileName() + " " + elements[i].getLineNumber(),
                        elements[i].getMethodName());
            }
            error.put("stack", log);
            return new JSONObject(error).toString();
        }
    }

    @RequestMapping(value = "/ncp/api/elmo", method = RequestMethod.GET)
    @ResponseBody
    public String npcGetElmoJSON(@RequestParam(value = "courses", required = false) String[] courses) throws Exception {
        return this.getElmoJSON(courses);
    }

    @RequestMapping(value = "/api/elmo", method = RequestMethod.GET)
    @ResponseBody
    public String getElmoJSON(
            @RequestParam(value = "courses", required = false) String[] courses) throws Exception {
        if (courses != null) {
            String courseIdList = "";
            for (int i = 0; i < courses.length; i++) {
                courseIdList += courses[i] + ", ";
            }
            log.info("Courses: " + courseIdList);
        }

        try {

//            ElmoParser parser = (ElmoParser) context.getSession().getAttribute("elmo");
            final WayfUser user = getCurrentUser();
            final String elmo = studyFetcher.fetchStudies(user.getCpr());
            final ElmoParser parser = ElmoParser.elmoParser(elmo);

            String xmlString;
            if (courses != null) {
                log.debug("Courses count: {}", courses.length);
                List<String> courseList = Arrays.asList(courses);
                xmlString = parser.getCourseData(courseList);
            } else {
                log.debug("Courses count: null");
                xmlString = parser.getCourseData(null);
            }

            log.trace(xmlString);
            JSONObject json = XML.toJSONObject(xmlString);
            return json.toString();
        } catch (Exception e) {
            log.error("Failed to get Elmo JSON", e);
            StackTraceElement elements[] = e.getStackTrace();
            Map<String, Object> error = new HashMap<String, Object>();
            Map<String, Object> log = new HashMap<String, Object>();
            error.put("message", e.getMessage());
            for (int i = 0, n = elements.length; i < n; i++) {
                log.put(elements[i].getFileName() + " " + elements[i].getLineNumber(),
                        elements[i].getMethodName());
            }
            error.put("stack", log);
            return new JSONObject(error).toString();
        }
    }

    private WayfUser getCurrentUser() {
        return (WayfUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.learnpad.ontology.wiki;

import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Resource;
import eu.learnpad.ontology.config.APP;
import eu.learnpad.ontology.kpi.dashboard.AbstractKpiTest;
import eu.learnpad.ontology.persistence.util.OntUtil;
import eu.learnpad.ontology.recommender.Inferencer;
import eu.learnpad.ontology.recommender.RecommenderException;
import eu.learnpad.or.rest.data.NotificationActionType;
import eu.learnpad.or.rest.data.ResourceType;
import java.util.List;
import static junit.framework.Assert.*;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author sandro.emmenegger
 */
public class UserActionNotificationLogTest extends AbstractKpiTest {

    public UserActionNotificationLogTest() {
    }

    @Test
    public void testLogResourceNotification() throws RecommenderException {
        OntModel model = latestModel();
        new Inferencer(model);
        
        //test page created
        OntClass pageClass = model.getOntClass(APP.NS.XWIKI + "Page");
        OntProperty testWikiPageUriProperty = model.getOntProperty(APP.NS.XWIKI + "pageHasURL");
        String testWikiPageUri = getTestWikiPage(model).getURI();
        Literal value = model.createTypedLiteral(testWikiPageUri);
        List<Individual> pageInstancs = OntUtil.getInstancesWithProperty(model, pageClass, testWikiPageUriProperty, value);
        assertNotNull(pageInstancs);
        assertEquals(1, pageInstancs.size());
        
        Long timestamp = System.currentTimeMillis();
        Individual logEntry = createUserActionLog(MODELSET_ID, null, null, testWikiPageUri, ResourceType.PAGE, null, TEST_USER, timestamp, NotificationActionType.ADDED);
        assertNotNull(logEntry);

        pageInstancs = OntUtil.getInstancesWithProperty(model, pageClass, testWikiPageUriProperty, value);
        assertNotNull(pageInstancs);
        assertEquals(1, pageInstancs.size());
        Individual pageInstance = pageInstancs.get(0);
        assertEquals(logEntry.getURI(), pageInstance.getURI());
        
        //test page log created
        testLogCreated(model, pageInstance, "added", TEST_USER, 1);
        
        //test comment log created
        logEntry = createUserActionLog(MODELSET_ID, null, null, "1", ResourceType.COMMENT, testWikiPageUri, TEST_USER, timestamp, NotificationActionType.ADDED);
        
        OntClass commentClass = model.getOntClass(APP.NS.XWIKI + "Comment");
        OntProperty referedPageUrlProperty = model.getOntProperty(APP.NS.XWIKI + "annotationIsMadeToPage");
        List<Individual> commentInstancs = OntUtil.getInstancesWithProperty(model, commentClass, referedPageUrlProperty, pageInstance);

        assertNotNull(commentInstancs);
        assertEquals(1, commentInstancs.size());

        //test comment log created
        testLogCreated(model, commentInstancs.get(0), "added", TEST_USER, 1);
    }

    @After
    public void cleanUpTestEntries(){
        super.cleanUpUserActionLogs();
    }

    private void testLogCreated(OntModel model, Individual instance, String actionType, String userId, int expected) {
        
        OntClass actionHistoryClass = model.getOntClass(APP.NS.XWIKI + "ActionHistory");
        OntProperty actionAppliedToResourceProp = model.getOntProperty(APP.NS.XWIKI + "actionAppliedToResource");
        List<Individual> resourceHistInstancs = OntUtil.getInstancesWithProperty(model, actionHistoryClass, actionAppliedToResourceProp, instance);

        assertNotNull(resourceHistInstancs);
        assertEquals(expected, resourceHistInstancs.size());
        Individual resourceHistInstance = resourceHistInstancs.get(0);
        OntProperty actionTypeProp = model.getOntProperty(APP.NS.XWIKI + "actionType");
        assertTrue(resourceHistInstance.hasProperty(actionTypeProp));
        assertEquals(actionType, resourceHistInstance.getPropertyValue(actionTypeProp).asLiteral().getString());

        //Test if user assigned to history
        if (userId != null) {
            OntProperty userProp = model.getOntProperty(APP.NS.XWIKI + "user");
            assertTrue(resourceHistInstance.hasProperty(userProp));
            Resource user = resourceHistInstance.getPropertyResourceValue(userProp);
            Individual userInstance = user.as(Individual.class);
            assertNotNull(userInstance);
            OntProperty emailProp = model.getOntProperty(APP.NS.EMO + "performerHasEmailAddress");
            assertNotNull(emailProp);
            assertTrue(userInstance.hasProperty(emailProp));
            assertEquals(userId, userInstance.getPropertyValue(emailProp).asLiteral().getString());
        }

    }
    

}

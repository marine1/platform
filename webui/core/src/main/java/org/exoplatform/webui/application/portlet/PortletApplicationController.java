/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.application.portlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.GenericPortlet;
import javax.portlet.PortletConfig;
import javax.portlet.PortletContext;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.web.WebAppController;
/**
 * Created by The eXo Platform SAS
 * May 8, 2006
 */
public class PortletApplicationController extends GenericPortlet {
  
  protected static Log log = ExoLogger.getLogger("portlet:PortletApplicationController"); 
  
  private String applicationId_ ;
  
  /**
   * This method is called when the portlet is initialised, in eXo this is a lazy loading
   * mechanism
   * 
   * the main goal of this method is to generate an application ID
   *         applicationId_  = portlet-application-name + "/" + portlet-name 
   */
  public void init(PortletConfig config) throws PortletException {
    super.init(config) ;
    PortletContext pcontext = config.getPortletContext();
    String contextName = pcontext.getPortletContextName();
    applicationId_  = contextName + "/" + config.getPortletName() ;
  }
  
  /**
   * Delegate the action to the PortletApplication object
   */
  public void processAction(ActionRequest req, ActionResponse res) throws PortletException, IOException {
    try {
      getPortletApplication().processAction(req, res) ;
    } catch(Exception ex) {
      log.error("Error while processing action in the porlet", ex);
    }
  }
  
  /**
   * Delegate the render to the PortletApplication object
   */  
  public  void render(RenderRequest req,  RenderResponse res) throws PortletException, IOException {
    try {
      getPortletApplication().render(req, res) ;
    } catch(Exception ex) {
      log.error("Error while rendering the porlet", ex);
    }
  }
  
  /**
   * try to obtain the PortletApplication from the WebAppController.
   * 
   * If it does not exist a new PortletApplication object is created, init and cached in the
   * controller
   */
  private PortletApplication getPortletApplication() throws Exception {
    PortalContainer container = PortalContainer.getInstance() ;
    WebAppController controller = 
      (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
    PortletApplication application = controller.getApplication(applicationId_) ;
    if(application == null) {
      application = new PortletApplication(getPortletConfig()) ;
      application.onInit() ; 
      controller.addApplication(application) ;
    }
    return application ;
  }
  
  /**
   * When the portlet is destroyed by the portlet container, the onDestroy() method of the
   * PortletApplication is called and then the PortletApplication is removed from the cache
   * inside th WebController
   */
  @SuppressWarnings("unchecked")
  public void destroy() {
    RootContainer rootContainer =  RootContainer.getInstance() ;
    List<PortalContainer> containers = 
      rootContainer.getComponentInstancesOfType(PortalContainer.class) ;
    try {
      for(PortalContainer container : containers) {
        PortalContainer.setInstance(container) ;
        WebAppController controller = 
          (WebAppController)container.getComponentInstanceOfType(WebAppController.class) ;
        PortletApplication application = controller.getApplication(applicationId_) ;
        if(application != null) {
          application.onDestroy() ;
          controller.removeApplication(applicationId_) ;
        }
      }
    } catch(Exception ex) {
      log.error("Error while destroying the porlet", ex);
    }
  }
}
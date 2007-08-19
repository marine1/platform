/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.webui.core.lifecycle;

import org.exoplatform.webui.application.WebuiRequestContext;
import org.exoplatform.webui.core.UIComponent;
import org.exoplatform.webui.core.UIContainer;

/**
 * Jul 10, 2006
 */
public class UIContainerLifecycle extends Lifecycle {
  
  @SuppressWarnings("unused")
  public void processRender(UIComponent uicomponent , WebuiRequestContext context) throws Exception {
    context.getWriter().append("<div class=\"").append(uicomponent.getId()).append("\" id=\"").append(uicomponent.getId()).append("\">");
    UIContainer uiContainer = (UIContainer) uicomponent;
    uiContainer.renderChildren(context);
    context.getWriter().append("</div>");
  }
}

<%@ page language="java" %>
<%@ page errorPage="/common/Error.jsp" %>
<%@ taglib uri="struts-html-el" prefix="html" %>
<%@ taglib uri="struts-tiles" prefix="tiles" %>
<%@ taglib uri="jstl-fmt" prefix="fmt" %>
<%@ taglib uri="jstl-c" prefix="c" %>
<%@ taglib uri="hq" prefix="hq" %>
<%--
  NOTE: This copyright does *not* cover user programs that use HQ
  program services by normal system calls through the application
  program interfaces provided as part of the Hyperic Plug-in Development
  Kit or the Hyperic Client Development Kit - this is merely considered
  normal use of the program, and does *not* fall under the heading of
  "derived work".
  
  Copyright (C) [2004, 2005, 2006], Hyperic, Inc.
  This file is part of HQ.
  
  HQ is free software; you can redistribute it and/or modify
  it under the terms version 2 of the GNU General Public License as
  published by the Free Software Foundation. This program is distributed
  in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
  even the implied warranty of MERCHANTABILITY or FITNESS FOR A
  PARTICULAR PURPOSE. See the GNU General Public License for more
  details.
  
  You should have received a copy of the GNU General Public License
  along with this program; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
  USA.
 --%>
 <link rel=stylesheet href="<html:rewrite page="/css/customCSS.css"/>" type="text/css">
<script src="<html:rewrite page="/js/rico.js"/>" type="text/javascript"></script>
<script src="<html:rewrite page="/js/popup.js"/>" type="text/javascript"></script>
<script src="<html:rewrite page="/js/"/>diagram.js" type="text/javascript"></script>
<script language="JavaScript" type="text/javascript">
  var help = "<hq:help/>";
          
 function getUpdateStatus(opt) {
   if (opt=="<fmt:message key="header.Acknowledge"/>") {
     var pars =  "update=true";
     var updateUrl = 'Dashboard.do?';
     var url = updateUrl + pars;
     //window.location = url;
       new Ajax.Request( url, {method: 'post'} );
       $('hb').innerHTML = '<html:img page="/images/spacer.gif" width="1" height="1" alt="" border="0"/>'
      }
     menuLayers.hide();
   }

</script>
<div class="headerWrapper">
<div style="position:absolute;left:0px;top:1px;width:225px;height:60px;">
    <html:link action="/Dashboard">
        <c:choose>
            <c:when test="${applicationScope.largeLogo}">
                <html:img page="/customer/${applicationScope.largeLogoName}" width="225" height="31" alt=""
                          border="0"/>
               
                <html:img page="/images/cobrand_logo.gif" width="225" height="25" alt="" border="0"/>
            </c:when>
            <c:otherwise>
                <html:img page="/images/newLogo18.gif" width="203" height="60" alt="" border="0" />
            </c:otherwise>
        </c:choose>
    </html:link>
</div>

<div class="headRightWrapper">
<div class="headTopNav">
            <div class="headUsrName">
                <c:out value="${sessionScope.webUser.username}"/>
                &nbsp;-&nbsp;
                <html:link action="/Logout">
                    <fmt:message key="admin.user.generalProperties.Logout"/>
                </html:link>
            </div>
            <div class="headAlertWrapper">
                            <div style="float:left;display:inline;padding-top:4px;"><fmt:message key="header.RecentAlerts"/> :</div> <div id="recentAlerts"></div>
                            <div style="height:1px;width:1px;clear:both;"><html:img page="/images/spacer.gif" border="0" width="1" height="1"/></div>
            </div>
     <div style="height:1px;width:1px;clear:both;"><html:img page="/images/spacer.gif" border="0" width="1" height="1"/></div>
</div>

<div class="headBotNav">

    <ul id="navigationTbl">
        <li onmouseover="this.style.backgroundColor='#60a5ea';" onmouseout="this.style.backgroundColor='#336699';">

            <html:link page="/Dashboard.do"><fmt:message key="dash.home.PageTitle"/></html:link>

        </li>

        <li onmouseover="this.style.backgroundColor='#60a5ea';" onmouseout="this.style.backgroundColor='#336699';">

        <html:link page="/ResourceHub.do"><fmt:message key="resource.hub.ResourceHubPageTitle"/></html:link>

        </li>
         <c:if test="${not empty mastheadAttachments}"><c:forEach var="attachment" items="${mastheadAttachments}">
             <li onmouseover="this.style.backgroundColor='#60a5ea';" onmouseout="this.style.backgroundColor='#336699';">

             <html:link action="/mastheadAttach" paramId="id" paramName="attachment" paramProperty="id"><c:out value="${attachment.view.description}"/></html:link>

             </li>
         </c:forEach></c:if>
        <li onmouseover="this.style.backgroundColor='#60a5ea';" onmouseout="this.style.backgroundColor='#336699';">

        <html:link action="/Admin"><fmt:message key="admin.admin.AdministrationTitle"/></html:link>

        </li>
        <li onmouseover="this.style.backgroundColor='#60a5ea';" onmouseout="this.style.backgroundColor='#336699';">
            <a href="." onclick="toggleMenu('recent');return false;"><span  id="recentImg"><fmt:message key=".dashContent.recentResources"/></span></a><tiles:insert definition=".toolbar.recentResources"/>
            </li>
        
        <li onmouseover="this.style.backgroundColor='#60a5ea';" onmouseout="this.style.backgroundColor='#336699';">

        <html:link href="." onclick="helpWin=window.open(help,'help','width=800,height=650,scrollbars=yes,toolbar=yes,left=80,top=80,resizable=yes');helpWin.focus();return false;">
            <fmt:message key="common.label.Help"/></html:link>

        </li>
        
        </ul>

    <div style="display:none;position:absolute;right:5px;bottom:2px;" id="loading">
        <html:img page="/images/ajax-loader.gif" border="0" width="16" height="16"/>
    </div>
    <c:if test="${not empty HQUpdateReport}">
        <div style="position:absolute;right:26px;bottom:2px;" id="hb">
            <html:img page="/images/transmit2.gif" border="0" width="16" height="16"
                      onmouseover="menuLayers.show('update', event)" onmouseout="menuLayers.hide()"/>
        </div>
  </c:if>
</div>

    </div>
</div>

<c:if test="${not empty HQUpdateReport}">
<div id="update" class="menu" style="border:1px solid black;padding-top:15px;padding-bottom:15px;font-weight:bold;font-size:12px;">
<c:out value="${HQUpdateReport}" escapeXml="false"/>
    <form name="updateForm" action="">
        <div style="text-align:center;padding-left:15px;padding-right:15px;"><input type="button" value="<fmt:message key="header.RemindLater"/>" onclick="getUpdateStatus(this.value);"><span style="padding-left:15px;"><input type="button" value="<fmt:message key="header.Acknowledge"/>" onclick="getUpdateStatus(this.value);"></span>
        </div>

    </form>
</div>
</c:if>

<script language="JavaScript1.2">
      <!--
      var refreshCount = 0;
      var autoLogout = true;
                                                                                    
      function refreshAlerts() {
        refreshCount++;

        new Ajax.Request('<html:rewrite page="/common/RecentAlerts.jsp"/>',
                         {method: 'get', onSuccess:showRecentAlertResponse});
      }

      function showRecentAlertResponse(originalRequest) {
        if (originalRequest.responseText.indexOf('recentAlertsText') > 0) {
          $('recentAlerts').innerHTML = originalRequest.responseText;
        }
        else {
          refreshCount = 31;
        }

        if (refreshCount < 30) {
          setTimeout( "refreshAlerts()", 60*1000 );
        } else if (autoLogout) {
          top.location.href = "<html:rewrite action="/Logout"/>";
        }
      }

      onloads.push( refreshAlerts );
      //-->
      </script>

/**
 * NOTE: This copyright does *not* cover user programs that use Hyperic
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 *  "derived work".
 *
 *  Copyright (C) [2010], VMware, Inc.
 *  This file is part of Hyperic.
 *
 *  Hyperic is free software; you can redistribute it and/or modify
 *  it under the terms version 2 of the GNU General Public License as
 *  published by the Free Software Foundation. This program is distributed
 *  in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 *  even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 *  PARTICULAR PURPOSE. See the GNU General Public License for more
 *  details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 *  USA.
 *
 */
package org.hyperic.hq.web.admin.managers;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


import org.hyperic.hq.appdef.Agent;
import org.hyperic.hq.appdef.server.session.AgentPluginStatus;
import org.hyperic.hq.appdef.server.session.AgentPluginStatusEnum;
import org.hyperic.hq.appdef.server.session.Platform;
import org.hyperic.hq.appdef.server.session.PlatformType;
import org.hyperic.hq.appdef.shared.AgentManager;
import org.hyperic.hq.bizapp.shared.AppdefBoss;
import org.hyperic.hq.bizapp.shared.AuthzBoss;
import org.hyperic.hq.product.shared.PluginManager;
import org.hyperic.hq.web.BaseControllerTest;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.hyperic.hq.product.Plugin;

/**
 * @author Annie Chen
 *
 */
public class PluginManagerControllerTest extends BaseControllerTest {
    private PluginManagerController pluginManagerController;
    private PluginManager mockPluginManager;
    private AgentManager mockAgentManager;
    private AppdefBoss mockAppdefBoss;
    private AuthzBoss mockAuthzBoss;
    private HttpServletRequest mockHttpServletRequest;
    private static SimpleDateFormat format;
    private static Date date1 = new Date();
    private static Date date2 = new Date();
    
    @BeforeClass
    public static void beforeClass(){
        format = new SimpleDateFormat("MM/dd/yyyy hh:mm aa zzz");
        try {
            date1 = format.parse("01/15/2011 06:00 PM PST");
            date2 = format.parse("01/01/2010 06:30 PM PST");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
    @Before
    public void setup(){
        super.setUp();
        mockPluginManager = createMock(PluginManager.class);
        mockAgentManager = createMock(AgentManager.class);
        mockAppdefBoss = getMockAppdefBoss();
        mockAuthzBoss = getMockAuthzBoss();
        mockHttpServletRequest = createMock(HttpServletRequest.class);
        pluginManagerController = new PluginManagerController(mockAppdefBoss, mockAuthzBoss, 
            mockPluginManager, mockAgentManager);

    }
    
    @Test
    public void testMechanismOff(){
        Model model =new ExtendedModelMap();
        expect(mockPluginManager.getAllPlugins()).andStubReturn(new ArrayList<Plugin>());
        expect(mockPluginManager.getPluginRollupStatus()).andStubReturn(new HashMap<Integer, Map<AgentPluginStatusEnum, Integer>>());
        expect(mockPluginManager.isPluginSyncEnabled()).andStubReturn(false);
        expect(mockPluginManager.getCustomPluginDir()).andStubReturn(new File("/root/hq/test"));
        expect(mockAgentManager.getAgents()).andStubReturn(getAgents());
        expect(mockAgentManager.getNumAutoUpdatingAgents()).andReturn(Long.parseLong("0"));

        replay(mockAgentManager);
        replay(mockPluginManager);
        
        String toPage = pluginManagerController.index(model);
        Map<String,Object> map = model.asMap();
        List<Map<String, Object>> summaries = (List<Map<String, Object>>)map.get("pluginSummaries");
        Map<String, Object> info = (Map<String, Object>)map.get("info");
        
        assertEquals("should be direct to admin/managers/plugin page.", "admin/managers/plugin",toPage);
        assertEquals("pluginSummaries size should be 0",0,summaries.size());
        assertEquals("all agent count should be 0",Long.valueOf(0),info.get("allAgentCount"));
        assertTrue("mechanismOn should be false", !(Boolean)map.get("mechanismOn"));
        assertEquals("instruction should be admin.managers.plugin.mechanism.off",
            "admin.managers.plugin.mechanism.off",
            map.get("instruction"));
    }
    
    @Test
    public void testMechanismOn(){
        Model model =new ExtendedModelMap();
        
        expect(mockPluginManager.getAllPlugins()).andStubReturn(getAllPlugins());
        expect(mockPluginManager.getPluginRollupStatus()).andStubReturn(getPluginRollupStatus());
        expect(mockPluginManager.isPluginSyncEnabled()).andStubReturn(true);
        expect(mockPluginManager.getCustomPluginDir()).andStubReturn(new File("/root/hq/test"));
        expect(mockAgentManager.getAgents()).andStubReturn(getAgents());
        expect(mockAgentManager.getNumAutoUpdatingAgents()).andStubReturn(Long.parseLong("3"));
        replay(mockAgentManager);
        replay(mockPluginManager);
        
        String toPage = pluginManagerController.index(model);
        Map<String,Object> map = model.asMap();
        Map<String, Object> info = (Map<String, Object>)map.get("info");
        
        assertEquals("should be direct to admin/managers/plugin page.", "admin/managers/plugin",toPage);
        assertTrue("mechanismOn should be true", (Boolean)map.get("mechanismOn"));
        assertEquals("all agent count should be 3",Long.valueOf(3),info.get("allAgentCount"));
        assertEquals("instruction should be admin.managers.plugin.instructions",
            "admin.managers.plugin.instructions", map.get("instruction"));
        assertEquals("file path should be /root/hq/test","/root/hq/test",""+map.get("customDir"));
    }
    
    @Test
    public void testAgentSummary(){
        expect(mockAgentManager.getAgents()).andReturn(getAgents());
        replay(mockAgentManager);
        
        List<String> summaries = pluginManagerController.getAgentStatusSummary();
        
        assertEquals("There should be only two agents in summary",2,summaries.size());
        assertEquals("agentX's name should be 3.3.3.3","3.3.3.3",summaries.get(0));
        assertEquals("agentZ's name should be 5.5.5.5","5.5.5.5",summaries.get(1));
    }
    
    private List<Agent> getAgents(){
        List<Agent> result = new ArrayList<Agent>();
        
        Agent agentX = new Agent();
        Collection<AgentPluginStatus> pluginStatusX = new ArrayList<AgentPluginStatus>();
        
        AgentPluginStatus apXA = new AgentPluginStatus();
        apXA.setLastSyncStatus(AgentPluginStatusEnum.SYNC_FAILURE.toString());
        apXA.setPluginName("plugin-a");
        apXA.setLastSyncAttempt(0);
        pluginStatusX.add(apXA);

        AgentPluginStatus apXB = new AgentPluginStatus();
        apXB.setLastSyncStatus(AgentPluginStatusEnum.SYNC_FAILURE.toString());
        apXB.setPluginName("plugin-b");
        apXB.setLastSyncAttempt(10);
        pluginStatusX.add(apXB);
        
        AgentPluginStatus apXC = new AgentPluginStatus();
        apXC.setLastSyncStatus(AgentPluginStatusEnum.SYNC_IN_PROGRESS.toString());
        apXC.setPluginName("plugin-c");
        apXC.setLastSyncAttempt(100);
        pluginStatusX.add(apXC);
        
        agentX.setPluginStatuses(pluginStatusX);
        
        Agent agentY = new Agent();
        Collection<AgentPluginStatus> pluginStatusY = new ArrayList<AgentPluginStatus>();
        
        AgentPluginStatus apYA = new AgentPluginStatus();
        apYA.setLastSyncStatus(AgentPluginStatusEnum.SYNC_SUCCESS.toString());
        apYA.setPluginName("plugin-a");
        apYA.setLastSyncAttempt(0);
        pluginStatusY.add(apYA);

        AgentPluginStatus apYB = new AgentPluginStatus();
        apYB.setLastSyncStatus(AgentPluginStatusEnum.SYNC_SUCCESS.toString());
        apYB.setPluginName("plugin-b");
        apYB.setLastSyncAttempt(10);
        pluginStatusY.add(apYB);
        
        AgentPluginStatus apYC = new AgentPluginStatus();
        apYC.setLastSyncStatus(AgentPluginStatusEnum.SYNC_SUCCESS.toString());
        apYC.setPluginName("plugin-c");
        apYC.setLastSyncAttempt(100);
        pluginStatusY.add(apYC);
        
        agentY.setPluginStatuses(pluginStatusY);        
        
        Agent agentZ = new Agent();
        Collection<AgentPluginStatus> pluginStatusZ = new ArrayList<AgentPluginStatus>();
        
        AgentPluginStatus apZA = new AgentPluginStatus();
        apZA.setLastSyncStatus(AgentPluginStatusEnum.SYNC_SUCCESS.toString());
        apZA.setPluginName("plugin-a");
        apZA.setLastSyncAttempt(0);
        pluginStatusZ.add(apZA);

        AgentPluginStatus apZB = new AgentPluginStatus();
        apZB.setLastSyncStatus(AgentPluginStatusEnum.SYNC_IN_PROGRESS.toString());
        apZB.setPluginName("plugin-b");
        apZB.setLastSyncAttempt(10);
        pluginStatusZ.add(apZB);
        
        AgentPluginStatus apZC = new AgentPluginStatus();
        apZC.setLastSyncStatus(AgentPluginStatusEnum.SYNC_FAILURE.toString());
        apZC.setPluginName("plugin-c");
        apZC.setLastSyncAttempt(100);
        pluginStatusZ.add(apZC);
        
        agentZ.setPluginStatuses(pluginStatusZ);    
        
        Collection <Platform> platformsX = new ArrayList<Platform>();
        agentX.setPlatforms(platformsX);
        agentX.setAddress("3.3.3.3");
        
        Collection <Platform> platformsY = new ArrayList<Platform>();
        agentY.setPlatforms(platformsY);
        agentY.setAddress("4.4.4.4");        
        
        Collection <Platform> platformsZ = new ArrayList<Platform>();
        Platform platformZ1 = new Platform();
        PlatformType platformType = new PlatformType("platformName-Z","plugin");
        
        platformZ1.setPlatformType(platformType);
        platformZ1.setFqdn("agentZ");
        platformsZ.add(platformZ1);
        agentZ.setPlatforms(platformsZ);
        agentZ.setAddress("5.5.5.5");   

        
        result.add(agentX);
        result.add(agentY);
        result.add(agentZ);
        return result;
    }

    
    @Test
    public void testInfoNull(){
        expect(mockAgentManager.getAgents()).andStubReturn(null);
        expect(mockAgentManager.getNumAutoUpdatingAgents()).andReturn(Long.parseLong("0"));
        replay(mockAgentManager);
        
        Map<String, Object> result = pluginManagerController.getAgentInfo();
        
        assertEquals("agentErrorCount should be 0",0,result.get("agentErrorCount"));
        assertEquals("allAgentCount should be 0",Long.valueOf("0"),result.get("allAgentCount"));
    }
    
    @Test
    public void testInfo(){
        expect(mockAgentManager.getAgents()).andStubReturn(getAgents());
        expect(mockAgentManager.getNumAutoUpdatingAgents()).andReturn(Long.parseLong("3"));
        replay(mockAgentManager);

        Map<String, Object> result = pluginManagerController.getAgentInfo();
        
        assertEquals("allAgentCount should be 3",Long.valueOf("3"),result.get("allAgentCount"));
        assertEquals("agentErrorCount should be 2",2,result.get("agentErrorCount"));
    }
    
    @Test
    public void testPluginSummaries(){
        expect(mockPluginManager.getAllPlugins()).andReturn(getAllPlugins());
        expect(mockPluginManager.getPluginRollupStatus()).andReturn(getPluginRollupStatus());
        replay(mockPluginManager);
 
        List<Map<String, Object>> summaries = pluginManagerController.getPluginSummaries();
        
        assertEquals("plugin-a: inProgressAgentCount should be 0",0,summaries.get(0).get("inProgressAgentCount"));
        assertEquals("plugin-a: allAgentCount should be 101",101,summaries.get(0).get("allAgentCount"));
        assertEquals("plugin-a: successAgentCount should be 100",100,summaries.get(0).get("successAgentCount"));
        assertEquals("plugin-a: errorAgentCount should be 1",1,summaries.get(0).get("errorAgentCount"));
        assertEquals("plugin-a: inProgress should be false",false,(Boolean)summaries.get(0).get("inProgress"));
        assertEquals("plugin-a: updatedDate should be ...",format.format(date1),""+summaries.get(0).get("updatedDate"));
        assertEquals("plugin-a: initialDeployDate should be ...",format.format(date2),""+summaries.get(0).get("initialDeployDate"));
        assertEquals("plugin-a: id should be 1",1,summaries.get(0).get("id"));
        assertEquals("plugin-a: name should be plugin-a","plugin-a",summaries.get(0).get("name"));
        
        //make sure plugins are sorted by status
        assertEquals("plugin-c: name should be plugin-c","plugin-c",summaries.get(1).get("name"));
        assertEquals("plugin-c: inProgressAgentCount should be 4",4,summaries.get(1).get("inProgressAgentCount"));
        assertEquals("plugin-c: allAgentCount should be 7",7,summaries.get(1).get("allAgentCount"));
        assertEquals("plugin-c: successAgentCount should be 0",0,summaries.get(1).get("successAgentCount"));
        assertEquals("plugin-c: errorAgentCount should be 3",3,summaries.get(1).get("errorAgentCount"));
        assertEquals("plugin-c: inProgress should be true",true,(Boolean)summaries.get(1).get("inProgress"));
        assertEquals("plugin-c: id should be 3",3,summaries.get(1).get("id"));        

        assertEquals("plugin-b: name should be plugin-b","plugin-b",summaries.get(2).get("name"));
        assertEquals("plugin-b: inProgressAgentCount should be 0",0,summaries.get(2).get("inProgressAgentCount"));
        assertEquals("plugin-b: allAgentCount should be 99",99,summaries.get(2).get("allAgentCount"));
        assertEquals("plugin-b: successAgentCount should be 99",99,summaries.get(2).get("successAgentCount"));
        assertEquals("plugin-b: errorAgentCount should be 0",0,summaries.get(2).get("errorAgentCount"));
        assertEquals("plugin-b: inProgress should be false",false,(Boolean)summaries.get(2).get("inProgress"));
        assertEquals("plugin-b: id should be 2",2,summaries.get(2).get("id"));        
    }
    
    @Test
    public void testAgentStatus(){
        
        expect(mockPluginManager.getStatusesByPluginId(3, AgentPluginStatusEnum.SYNC_FAILURE)).andStubReturn(getErrorAgentStatusList());
        expect(mockPluginManager.getStatusesByPluginId(3, AgentPluginStatusEnum.SYNC_IN_PROGRESS)).andStubReturn(getInProgressAgentStatusList());
        replay(mockPluginManager);
        List<Map<String, Object>> result = pluginManagerController.getAgentStatus(3, "");
    }
    
    @Test
    public void testAgentStatusWithKeyword(){
        expect(mockPluginManager.getStatusesByPluginId(3, AgentPluginStatusEnum.SYNC_FAILURE)).andStubReturn(getErrorAgentStatusList());
        expect(mockPluginManager.getStatusesByPluginId(3, AgentPluginStatusEnum.SYNC_IN_PROGRESS)).andStubReturn(getInProgressAgentStatusList());
        replay(mockPluginManager);
        List<Map<String, Object>> result =  pluginManagerController.getAgentStatus(3, "xx");
        
        assertEquals("result should be empty",0,result.size());
    }   
    
    private Collection<AgentPluginStatus> getErrorAgentStatusList(){
        Collection<AgentPluginStatus> result = new ArrayList<AgentPluginStatus>();
        
        AgentPluginStatus apCZ = new AgentPluginStatus();
        Agent agentZ = new Agent();
        Collection <Platform> platformsX = new ArrayList<Platform>();
        agentZ.setPlatforms(platformsX);
        agentZ.setAddress("5.5.5.5");
        apCZ.setAgent(agentZ);
        apCZ.setLastSyncAttempt(date1.getTime());
        result.add(apCZ);

        return result;
    }
    private Collection<AgentPluginStatus> getInProgressAgentStatusList(){
        Collection<AgentPluginStatus> result = new ArrayList<AgentPluginStatus>();

        AgentPluginStatus apCX = new AgentPluginStatus();
        Agent agentX = new Agent();
        Collection <Platform> platformsX = new ArrayList<Platform>();
        agentX.setPlatforms(platformsX);
        agentX.setAddress("3.3.3.3");
        apCX.setAgent(agentX);
        apCX.setLastSyncAttempt(date2.getTime());        
        
        result.add(apCX);
        return result;
    }    
    private HashMap<Integer, Map<AgentPluginStatusEnum, Integer>> getPluginRollupStatus(){
        HashMap<Integer, Map<AgentPluginStatusEnum, Integer>> rollupStatus = 
            new HashMap<Integer, Map<AgentPluginStatusEnum, Integer>>();
        Map<AgentPluginStatusEnum, Integer> pluginA = new HashMap<AgentPluginStatusEnum, Integer>();
        pluginA.put(AgentPluginStatusEnum.SYNC_SUCCESS, 100);
        pluginA.put(AgentPluginStatusEnum.SYNC_IN_PROGRESS, 0);
        pluginA.put(AgentPluginStatusEnum.SYNC_FAILURE, 1);

        Map<AgentPluginStatusEnum, Integer> pluginB = new HashMap<AgentPluginStatusEnum, Integer>();
        pluginB.put(AgentPluginStatusEnum.SYNC_SUCCESS, 99);
        pluginB.put(AgentPluginStatusEnum.SYNC_IN_PROGRESS, 0);
        pluginB.put(AgentPluginStatusEnum.SYNC_FAILURE, 0);
  
        Map<AgentPluginStatusEnum, Integer> pluginC = new HashMap<AgentPluginStatusEnum, Integer>();
        pluginC.put(AgentPluginStatusEnum.SYNC_SUCCESS, 0);
        pluginC.put(AgentPluginStatusEnum.SYNC_IN_PROGRESS, 4);
        pluginC.put(AgentPluginStatusEnum.SYNC_FAILURE, 3);
        
        rollupStatus.put(1, pluginA);
        rollupStatus.put(2, pluginB);
        rollupStatus.put(3, pluginC);
        
        return rollupStatus;
    }
    private Long getNumAutoUpdatingAgents(){
        return Long.valueOf(3);
    }
    private List<Plugin> getAllPlugins(){
        List<Plugin> plugins = new ArrayList<Plugin>();
        Plugin pluginA = new Plugin();
        pluginA.setName("plugin-a");
        pluginA.setId(1);
        pluginA.setModifiedTime(date1.getTime());
        pluginA.setCreationTime(date2.getTime());
        plugins.add(pluginA);
        
        Plugin pluginB = new Plugin();
        pluginB.setName("plugin-b");
        pluginB.setId(2);
        plugins.add(pluginB);
        
        Plugin pluginC = new Plugin();
        pluginC.setName("plugin-c");
        pluginC.setId(3);
        plugins.add(pluginC);
        
        return plugins;
    }
    
}

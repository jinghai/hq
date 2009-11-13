/*
 * Generated by XDoclet - Do not edit!
 */
package org.hyperic.hq.autoinventory.shared;

import javax.ejb.CreateException;
import javax.ejb.FinderException;

import org.hyperic.hq.appdef.shared.AppdefEntityID;
import org.hyperic.hq.authz.server.session.AuthzSubject;
import org.hyperic.hq.autoinventory.AIHistory;
import org.hyperic.hq.autoinventory.AISchedule;
import org.hyperic.hq.autoinventory.AutoinventoryException;
import org.hyperic.hq.autoinventory.DuplicateAIScanNameException;
import org.hyperic.hq.autoinventory.ScanConfigurationCore;
import org.hyperic.hq.scheduler.ScheduleValue;
import org.hyperic.hq.scheduler.ScheduleWillNeverFireException;
import org.hyperic.util.pager.PageControl;
import org.hyperic.util.pager.PageList;

/**
 * Local interface for AIScheduleManager.
 */
public interface AIScheduleManager
{
   /**
    * Schedule an AI scan on an appdef entity (platform or group of platforms)
    */
   public void doScheduledScan( AuthzSubject subject,AppdefEntityID id,ScanConfigurationCore scanConfig,String scanName,String scanDesc,ScheduleValue schedule ) throws AutoinventoryException, CreateException, DuplicateAIScanNameException, ScheduleWillNeverFireException;

   /**
    * Get a list of scheduled scans based on appdef id
    */
   public PageList<AIScheduleValue> findScheduledJobs( AuthzSubject subject,AppdefEntityID id,PageControl pc ) throws FinderException;

   public AISchedule findScheduleByID( AuthzSubject subject,Integer id ) throws FinderException, CreateException;

   /**
    * Get a job history based on appdef id
    */
   public PageList<AIHistory> findJobHistory( AuthzSubject subject,AppdefEntityID id,PageControl pc ) throws FinderException;

   public void deleteAIJob( AuthzSubject subject,java.lang.Integer[] ids ) throws AutoinventoryException;

}

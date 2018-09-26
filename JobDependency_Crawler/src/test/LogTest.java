/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 * @author kesav
 */

public class LogTest 
{
    static Logger logger = Logger.getRootLogger();
    public static void main(String args[])
    {
        String log4jLocation = "resources/log4j.properties";
	PropertyConfigurator.configure(log4jLocation);
        logger=Logger.getLogger("JilUtilities.main");
        logger.info("test message");
    }
    
}

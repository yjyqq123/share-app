package com.soft1851.contentcenter.configuration;

import org.springframework.cloud.netflix.ribbon.RibbonClients;
import ribbonconfig.RibbonConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Configuration;

@Configuration
//@RibbonClient(name = "user-center",configuration = RibbonConfiguration.class)
//@RibbonClients(defaultConfiguration = RibbonConfiguration.class)
public class UserCenterRibbonConfiguration {

}

package com.example.motorinsurancesales.dataprocurement.contextrecognition;

import com.example.motorinsurancesales.customerjourney.SalesChannel;

// holds info regarding operator
// if it's agent then we need info about its sale structure and we will trigger features like commision calculation
class SalesContext {
    SalesChannel salesChannel;
    AgentChannelContext agentContext;
    // does NCD deserve dedicated one? perhaps. it depends on how different it is from Agent sales channel
    ContactCenterContext contactCenterContext;

}

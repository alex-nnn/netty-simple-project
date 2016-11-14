package com.example;

import com.example.server.conf.SpringCfg;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * Created by remote on 11/13/16.
 */
public class Main {
    static public void main(String[] args){
        AbstractApplicationContext ctx = new AnnotationConfigApplicationContext(SpringCfg.class);
        ctx.registerShutdownHook();
    }
}

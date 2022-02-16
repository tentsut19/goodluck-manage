package th.co.infinitait.goodluck.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import sun.awt.AppContext;
import sun.awt.SunToolkit;

@SuppressWarnings("restriction")
public class ContextConfiguration implements ApplicationListener<ContextRefreshedEvent> {
    @Override
    public void onApplicationEvent(ContextRefreshedEvent arg0) {
        //refresh awt context
        if(AppContext.getAppContext() == null) {
            SunToolkit.createNewAppContext();
        }
    }
}
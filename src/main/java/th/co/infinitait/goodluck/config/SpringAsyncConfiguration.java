package th.co.infinitait.goodluck.config;


import java.lang.reflect.Method;
import java.util.Date;
import java.util.concurrent.Executor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import th.co.infinitait.goodluck.entity.UpdateOrderEntity;
import th.co.infinitait.goodluck.repository.UpdateOrderRepository;

@Slf4j
@Configuration
public class SpringAsyncConfiguration implements AsyncConfigurer {

    @Autowired
    private UpdateOrderRepository updateOrderRepository;

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(10);
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {

            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                log.info("Throwable Exception message : {}", ex.getMessage());
                log.info("Method name                 : {}", method.getName());
                if(method.getName().equals("uploadFileUpdateSuccess") ||
                        method.getName().equals("uploadFileUpdateParcelCode") ||
                        method.getName().equals("uploadFileUpdateCancel")){
                    String state = "";
                    if(method.getName().equals("uploadFileUpdateSuccess")){
                        state = "Success";
                    }else if(method.getName().equals("uploadFileUpdateParcelCode")){
                        state = "Shipping";
                    }else if(method.getName().equals("uploadFileUpdateCancel")){
                        state = "Cancel";
                    }
                    UpdateOrderEntity updateOrder = new UpdateOrderEntity();
                    updateOrder.setStatus("failUploadFile");
                    updateOrder.setState(state);
                    updateOrder.setCreatedBy("system");
                    updateOrder.setCreatedAt(new Date());
                    updateOrderRepository.save(updateOrder);
                }
                for (Object param : params) {
                    log.info("Parameter value             : {}", param);
                }
                log.info("stack Trace ");
                ex.printStackTrace();
            }

        };
    }
}

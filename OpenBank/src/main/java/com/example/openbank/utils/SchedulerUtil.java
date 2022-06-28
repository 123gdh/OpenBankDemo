package com.example.openbank.utils;

import com.tenpay.business.entpay.sdk.exception.EntpayException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.stereotype.Component;

@Component
@Data
@Slf4j
public class SchedulerUtil {
    //quartz定时任务：暂时停用，目前使用spring自带的Scheduled定时任务
    public static Scheduler startScheduler(JobDetail jobDetail, Trigger trigger) throws EntpayException{
        try {
            StdSchedulerFactory stdSchedulerFactory = new StdSchedulerFactory();
            Scheduler scheduler = stdSchedulerFactory.getScheduler();
            scheduler.scheduleJob(jobDetail,trigger);
            scheduler.start();
            return scheduler;
        } catch (SchedulerException e) {
            log.error("定时任务Util处理"+e.getMessage(),e);
            throw new EntpayException("定时任务Util异常"+e.getMessage(),e);
        }
    }
}

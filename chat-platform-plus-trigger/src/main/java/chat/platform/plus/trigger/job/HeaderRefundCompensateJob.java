package chat.platform.plus.trigger.job;


import chat.platform.plus.domain.trade.adapter.port.TradePort;
import chat.platform.plus.domain.trade.adapter.repository.TradeRepository;
import chat.platform.plus.domain.trade.model.entity.HeaderCompensateTaskEntity;
import chat.platform.plus.types.common.Constants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 团长退单补偿任务
 */
@Slf4j
@Service
public class HeaderRefundCompensateJob {

    @Resource
    private TradePort tradePort;

    @Resource
    private TradeRepository tradeRepository;

    @Resource
    private RedissonClient redissonClient;

    @Scheduled(cron = "0/10 * * * * ?")
    private void exec() {
        RLock lock = redissonClient.getLock(Constants.HeaderRefundCompensateJobLock);
        try {
            boolean getLock = lock.tryLock(3, 30, TimeUnit.SECONDS);
            if (!getLock) {
                log.info("获取锁失败：{}，此时有其他应用在团长退单补偿任务，等待", Constants.HeaderRefundCompensateJobLock);
                return;
            }
            log.info("获取锁成功：{}，团长退单补偿任务开始", Constants.HeaderRefundCompensateJobLock);
            List<HeaderCompensateTaskEntity> list = tradeRepository.getUnCompleteHeaderRefundCompensateTaskList();
            if (list == null || list.isEmpty()) {
                log.info("没有需要执行的团长退单补偿任务 - 团长退单补偿任务结束");
                return;
            }
            for (HeaderCompensateTaskEntity headerCompensateTaskEntity : list) {
                Integer status = tradePort.getTeamProgress(headerCompensateTaskEntity.getTeamId());
                if (status == 1) {
                    log.info("拼团组队完成，进行团长退单补偿，用户ID：{}，拼团组队ID：{}", headerCompensateTaskEntity.getHeaderId(), headerCompensateTaskEntity.getTeamId());
                    tradeRepository.headerRefundCompensate(headerCompensateTaskEntity.getHeaderId(), headerCompensateTaskEntity.getTeamId(), status);
                } else if (status == 2) {
                    log.info("拼团组队失败，不进行团长退单补偿，用户ID：{}，拼团组队ID：{}", headerCompensateTaskEntity.getHeaderId(), headerCompensateTaskEntity.getTeamId());
                    tradeRepository.headerRefundCompensateFail(headerCompensateTaskEntity.getHeaderId(), headerCompensateTaskEntity.getTeamId(), status);
                } else if (status == 0){
                    log.info("拼团组队仍在进行，不进行团长退单补偿，用户ID：{}，拼团组队ID：{}", headerCompensateTaskEntity.getHeaderId(), headerCompensateTaskEntity.getTeamId());
                }
            }
            log.info("团长退单补偿任务结束");
        } catch (Exception e) {
            log.info("团长退单补偿任务执行异常", e);
        } finally {
            // 检查锁是否被任何线程持有以及是否被当前线程持有 - 释放锁
            if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

}

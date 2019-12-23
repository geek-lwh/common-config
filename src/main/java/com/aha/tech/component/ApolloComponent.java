package com.aha.tech.component;

import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import com.ctrip.framework.apollo.spring.annotation.ApolloConfigChangeListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @Author: luweihong
 * @Date: 2018/9/19
 *
 * 用于监听一些变量变更后的动作,如果无需后续动作可以不更新 apollo实时更新
 */
@Component
public class ApolloComponent {

    private static final Logger logger = LoggerFactory.getLogger(ApolloComponent.class);

    @Autowired
    private RefreshScope refreshScope;


    /**
     * apollo 配置变更时间
     */
    @ApolloConfigChangeListener
    private void onChange(ConfigChangeEvent changeEvent) {
        Set<String> changeSet = changeEvent.changedKeys();
        changeSet.forEach(k -> {
            if (changeEvent.isChanged(k)) {
                String oldValue = changeEvent.getChange(k).getOldValue();
                String newValue = changeEvent.getChange(k).getNewValue();
                logger.info("apollo配置变更 变更键:{} 旧值: {} 新值: {}", k, oldValue, newValue);
            }
        });

        refreshScope.refreshAll();
    }
}

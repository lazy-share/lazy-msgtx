package com.lazy.msgtx.core.endpoint;

import com.alibaba.fastjson.JSON;
import com.lazy.msgtx.core.MessageLog;
import com.lazy.msgtx.core.common.Const;
import com.lazy.msgtx.core.common.SpringUtil;
import com.lazy.msgtx.core.serializer.SerializationFactory;
import com.lazy.msgtx.core.storage.MessageStorage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * 消息事务端点
 * </p>
 *
 * @author lzy
 * @since 2022/6/4.
 */
@Slf4j
@Endpoint
public class MsgTxEndpoint implements ApplicationContextAware {

    @Autowired
    private MessageStorage messageStorage;

    private ApplicationContext applicationContext;

    @PostMapping("/msgtx/page")
    public EndpointResult page(@RequestBody PageRequest request) {

        PageResponse response = messageStorage.page(request);
        if (CollectionUtils.isEmpty(response.getResultList())) {
            return EndpointResult.success(response);
        }
        List<MessageLog> messageLogs = JSON.parseArray(
                JSON.toJSONString(response.getResultList()), MessageLog.class);

        List<MsgTxTree> treeList = this.toTreeByPid(messageLogs, Const.ROOT);
        response.setResultList(treeList);

        return EndpointResult.success(response);
    }

    private void recursionTree(MsgTxTree parent, List<MessageLog> allResource) {
        String currentId = parent.getId();
        MsgTxTree childTree;
        for (MessageLog resource : allResource) {
            if (currentId.equals(String.valueOf(resource.getPid()))) {
                childTree = new MsgTxTree();
                BeanUtils.copyProperties(resource, childTree);
                childTree.setId(String.valueOf(resource.getId()));
                childTree.setPid(String.valueOf(resource.getPid()));
                parent.getSubList().add(childTree);
                recursionTree(childTree, allResource);
            }
        }
        Collections.sort(parent.getSubList());
    }

    public List<MsgTxTree> toTreeByPid(List<MessageLog> allResource, Long pid) {

        List<MessageLog> rootResourceList = new ArrayList<>();
        List<Long> pids = new ArrayList<>();
        for (MessageLog resourceEntity : allResource) {
            if (pid.equals(resourceEntity.getPid())) {
                rootResourceList.add(resourceEntity);
                pids.add(resourceEntity.getId());
            }
        }

        //补充pid的子级
        allResource.addAll(JSON.parseArray(
                JSON.toJSONString(messageStorage.loadInPid(pids)), MessageLog.class));

        MsgTxTree rootTree;
        List<MsgTxTree> treeDtoList = new ArrayList<>();
        for (MessageLog rootResource : rootResourceList) {
            rootTree = new MsgTxTree();
            BeanUtils.copyProperties(rootResource, rootTree);
            rootTree.setId(String.valueOf(rootResource.getId()));
            rootTree.setPid(String.valueOf(rootResource.getPid()));
            recursionTree(rootTree, allResource);
            treeDtoList.add(rootTree);
        }
        Collections.sort(treeDtoList);
        return treeDtoList;
    }


    @GetMapping("/msgtx/retry/{id}")
    public EndpointResult retry(@PathVariable("id") Long id) {

        try {
            if (id == null) {
                return EndpointResult.fail("id不能为空");
            }

            MessageLog messageLog = messageStorage.load(id);
            if (messageLog == null) {
                return EndpointResult.fail("查无数据");
            }

            String retryEndpoint = messageLog.getRetryEndpoint();
            if (!StringUtils.hasText(retryEndpoint)) {
                return EndpointResult.fail("调用端点数据为空");
            }

            String[] clazzAndMethod = retryEndpoint.split("#");
            if (clazzAndMethod.length != 3) {
                return EndpointResult.fail("调用端点数据错误");
            }

            String className = clazzAndMethod[0];
            Object invokeBean = SpringUtil.getBean(className);

            String methodName = clazzAndMethod[1];
            String paramType = clazzAndMethod[2];
            Class<?> invokeParamType = SpringUtil.getClass(paramType);
            if (invokeParamType == null) {
                return EndpointResult.fail("调用方法参数不存在");
            }
            Method invokeMethod = ReflectionUtils.findMethod(invokeBean.getClass(), methodName, invokeParamType);
            if (invokeMethod == null) {
                return EndpointResult.fail("调用方法不存在");
            }

            String invokeParams = messageLog.getMessageBody();
            if (!StringUtils.hasText(invokeParams)) {
                return EndpointResult.fail("调用方法参数为空");
            }

            ReflectionUtils.invokeMethod(invokeMethod, invokeBean,
                    SerializationFactory.of().deserialize(invokeParams, invokeParamType));

        } catch (Throwable e) {
            log.error("重试过程异常", e);
            return EndpointResult.fail(StringUtils.hasText(e.getMessage()) ? e.getMessage() : "系统异常");
        }

        return EndpointResult.success("执行成功");
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

package com.heibaiying.actuator.endpoint;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author : heibaiying
 * @description : 自定义端点
 */
@Endpoint(id = "customEndPoint")
@Component
public class CustomEndPoint {

    @ReadOperation
    public Map<String, Object> getCupInfo() throws SigarException {

        Map<String, Object> cupInfoMap = new LinkedHashMap<>();

        Sigar sigar = new Sigar();

        CpuInfo infoList[] = sigar.getCpuInfoList();
        CpuPerc[] cpuList = sigar.getCpuPercList();

        for (int i = 0; i < infoList.length; i++) {
            CpuInfo info = infoList[i];
            cupInfoMap.put("CPU " + i + " 的总量MHz", info.getMhz());                            // CPU的总量MHz
            cupInfoMap.put("CPU " + i + " 生产商", info.getVendor());                            // 获得CPU的生产商，如：Intel
            cupInfoMap.put("CPU " + i + " 类别", info.getModel());                               // 获得CPU的类别，如：Core
            cupInfoMap.put("CPU " + i + " 缓存数量", info.getCacheSize());                       // 缓冲存储器数量
            cupInfoMap.put("CPU " + i + " 用户使用率", CpuPerc.format(cpuList[i].getUser()));    // 用户使用率
            cupInfoMap.put("CPU " + i + " 系统使用率", CpuPerc.format(cpuList[i].getSys()));     // 系统使用率
            cupInfoMap.put("CPU " + i + " 当前等待率", CpuPerc.format(cpuList[i].getWait()));    // 当前等待率
            cupInfoMap.put("CPU " + i + " 当前错误率", CpuPerc.format(cpuList[i].getNice()));    // 当前错误率
            cupInfoMap.put("CPU " + i + " 当前空闲率", CpuPerc.format(cpuList[i].getIdle()));    // 当前空闲率
            cupInfoMap.put("CPU " + i + " 总的使用率", CpuPerc.format(cpuList[i].getCombined()));// 总的使用率
        }
        return cupInfoMap;
    }

}

package com.app.dtu.bean.model.device;

import com.app.dtu.bean.DataMsg;
import com.app.dtu.bean.Message;
import com.app.dtu.bean.model.*;
import com.app.dtu.config.DtuConfig;
import com.app.dtu.service.ServiceItem;
import com.app.dtu.util.DtuUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 故障电弧监控-03
 */
@Entity
@Table(name = DtuConfig.DTU_TABLE_PRIFIX +"fault_arc_monitor_device")
public class FaultArcMonitorDevice extends RedundancyDeviceData implements DeviceDataDeal, ParseToEntityAdapter<FaultArcMonitorDevice> {


    private static final Logger logger = LoggerFactory.getLogger(FaultArcMonitorDevice.class);

    public FaultArcMonitorDevice() {
    }

    public FaultArcMonitorDevice(Message message) {
        setMessage(message);
    }

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    private Integer ua;
    private Integer ia;
    // 电弧
    private Integer h1;

    private Integer umax1;
    private Integer umin1;

    private Integer imax1;

    private Integer hmax;


    @Override
    public FaultArcMonitorDevice generateEntity(Message message) {
        buildRedunancyDeviceInfo();
        if (CollectionUtils.isEmpty(message.getDataMsgs())){
            return this;
        }
        for (int i = 0; i < message.getDataMsgs().size(); i++) {
            DataMsg dataMsg = message.getDataMsgs().get(i);
            List<Integer> dataMsgs = dataMsg.getDatas();
            if (message.parseDeviceModelEnum() == DeviceTypeName.FAULT_ARC_MONITOR_0301) {
                if (DataType.getValue(dataMsg.getType()) == DataType.DATA_TYPE_03) {
                    ua = DtuUtil.getIntegerValue(dataMsgs, 0);
                } else if (DataType.getValue(dataMsg.getType()) == DataType.DATA_TYPE_04) {
                    ia = DtuUtil.getIntegerValue(dataMsgs, 0);
                } else if (DataType.getValue(dataMsg.getType()) == DataType.DATA_TYPE_05) {
                    h1 = DtuUtil.getIntegerValue(dataMsgs, 0);
                } else if (DataType.getValue(dataMsg.getType()) == DataType.DATA_TYPE_85) {
                    hmax = DtuUtil.getIntegerValue(dataMsgs, 0);
                } else if (DataType.getValue(dataMsg.getType()) == DataType.DATA_TYPE_83) {
                    umax1 = DtuUtil.getIntegerValue(dataMsgs, 0);
                    umin1 = DtuUtil.getIntegerValue(dataMsgs, 1);
                } else if (DataType.getValue(dataMsg.getType()) == DataType.DATA_TYPE_84) {
                    imax1 = DtuUtil.getIntegerValue(dataMsgs, 0);
                }
            }
        }
        return this;
    }

    @Override
    public boolean execute() {
        try{
            DeviceDataDeal deviceDataDeal = getStorageEntity();
            if (Objects.isNull(deviceDataDeal)){
                return false;
            }
            ServiceItem.faultArcMonitorService.updateOldDataStatus(getMessageId());
            ServiceItem.faultArcMonitorService.save(deviceDataDeal);
        }catch (Throwable e){
            logger.error("Execute add data to db or generate entity is error");
            return false;
        }
        return true;
    }
    @Override
    public boolean isChange() {
        boolean isChange = false;
        List<String> values = client.hmget(getMessageId(), "warn", "id");
        if (CollectionUtils.isEmpty(values) || values.size() < 2) {
            isChange = true;
        }else {
            if (!values.get(0).equalsIgnoreCase(String.valueOf(getWarnList()))){
                isChange = true;
            }else{
                isChange = false;
            }
        }
        if (!isChange) {
            Map<String, String> hashValue = new HashMap<>();
            hashValue.put("warn", String.valueOf(getWarnList()));
            hashValue.put("id", String.valueOf(getId()));
            client.hmset(getMessageId(),hashValue);
            logger.info("Redis set cache is [device_id: {}], [value: {}]", hashValue.toString());
            ServiceItem.faultArcMonitorService.updatePreviousDataStatus(getId(), 2);
        }
        return isChange;
    }
    @Override
    public FaultArcMonitorDevice buildDevice() {
        return this;
    }

    @Override
    public Message buildMessage() {
        return getMessage();
    }



    public static Logger getLogger() {
        return logger;
    }

    public Integer getUa() {
        return ua;
    }

    public void setUa(Integer ua) {
        this.ua = ua;
    }

    public Integer getIa() {
        return ia;
    }

    public void setIa(Integer ia) {
        this.ia = ia;
    }

    public Integer getH1() {
        return h1;
    }

    public void setH1(Integer h1) {
        this.h1 = h1;
    }

    public Integer getUmax1() {
        return umax1;
    }

    public void setUmax1(Integer umax1) {
        this.umax1 = umax1;
    }

    public Integer getUmin1() {
        return umin1;
    }

    public void setUmin1(Integer umin1) {
        this.umin1 = umin1;
    }

    public Integer getImax1() {
        return imax1;
    }

    public void setImax1(Integer imax1) {
        this.imax1 = imax1;
    }

    public Integer getHmax() {
        return hmax;
    }

    public void setHmax(Integer hmax) {
        this.hmax = hmax;
    }

    @Override
    public String toString() {
        return "FaultArcMonitorDevice{" +
                "id=" + getId() +
                ", ua=" + ua +
                ", ia=" + ia +
                ", h1=" + h1 +
                ", umax1=" + umax1 +
                ", umin1=" + umin1 +
                ", imax1=" + imax1 +
                ", hmax=" + hmax +
                '}';
    }
}

package com.app.dtu.bean.model.device;

import com.app.dtu.bean.Message;
import com.app.dtu.bean.model.DeviceDataDeal;
import com.app.dtu.bean.model.ParseToEntityAdapter;
import com.app.dtu.bean.model.RedundancyDeviceData;

import javax.persistence.*;

/**
 * 可燃气体监控-06
 */
@Entity
@Table(name = "dtu_combustible_gas_monitor_device")
public class CombustibleGasMonitorDevice extends RedundancyDeviceData implements DeviceDataDeal, ParseToEntityAdapter<CombustibleGasMonitorDevice>{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean execute() {
        return false;
    }

    @Override
    public CombustibleGasMonitorDevice buildDevice() {
        return null;
    }

    @Override
    public Message buildMessage() {
        return getMessage();
    }

    @Override
    public CombustibleGasMonitorDevice generateEntity(Message message) {
        return this;
    }
}

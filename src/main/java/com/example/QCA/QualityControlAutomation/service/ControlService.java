package com.example.QCA.QualityControlAutomation.service;

import com.example.QCA.QualityControlAutomation.domain.ControlResult;
import com.example.QCA.QualityControlAutomation.domain.InitInfo;
import com.example.QCA.QualityControlAutomation.repository.ControlRepository;
import com.example.QCA.QualityControlAutomation.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ControlService {

    private final ControlRepository controlRepository;

    public ControlService(ControlRepository controlRepository) {
        this.controlRepository = controlRepository;
    }

    public void setLabelAndHomepage() throws Exception {
        DataUtil dataUtil = new DataUtil();
        List<InitInfo> initInfoList = dataUtil.getLabelAndHomepage();
        List<ControlResult> controlResultList = new ArrayList<>();

        for (InitInfo initInfo : initInfoList)
            controlResultList.add(new ControlResult(initInfo.getLabel(), initInfo.getHomepage()));

        controlRepository.saveAll(controlResultList);
    }
}

package com.newland.service;


import com.newland.model.ServiceObject;

import java.util.List;

public interface IObjectService {
    List<ServiceObject> selectHis(ServiceObject his);
}

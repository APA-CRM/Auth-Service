package com.crm.auth.persistance.entity.redis;

import com.crm.sharedlib.enums.Action;
import com.crm.sharedlib.enums.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResourcePermission implements Serializable {

    private Resource resource;

    private List<Action> actions;

}

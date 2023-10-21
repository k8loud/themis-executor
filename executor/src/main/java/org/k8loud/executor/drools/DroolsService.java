package org.k8loud.executor.drools;

import org.k8loud.executor.dto.OrderDiscount;
import org.k8loud.executor.dto.OrderRequest;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DroolsService {

    private final KieContainer container;

    /**
     * 1. drools gets metrics from { prometheus | hephaestus }
     * 2. depending on the rules builds an action @Action
     * 3. themis executes it
     */


    ///d
    public DroolsService(KieContainer container) {
        this.container = container;
    }

    public OrderDiscount getDiscount(OrderRequest rq) {
        OrderDiscount orderDiscount = new OrderDiscount();
        KieSession session = container.newKieSession();
        session.setGlobal("orderDiscount", orderDiscount);
        session.insert(rq);
        session.fireAllRules();
        session.dispose();
        return orderDiscount;
    }
}

package org.openmrs.module.htmlformentry.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Provider;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.openmrs.module.htmlformentry.element.ProviderStub;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Controller
public class ProviderController {
    private Log log = LogFactory.getLog(ProviderController.class);

    @RequestMapping("/module/htmlformentry/providers")
    @ResponseBody
    public Object getProviders(@RequestParam(value="searchParam",required = false) String searchParam,
                               @RequestParam(value="includeRetired", required= false, defaultValue = "false") Boolean includeRetired)
            throws Exception {
        ProviderService ps = Context.getProviderService();

        List<Provider> providerList = null;
        if(includeRetired) {
            providerList = ps.getProviders(searchParam,null,null,null);
        } else {
            if(searchParam == null) providerList = ps.getAllProviders();
            else providerList = ps.getProviders(searchParam, null, null, null, false);
        }

        return getProviderStubs(providerList);
    }

    private List<ProviderStub> getProviderStubs(Collection<Provider> providers) {
        List<ProviderStub> providerStubList = new LinkedList<ProviderStub>();
        for(Provider p:providers) {
            providerStubList.add(new ProviderStub(p));
        }
        return providerStubList;
    }
}

package org.openmrs.module.htmlformentry.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.Provider;
import org.openmrs.api.ProviderService;
import org.openmrs.api.context.Context;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class ProviderController {
    private Log log = LogFactory.getLog(ProviderController.class);

    @RequestMapping("/htmlformentry/providers")
    @ResponseBody
    public Object getProviders(@RequestParam(value="searchParam",required = false) String searchParam,
                               @RequestParam(value="includeRetired", required= false) Boolean includeRetired)
            throws Exception {
        ProviderService ps = Context.getProviderService();

        List<Provider> providerList = null;
        if(includeRetired) {
            providerList = ps.getProviders(searchParam,null,null,null);
        } else {
            providerList = ps.getProviders(searchParam,null,null,null,false);
        }
        System.out.println("Here is the list" + providerList);
        //Create the json string
//        if(providerList!=null) {
//            if (!providerList.isEmpty()) {
//                StringBuilder json = new StringBuilder("[");
//                Person person;
//                for (Provider p : providerList) {
//                    json.append("{").append("'provider_id':'").append(p.getProviderId()).append("',").
//                            append("'identifier':'").append(p.getIdentifier()).append("'");
//
//                    String pname = null;
//                    if ((person = p.getPerson()) != null) {//Add name  for display purposes
//                        pname = person.getGivenName() + " " + person.getFamilyName();
//                    } else {    //use provider name
//                        pname = p.getName();
//                    }
//
//                    if (StringUtils.hasLength(pname)) {
//                        json.append(",'name':'").append(pname).append("'");
//                    }
//
//                    json.append("},");
//                }
//
//                json.deleteCharAt(json.lastIndexOf(",")).append("]");
//                return json.toString();
//            }
//        }
        return providerList;
    }

    @RequestMapping("/module/htmlformentry/hello")
    public void hello() {
       // return "hello";
    }

//    @RequestMapping("/module/htmlformentry/another")
//    @ResponseBody
//    public Object another(){
//        return new Person(1);
//    }
}

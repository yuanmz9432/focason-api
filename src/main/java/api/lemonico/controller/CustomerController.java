package api.lemonico.controller;

import api.lemonico.resource.CustomerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController extends AbstractController{

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final static String COLLECTION_RESOURCE_PATH = "/customers";

    private final static String CUSTOMER_RESOURCE_PATH = COLLECTION_RESOURCE_PATH + "/{id}";

    @RequestMapping(method = RequestMethod.GET, path = COLLECTION_RESOURCE_PATH)
    public ResponseEntity<CustomerResource> getCustomers() {
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = CUSTOMER_RESOURCE_PATH)
    public String getCustomer(@PathVariable Long id) {
        logger.info("id: {}", id);
        return "getCustomer";
    }

    @RequestMapping(method = RequestMethod.PUT, path = COLLECTION_RESOURCE_PATH)
    public String updateCustomer(@RequestBody CustomerResource resource) {
        logger.info("resource: {}", resource);
        return "updateCustomer";
    }

    @RequestMapping(method = RequestMethod.POST, path = COLLECTION_RESOURCE_PATH)
    public String createCustomer(@RequestBody CustomerResource resource) {
        logger.info("resource: {}", resource);
        return "createCustomer";
    }

    @RequestMapping(method = RequestMethod.DELETE, path = CUSTOMER_RESOURCE_PATH)
    public String deleteCustomer(@PathVariable Long id) {
        logger.info("id: {}", id);
        return "deleteCustomer";
    }
}

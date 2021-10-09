package api.lemonico.controller;

import api.lemonico.annotation.LcConditionParam;
import api.lemonico.annotation.LcPaginationParam;
import api.lemonico.annotation.LcSortParam;
import api.lemonico.attribute.LcPagination;
import api.lemonico.attribute.LcSort;
import api.lemonico.repository.CustomerRepository;
import api.lemonico.resource.CustomerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CustomerController extends AbstractController{

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final static String COLLECTION_RESOURCE_URI = "/customers";

    private final static String CUSTOMER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    @RequestMapping(method = RequestMethod.GET, path = COLLECTION_RESOURCE_URI)
    public ResponseEntity<CustomerResource> getCustomers(
            @LcConditionParam CustomerRepository.Condition condition,
            @LcPaginationParam LcPagination pagination,
            @LcSortParam LcSort lcSort) {
        if (condition == null) {
            condition = CustomerRepository.Condition.DEFAULT;
        }
        var sort = CustomerRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, path = CUSTOMER_RESOURCE_URI)
    public String getCustomer(@PathVariable Long id) {
        logger.info("id: {}", id);
        return "getCustomer";
    }

    @RequestMapping(method = RequestMethod.PUT, path = COLLECTION_RESOURCE_URI)
    public String updateCustomer(@RequestBody CustomerResource resource) {
        logger.info("resource: {}", resource);
        return "updateCustomer";
    }

    @RequestMapping(method = RequestMethod.POST, path = COLLECTION_RESOURCE_URI)
    public String createCustomer(@RequestBody CustomerResource resource) {
        logger.info("resource: {}", resource);
        return "createCustomer";
    }

    @RequestMapping(method = RequestMethod.DELETE, path = CUSTOMER_RESOURCE_URI)
    public String deleteCustomer(@PathVariable Long id) {
        logger.info("id: {}", id);
        return "deleteCustomer";
    }
}

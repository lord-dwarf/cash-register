package com.polinakulyk.cashregister.config.autorun;

import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.ProductAmountUnit;
import com.polinakulyk.cashregister.service.api.ProductService;
import com.polinakulyk.cashregister.service.api.UserService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.GRAM;
import static com.polinakulyk.cashregister.db.entity.ProductAmountUnit.Value.UNIT;

@Component
public class InitDbAutorun {

    private final UserService userService;
    private final ProductService productService;

    private final AtomicBoolean isContextInitialized = new AtomicBoolean();

    @Value("${cashregister.initdb.user.teller.username}")
    private String tellerUsername;
    @Value("${cashregister.initdb.user.teller.password}")
    private String tellerPassword;
    @Value("${cashregister.initdb.user.teller.role}")
    private String tellerRole;
    @Value("${cashregister.initdb.user.teller2.username}")
    private String teller2Username;
    @Value("${cashregister.initdb.user.teller2.password}")
    private String teller2Password;
    @Value("${cashregister.initdb.user.teller2.role}")
    private String teller2Role;
    @Value("${cashregister.initdb.user.sr_teller.username}")
    private String srTellerUsername;
    @Value("${cashregister.initdb.user.sr_teller.password}")
    private String srTellerPassword;
    @Value("${cashregister.initdb.user.sr_teller.role}")
    private String srTellerRole;
    @Value("${cashregister.initdb.user.merch.username}")
    private String merchUsername;
    @Value("${cashregister.initdb.user.merch.password}")
    private String merchPassword;
    @Value("${cashregister.initdb.user.merch.role}")
    private String merchRole;

    public InitDbAutorun(UserService userService, ProductService productService) {
        this.userService = userService;
        this.productService = productService;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isContextInitialized.compareAndSet(false, true)) {

            // users
            userService.create(tellerUsername, tellerPassword, tellerRole, false);
            userService.create(teller2Username, teller2Password, teller2Role, false);
            userService.create(srTellerUsername, srTellerPassword, srTellerRole, false);
            userService.create(merchUsername, merchPassword, merchRole, false);

            // products
            productService.create(new Product()
                    .setCode("UA-2020")
                    .setCategory("parmesan")
                    .setName("Landana 500 days")
                    .setDetails("Produced in Poltava region by cows")
                    .setPrice(70000)
                    .setAmountAvailable(8000)
                    .setAmountUnit(GRAM)
            );
            productService.create(new Product()
                    .setCode("PL-2019")
                    .setCategory("blue mold")
                    .setName("Lazur Blue")
                    .setDetails("Produced in Poland by monks")
                    .setPrice(55000)
                    .setAmountAvailable(4000)
                    .setAmountUnit(GRAM)
            );
            productService.create(new Product()
                    .setCode("NL-2018")
                    .setCategory("goat")
                    .setName("Le Chevre")
                    .setDetails("Produced in Netherlands by goats")
                    .setPrice(12000)
                    .setAmountAvailable(100)
                    .setAmountUnit(UNIT)
            );
        }
    }

}

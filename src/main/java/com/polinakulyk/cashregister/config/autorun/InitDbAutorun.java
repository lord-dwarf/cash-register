package com.polinakulyk.cashregister.config.autorun;

import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.ReceiptItem;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.ProductService;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.UserService;
import java.util.concurrent.atomic.AtomicBoolean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import static com.polinakulyk.cashregister.db.dto.ProductAmountUnit.GRAM;
import static com.polinakulyk.cashregister.db.dto.ProductAmountUnit.UNIT;

@Component
public class InitDbAutorun {

    private static final AtomicBoolean isContextInitializedOnce = new AtomicBoolean();

    private final CashboxService cashboxService;
    private final UserService userService;
    private final ProductService productService;
    private final ReceiptService receiptService;

    @Value("${cashregister.initdb.cashbox.id}")
    private String cashboxId;
    @Value("${cashregister.initdb.cashbox.name}")
    private String cashboxName;
    @Value("${cashregister.initdb.user.teller.id}")
    private String tellerId;
    @Value("${cashregister.initdb.user.teller.username}")
    private String tellerUsername;
    @Value("${cashregister.initdb.user.teller.password}")
    private String tellerPassword;
    @Value("${cashregister.initdb.user.teller.role}")
    private String tellerRole;
    @Value("${cashregister.initdb.user.teller.full_name}")
    private String tellerFullName;
    @Value("${cashregister.initdb.user.teller2.id}")
    private String teller2Id;
    @Value("${cashregister.initdb.user.teller2.username}")
    private String teller2Username;
    @Value("${cashregister.initdb.user.teller2.password}")
    private String teller2Password;
    @Value("${cashregister.initdb.user.teller2.role}")
    private String teller2Role;
    @Value("${cashregister.initdb.user.teller2.full_name}")
    private String teller2FullName;
    @Value("${cashregister.initdb.user.sr_teller.id}")
    private String srTellerId;
    @Value("${cashregister.initdb.user.sr_teller.username}")
    private String srTellerUsername;
    @Value("${cashregister.initdb.user.sr_teller.password}")
    private String srTellerPassword;
    @Value("${cashregister.initdb.user.sr_teller.role}")
    private String srTellerRole;
    @Value("${cashregister.initdb.user.sr_teller.full_name}")
    private String srTellerFullName;
    @Value("${cashregister.initdb.user.merch.id}")
    private String merchId;
    @Value("${cashregister.initdb.user.merch.username}")
    private String merchUsername;
    @Value("${cashregister.initdb.user.merch.password}")
    private String merchPassword;
    @Value("${cashregister.initdb.user.merch.role}")
    private String merchRole;
    @Value("${cashregister.initdb.user.merch.full_name}")
    private String merchFullName;

    public InitDbAutorun(
            CashboxService cashboxService,
            UserService userService,
            ProductService productService,
            ReceiptService receiptService
    ) {
        this.cashboxService = cashboxService;
        this.userService = userService;
        this.productService = productService;
        this.receiptService = receiptService;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isContextInitializedOnce.compareAndSet(false, true)) {
            createCashboxes();
            createUsers();
            createProducts();
            createReceipts();
        }
    }

    private void createCashboxes() {
        cashboxService.createWithId(cashboxId, cashboxName, "ACTIVE");
    }

    private void createProducts() {
        productService.create(new Product()
                .setCode("NL-2017-1")
                .setCategory("parmesan")
                .setName("Landana 500 days 48%")
                .setDetails("Produced in Netherlands by cows")
                .setPrice(60000)
                .setAmountAvailable(8000)
                .setAmountUnit(GRAM)
        );
        productService.create(new Product()
                .setCode("DE-2017-1")
                .setCategory("dorblu")
                .setName("Kaserei Champignon 55%")
                .setDetails("Produced in Germany by folk")
                .setPrice(55000)
                .setAmountAvailable(12000)
                .setAmountUnit(GRAM)
        );
        productService.create(new Product()
                .setCode("DE-2017-2")
                .setCategory("dorblu")
                .setName("Kaserei Dorblu 50%")
                .setDetails("Produced in Germany by folk")
                .setPrice(55000)
                .setAmountAvailable(8000)
                .setAmountUnit(GRAM)
        );
        productService.create(new Product()
                .setCode("DE-2020-1")
                .setCategory("mold")
                .setName("Paladin Edelpilz 50%")
                .setDetails("Produced in Germany")
                .setPrice(40000)
                .setAmountAvailable(8000)
                .setAmountUnit(GRAM)
        );
        productService.create(new Product()
                .setCode("NL-2017-2")
                .setCategory("parmesan")
                .setName("Grand'Or Old Mill 50%")
                .setDetails("Produced in Netherlands")
                .setPrice(80000)
                .setAmountAvailable(8000)
                .setAmountUnit(GRAM)
        );
        productService.create(new Product()
                .setCode("DE-2020-2")
                .setCategory("cream")
                .setName("Kaserei Fitaki Original 40% 500g")
                .setDetails("Produced in Germany")
                .setPrice(14000)
                .setAmountAvailable(175)
                .setAmountUnit(UNIT)
        );
        productService.create(new Product()
                .setCode("NL-2021")
                .setCategory("goat")
                .setName("Le Chevre")
                .setDetails("Produced in Netherlands")
                .setPrice(60000)
                .setAmountAvailable(8000)
                .setAmountUnit(GRAM)
        );
        productService.create(new Product()
                .setCode("IT-2017")
                .setCategory("cream")
                .setName("Philadelphia 69% 125g")
                .setDetails("Produced in Italy")
                .setPrice(5500)
                .setAmountAvailable(180)
                .setAmountUnit(UNIT)
        );
        productService.create(new Product()
                .setCode("UA-2017")
                .setCategory("camembert")
                .setName("Pastourelle camembert in wine crust 50%")
                .setDetails("Produced in Ukraine")
                .setPrice(50000)
                .setAmountAvailable(7000)
                .setAmountUnit(GRAM)
        );
        productService.create(new Product()
                .setCode("GB-2019")
                .setCategory("cheddar")
                .setName("Wyke Farms Ivy's Vintage Reserve 58% 200g")
                .setDetails("Produced in England")
                .setPrice(13000)
                .setAmountAvailable(155)
                .setAmountUnit(UNIT)
        );
        productService.create(new Product()
                .setCode("FR-2021")
                .setCategory("goat")
                .setName("Le Chevre 150g")
                .setDetails("Produced in France")
                .setPrice(15000)
                .setAmountAvailable(160)
                .setAmountUnit(UNIT)
        );
        productService.create(new Product()
                .setCode("DE-2017-3")
                .setCategory("dorblu")
                .setName("Kaserei Champignon Grand Noir 60%")
                .setDetails("Produced in Germany")
                .setPrice(80000)
                .setAmountAvailable(8000)
                .setAmountUnit(GRAM)
        );
        productService.create(new Product()
                .setCode("PL-never")
                .setCategory("blue mold")
                .setName("Lazur Blue")
                .setDetails("Produced in Poland")
                .setPrice(45000)
                .setAmountAvailable(4000)
                .setAmountUnit(GRAM)
        );
    }

    private void createUsers() {

        // predefined ids make the already existing auth JTWs to be valid
        userService.createWithId(
                tellerId,
                cashboxId,
                tellerUsername,
                tellerPassword,
                tellerRole,
                tellerFullName,
                false
        );
        User user = userService.findById(tellerId).get();
        user.getCashbox();

        userService.createWithId(
                teller2Id,
                cashboxId,
                teller2Username,
                teller2Password,
                teller2Role,
                teller2FullName,
                false
        );
        userService.createWithId(
                srTellerId,
                cashboxId,
                srTellerUsername,
                srTellerPassword,
                srTellerRole,
                srTellerFullName,
                false
        );
        userService.createWithId(
                merchId,
                cashboxId,
                merchUsername,
                merchPassword,
                merchRole,
                merchFullName,
                false
        );
    }

    private void createReceipts() {
        createReceipts(tellerId);
        createReceipts(teller2Id);
        createReceipts(srTellerId);
    }

    private void createReceipts(String userId) {

        // COMPLETED receipts
        for (Product p : productService.findAll()) {
            Receipt r = receiptService.createReceipt(userId);
            receiptService.add(r.getId(), new ReceiptItem()
                    .setProduct(p)
                    .setAmount(50));
            receiptService.complete(r.getId());
        }

        // x1 CREATED receipt
        Receipt receipt = receiptService.createReceipt(userId);
        receiptService.add(receipt.getId(), new ReceiptItem()
                .setProduct(productService.findAll().stream().findFirst().get())
                .setAmount(50));

        // CANCELED receipts
        for (Product p : productService.findAll()) {
            Receipt r = receiptService.createReceipt(userId);
            receiptService.add(r.getId(), new ReceiptItem()
                    .setProduct(p)
                    .setAmount(50));
            receiptService.cancel(r.getId());
        }
    }
}

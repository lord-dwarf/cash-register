package com.polinakulyk.cashregister.config.autorun;

import com.polinakulyk.cashregister.db.dto.ProductAmountUnit;
import com.polinakulyk.cashregister.db.entity.Product;
import com.polinakulyk.cashregister.db.entity.Receipt;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.dto.UserDetailsDto;
import com.polinakulyk.cashregister.service.api.CashboxService;
import com.polinakulyk.cashregister.service.api.ProductService;
import com.polinakulyk.cashregister.service.api.ReceiptService;
import com.polinakulyk.cashregister.service.api.UserService;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import static com.polinakulyk.cashregister.db.dto.ProductAmountUnit.KILO;
import static com.polinakulyk.cashregister.db.dto.ProductAmountUnit.UNIT;
import static com.polinakulyk.cashregister.db.dto.ShiftStatus.ACTIVE;
import static com.polinakulyk.cashregister.security.dto.UserRole.fromString;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.bigDecimalAmount;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.bigDecimalMoney;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

@Slf4j
@Component
public class InitDbAutorun {

    private static final AtomicBoolean isContextInitializedOnce = new AtomicBoolean();

    private final CashboxService cashboxService;
    private final UserService userService;
    private final ProductService productService;
    private final ReceiptService receiptService;
    private final AuthHelper authHelper;

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
            ReceiptService receiptService,
            AuthHelper authHelper
    ) {
        this.cashboxService = cashboxService;
        this.userService = userService;
        this.productService = productService;
        this.receiptService = receiptService;
        this.authHelper = authHelper;
    }

    @EventListener
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (isContextInitializedOnce.compareAndSet(false, true)) {
                log.debug("BEGIN CashRegister autorun");
                createCashboxes();
                createUsers();
                createProducts(merchId);
                createReceipts();
                log.info("DONE CashRegister autorun");
        }
    }

    private void createCashboxes() {
        cashboxService.createWithId(cashboxId, cashboxName, ACTIVE);
        log.info("DONE Cash boxes created by autorun");
    }

    private void createProducts(String merchUserId) {
        setupAuthenticationContext(merchUserId);

        productService.create(new Product()
                .setCode("NL-2017-1")
                .setCategory("parmesan")
                .setName("Landana 500 days 48%")
                .setDetails("Produced in Netherlands.")
                .setPrice(bigDecimalMoney("600.00"))
                .setAmountAvailable(bigDecimalAmount("12.000", KILO))
                .setAmountUnit(KILO)
        );
        productService.create(new Product()
                .setCode("DE-2017-1")
                .setCategory("dorblu")
                .setName("Kaserei Champignon 55%")
                .setDetails("Produced in Germany.")
                .setPrice(bigDecimalMoney("550.00"))
                .setAmountAvailable(bigDecimalAmount("13.000", KILO))
                .setAmountUnit(KILO)
        );
        productService.create(new Product()
                .setCode("DE-2017-2")
                .setCategory("dorblu")
                .setName("Kaserei Dorblu 50%")
                .setDetails("Produced in Germany.")
                .setPrice(bigDecimalMoney("500.00"))
                .setAmountAvailable(bigDecimalAmount("11.000", KILO))
                .setAmountUnit(KILO)
        );
        productService.create(new Product()
                .setCode("DE-2020-1")
                .setCategory("mold")
                .setName("Paladin Edelpilz 50%")
                .setDetails("Produced in Germany.")
                .setPrice(bigDecimalMoney("400.00"))
                .setAmountAvailable(bigDecimalAmount("8.000", KILO))
                .setAmountUnit(KILO)
        );
        productService.create(new Product()
                .setCode("NL-2017-2")
                .setCategory("parmesan")
                .setName("Grand'Or Old Mill 50%")
                .setDetails("Produced in Netherlands.")
                .setPrice(bigDecimalMoney("800.00"))
                .setAmountAvailable(bigDecimalAmount("9.000", KILO))
                .setAmountUnit(KILO)
        );
        productService.create(new Product()
                .setCode("DE-2020-2")
                .setCategory("cream")
                .setName("Kaserei Fitaki Original 40% 500g")
                .setDetails("Produced in Germany.")
                .setPrice(bigDecimalMoney("140.00"))
                .setAmountAvailable(bigDecimalAmount("275", UNIT))
                .setAmountUnit(UNIT)
        );
        productService.create(new Product()
                .setCode("NL-2021")
                .setCategory("goat")
                .setName("Le Chevre")
                .setDetails("Produced in Netherlands.")
                .setPrice(bigDecimalMoney("600.00"))
                .setAmountAvailable(bigDecimalAmount("8.000", KILO))
                .setAmountUnit(KILO)
        );
        productService.create(new Product()
                .setCode("IT-2017")
                .setCategory("cream")
                .setName("Philadelphia 69% 125g")
                .setDetails("Produced in Italy.")
                .setPrice(bigDecimalMoney("550.00"))
                .setAmountAvailable(bigDecimalAmount("280", UNIT))
                .setAmountUnit(UNIT)
        );
        productService.create(new Product()
                .setCode("UA-2017")
                .setCategory("camembert")
                .setName("Pastourelle camembert in wine crust 50%")
                .setDetails("Produced in Ukraine.")
                .setPrice(bigDecimalMoney("500.00"))
                .setAmountAvailable(bigDecimalAmount("7.000", KILO))
                .setAmountUnit(KILO)
        );
        productService.create(new Product()
                .setCode("GB-2019")
                .setCategory("cheddar")
                .setName("Wyke Farms Ivy's Vintage Reserve 58% 200g")
                .setDetails("Produced in England.")
                .setPrice(bigDecimalMoney("130.00"))
                .setAmountAvailable(bigDecimalAmount("255", UNIT))
                .setAmountUnit(UNIT)
        );
        productService.create(new Product()
                .setCode("FR-2021")
                .setCategory("goat")
                .setName("Le Chevre 150g")
                .setDetails("Produced in France.")
                .setPrice(bigDecimalMoney("150.00"))
                .setAmountAvailable(bigDecimalAmount("260", UNIT))
                .setAmountUnit(UNIT)
        );
        productService.create(new Product()
                .setCode("DE-2017-3")
                .setCategory("dorblu")
                .setName("Kaserei Champignon Grand Noir 60%")
                .setDetails("Produced in Germany.")
                .setPrice(bigDecimalMoney("800.00"))
                .setAmountAvailable(bigDecimalAmount("8.000", KILO))
                .setAmountUnit(KILO)
        );
        productService.create(new Product()
                .setCode("PL-never")
                .setCategory("blue mold")
                .setName("Lazur Blue")
                .setDetails("Produced in Poland.")
                .setPrice(bigDecimalMoney("450.00"))
                .setAmountAvailable(bigDecimalAmount("4.001", KILO))
                .setAmountUnit(KILO)
        );

        cleanAuthenticationContext();
        log.info("DONE Products created by autorun");
    }

    private void createUsers() {
        // predefined ids make the already existing auth JTWs to be valid
        userService.createWithId(
                tellerId,
                cashboxId,
                tellerUsername,
                tellerPassword,
                fromString(tellerRole).get(),
                tellerFullName
        );
        userService.createWithId(
                teller2Id,
                cashboxId,
                teller2Username,
                teller2Password,
                fromString(teller2Role).get(),
                teller2FullName
        );
        userService.createWithId(
                srTellerId,
                cashboxId,
                srTellerUsername,
                srTellerPassword,
                fromString(srTellerRole).get(),
                srTellerFullName
        );
        userService.createWithId(
                merchId,
                cashboxId,
                merchUsername,
                merchPassword,
                fromString(merchRole).get(),
                merchFullName
        );
        log.info("DONE Users created by autorun");
    }

    private void createReceipts() {
        var products = productService.findAll();

        createReceipts(tellerId, products);
        createReceipts(teller2Id, products);
        createReceipts(srTellerId, products);

        log.info("DONE Receipts created by autorun");
    }

    private void createReceipts(String userId, List<Product> products) {
        setupAuthenticationContext(userId);

        // COMPLETED receipts
        for (Product p : products) {
            Receipt r = receiptService.createReceipt(userId);
            BigDecimal amt = generateReceiptItemAmount(p.getAmountUnit());
            receiptService.addReceiptItem(r.getId(), p.getId(), amt);
            receiptService.completeReceipt(r.getId());
        }

        // CANCELED receipts
        for (Product p : products) {
            Receipt r = receiptService.createReceipt(userId);
            BigDecimal amt = generateReceiptItemAmount(p.getAmountUnit());
            receiptService.addReceiptItem(r.getId(), p.getId(), amt);
            receiptService.cancelReceipt(r.getId());
        }

        // x1 CREATED receipt
        Receipt r = receiptService.createReceipt(userId);
        Product p = products.get(0);
        BigDecimal amt = generateReceiptItemAmount(p.getAmountUnit());
        receiptService.addReceiptItem(r.getId(), p.getId(), amt);

        cleanAuthenticationContext();
    }

    private BigDecimal generateReceiptItemAmount(ProductAmountUnit amountUnit) {
        switch (amountUnit) {
            case UNIT: return bigDecimalAmount("50", UNIT);
            case KILO: return bigDecimalAmount("0.150", KILO);
            default: throw new UnsupportedOperationException(quote(
                    "Product amount unit not supported", amountUnit));
        }
    }

    /*
     * Put Authentication object into Spring Security context,
     * for AuthHelper to be able to get user id from context in autorun.
     * User id from context will be used by service layer.
     */
    private void setupAuthenticationContext(String userId) {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                new UserDetailsDto().setUserId(userId), "");
        authHelper.setAuthentication(auth);
    }

    private void cleanAuthenticationContext() {
        authHelper.setAuthentication(null);
    }
}

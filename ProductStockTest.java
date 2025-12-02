package test;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import main.java.ProductStock;

import java.util.concurrent.TimeUnit;



@DisplayName("ProductStock JUnit5 Test ")
class ProductStockTest {

    private static final String ID = "111";
    private static final String LOCATION = "w-1";

    private ProductStock stock;

   

    @BeforeAll
    static void beforeAll() {
        System.out.println(">>> Starting ProductStock test ");
    }

    @AfterAll
    static void afterAll() {
        System.out.println(">>> Finished ProductStock test ");
    }

    @BeforeEach
    void setUp() {
        // onHand = 50, reorderThreshold = 10, maxCapacity = 100
        stock = new ProductStock(ID, LOCATION, 50, 10, 100);
    }

    @AfterEach
    void AfterEach() {
        System.out.println("Test done");
    }

    

    @Test
    @Tag("sanity")
    @DisplayName("Constructor-valid parameters ")
    void constructor_validParameters_shouldCreateObject() {
        String productId = "1";
        String location = "L-1";
        int initialOnHand = 20;
        int reorderThreshold = 5;
        int maxCapacity = 100;

        ProductStock ps = new ProductStock(productId, location, initialOnHand, reorderThreshold, maxCapacity);

        assertAll("Initial state",
                () -> assertEquals(productId, ps.getProductId()),
                () -> assertEquals(location, ps.getLocation()),
                () -> assertEquals(initialOnHand, ps.getOnHand()),
                () -> assertEquals(0, ps.getReserved()),
                () -> assertEquals(reorderThreshold, ps.getReorderThreshold()),
                () -> assertEquals(maxCapacity, ps.getMaxCapacity()),
                () -> assertEquals(initialOnHand, ps.getAvailable())
        );
    }

    @Test
    @Tag("regression")
    @DisplayName("Constructor - null productId should throw IllegalArgumentException")
    void constructor_nullProductId_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock(null, "L", 10, 5, 100));
    }

    @Test
    @DisplayName("Constructor - blank location should throw IllegalArgumentException")
    void constructor_blankLocation_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("1", "   ", 10, 5, 100));
    }

    @Test
    @DisplayName("Constructor - negative initialOnHand should throw")
    void constructor_negativeInitialOnHand_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("1", "L", -1, 5, 100));
    }

    @Test
    @DisplayName("Constructor - negative reorderThreshold should throw")
    void constructor_negativeReorderThreshold_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("1", "L", 10, -1, 100));
    }

    @Test
    @DisplayName("Constructor - non-positive maxCapacity should throw")
    void constructor_nonPositiveMaxCapacity_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("1", "L", 10, 5, 0));
    }

    @Test
    @DisplayName("Constructor - initialOnHand > maxCapacity should throw")
    void constructor_initialOnHandGreaterThanMaxCapacity_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProductStock("1", "L", 101, 5, 100));
    }

    
    
    
    
    
    
    
    
    

    @ParameterizedTest()
    @ValueSource(ints = {1, 5, 10})
    @Tag("sanity")
    @DisplayName("addStock - normal positive amounts")
    void addStock_normalAmounts_shouldIncreaseOnHand(int amount) {
        int before = stock.getOnHand();
        stock.addStock(amount);

        assertAll(
                () -> assertEquals(before + amount, stock.getOnHand()),
                () -> assertEquals(stock.getOnHand() - stock.getReserved(), stock.getAvailable())
        );
    }

    @Test
    @DisplayName("addStock - adding exactly to maxCapacity")
    void addStock_toMaxCapacity_shouldSucceed() {
        stock.addStock(50); // 50 + 50 = 100

        assertEquals(100, stock.getOnHand());
        assertEquals(100, stock.getAvailable());
    }

    @Test
    @DisplayName("addStock - non-positive amount")
    void addStock_nonPositiveAmount() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.addStock(0));
        assertThrows(IllegalArgumentException.class,
                () -> stock.addStock(-5));
    }

    @Test
    @DisplayName("addStock - exceeding maxCapacity ")
    void addStock_exceedingMaxCapacity() {
        assertThrows(IllegalStateException.class,
                () -> stock.addStock(51)); // 50 + 51 = 101 > 100
    }


    
    
    

    
    
    
    
    

    @Test
    @DisplayName("reserve - normal ")
    void reserve_normal_shouldWork() {
        int beforeReserved = stock.getReserved();
        int beforeAvailable = stock.getAvailable();

        stock.reserve(10);

        assertAll(
                () -> assertEquals(beforeReserved + 10, stock.getReserved()),
                () -> assertEquals(beforeAvailable - 10, stock.getAvailable())
        );
    }

    @Test
    @DisplayName("reserve - reserving exactly available is allowed")
    void reserve_exactAvailable_shouldWork() {
        int available = stock.getAvailable();
        stock.reserve(available);

        assertEquals(available, stock.getReserved());
        assertEquals(0, stock.getAvailable());
    }

    @Test
    @DisplayName("reserve - non-positive amount ")
    void reserve_nonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.reserve(0));
        assertThrows(IllegalArgumentException.class,
                () -> stock.reserve(-3));
    }

    @Test
    @DisplayName("reserve - amount greater than available")
    void reserve_greaterThanAvailable() {
        int available = stock.getAvailable();
        assertThrows(IllegalStateException.class,
                () -> stock.reserve(available + 1));
    }

    @Test
    @DisplayName("releaseReservation - normal")
    void releaseReservation_normal_shouldWork() {
        stock.reserve(20);
        int beforereleaseReserved = stock.getReserved();

        stock.releaseReservation(5);

        assertEquals(beforereleaseReserved - 5, stock.getReserved());
    }

    @Test
    @DisplayName("releaseReservation - non-positive amount")
    void releaseReservation_nonPositive() {
        stock.reserve(10);
        assertThrows(IllegalArgumentException.class,
                () -> stock.releaseReservation(0));
        assertThrows(IllegalArgumentException.class,
                () -> stock.releaseReservation(-1));
    }

    @Test
    @DisplayName("releaseReservation - releasing more than reserved ")
    void releaseReservation_moreThanReserved() {
        stock.reserve(10);
        assertThrows(IllegalStateException.class,
                () -> stock.releaseReservation(11));
    }

    
    
    
    
    
    
    
    
    
    
    

    @Test
    @DisplayName("shipReserved - normal")
    void shipReserved_normal() {
        stock.reserve(15);
        int beforeOnHand = stock.getOnHand();
        int beforeReserved = stock.getReserved();

        stock.shipReserved(10);

        assertAll(
                () -> assertEquals(beforeReserved - 10, stock.getReserved()),
                () -> assertEquals(beforeOnHand - 10, stock.getOnHand())
        );
    }

    @Test
    @DisplayName("shipReserved - non-positive amount")
    void shipReserved_nonPositive() {
        stock.reserve(10);
        assertThrows(IllegalArgumentException.class,
                () -> stock.shipReserved(0));
        assertThrows(IllegalArgumentException.class,
                () -> stock.shipReserved(-1));
    }

    @Test
    @DisplayName("shipReserved - amount greater than reserved ")
    void shipReserved_moreThanReserved() {
        stock.reserve(10);
        assertThrows(IllegalStateException.class,
                () -> stock.shipReserved(11));
    }

  
    
    
    
    
    
    
    

    @Test
    @DisplayName("removeDamaged - normal")
    void removeDamaged_normal() {
        stock.removeDamaged(10);
        assertEquals(40, stock.getOnHand());
    }

    @Test
    @DisplayName("removeDamaged - non-positive amount ")
    void removeDamaged_nonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.removeDamaged(0));
        assertThrows(IllegalArgumentException.class,
                () -> stock.removeDamaged(-1));
    }

    @Test
    @DisplayName("removeDamaged - amount greater than onHand")
    void removeDamaged_moreThanOnHand() {
        assertThrows(IllegalStateException.class,
                () -> stock.removeDamaged(51));
    }

    @Test
    @DisplayName("removeDamaged - reserved is capped to onHand if needed")
    void removeDamaged_reservedShouldNotExceedOnHand() {
        stock.reserve(30); // reserved = 30  onHand = 50
        stock.removeDamaged(40); // onHand = 10  /reserved يجب تصحيحه

        assertEquals(10, stock.getOnHand());
        assertEquals(10, stock.getReserved());
    }


    
    
    
    
    
    

    @Test
    @DisplayName("isReorderNeeded - returns false when available >= threshold")
    void isReorderNeeded_availableAboveThreshold() {
        assertFalse(stock.isReorderNeeded());
    }

    @Test
    @DisplayName("isReorderNeeded - returns true when available < threshold")
    void isReorderNeeded_availableBelowThreshold() {
        stock.reserve(45); // available = 5 < threshold = 10
        assertTrue(stock.isReorderNeeded());
    }

    @Test
    @Timeout(value = 1, unit = TimeUnit.SECONDS)
    @DisplayName("isReorderNeeded - executes quickly ")
    void isReorderNeeded_shouldBeFast() {
        assertDoesNotThrow(() -> stock.isReorderNeeded());
    }

  
    
   
    
    
    
    
    @Test
    @DisplayName("changeLocation")
    void changeLocation() {
    
        assertThrows(IllegalArgumentException.class,
                () ->  stock.changeLocation(""));
    }
    
    
    @Test
    @DisplayName("changeLocation v")
    void changeLocation_v() {
    stock.changeLocation("hhh");
    assertEquals("hhh", stock.getLocation());
        
    }
    
    
    

    @Test
    @DisplayName("updateReorderThreshold - valid value")
    void updateReorderThreshold_valid() {
        stock.updateReorderThreshold(20);
        assertEquals(20, stock.getReorderThreshold());
    }

    @Test
    @DisplayName("updateReorderThreshold - negative value ")
    void updateReorderThreshold_negative() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateReorderThreshold(-1));
    }

    @Test
    @DisplayName("updateReorderThreshold - value greater than maxCapacity should throw")
    void updateReorderThreshold_greaterThanMaxCapacity_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateReorderThreshold(101));
    }
    
    
    
    

    @Test
    @DisplayName("updateMaxCapacity - valid value ")
    void updateMaxCapacity_valid() {
        stock.updateReorderThreshold(80);
        stock.updateMaxCapacity(70); // threshold يجب أن ينقص إلى 70

        assertAll(
                () -> assertEquals(70, stock.getMaxCapacity()),
                () -> assertEquals(70, stock.getReorderThreshold())
        );
    }

    @Test
    @DisplayName("updateMaxCapacity - non-positive value ")
    void updateMaxCapacity_nonPositive() {
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateMaxCapacity(0));
        assertThrows(IllegalArgumentException.class,
                () -> stock.updateMaxCapacity(-1));
    }

    @Test
    @DisplayName("updateMaxCapacity - less than onHand")
    void updateMaxCapacity_lessThanOnHand() {
        assertThrows(IllegalStateException.class,
                () -> stock.updateMaxCapacity(40));
    }
    
    
    

  

    @Nested
    @DisplayName("When stock is newly initialized")
    class WhenStockInitialized {

        @Test
        @Tag("sanity")
        @DisplayName("available equals onHand when nothing is reserved")
        void availableEqualsOnHandInitially() {
            assertEquals(stock.getOnHand(), stock.getAvailable());
        }
    }

    @Test
    @Disabled()
    @DisplayName("future feature ")
    void future_feature() {
        fail("Not implemented yet");
    }
}

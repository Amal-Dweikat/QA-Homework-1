package test;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.junit.platform.suite.api.IncludeTags;

@Suite
@SuiteDisplayName("ProductStock Full Test Suite")
@IncludeTags({"sanity", "regression"}) 
@SelectClasses({
        ProductStockTest.class
})
public class ProductStockSuite {
  
}
